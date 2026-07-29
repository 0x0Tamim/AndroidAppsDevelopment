package com.echojournal.app.sync

import android.content.Context
import android.content.SharedPreferences
import com.echojournal.app.R
import com.echojournal.app.data.Entry
import com.echojournal.app.repository.EntryRepository
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Handles Google Drive sign-in and syncing journal entries + media to a Drive folder.
 *
 * Setup required (see strings.xml drive_web_client_id comment / README):
 * you need your own OAuth client IDs from Google Cloud Console for this to work.
 */
object DriveSyncManager {

    private const val DRIVE_SCOPE = "https://www.googleapis.com/auth/drive.file"
    private const val FOLDER_NAME = "EchoJournal"
    private const val BACKUP_FILE_NAME = "echo_journal_backup.json"
    private const val PREFS = "drive_sync_prefs"
    private const val KEY_FOLDER_ID = "folder_id"
    private const val KEY_BACKUP_FILE_ID = "backup_file_id"

    private val client = OkHttpClient()

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun buildSignInClient(context: Context): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DRIVE_SCOPE))
            .requestServerAuthCode(context.getString(R.string.drive_web_client_id))
            .build()
        return GoogleSignIn.getClient(context, options)
    }

    fun getSignedInAccount(context: Context): GoogleSignInAccount? =
        GoogleSignIn.getLastSignedInAccount(context)

    fun isSignedIn(context: Context): Boolean = getSignedInAccount(context) != null

    fun signOut(context: Context) {
        buildSignInClient(context).signOut()
        prefs(context).edit().clear().apply()
    }

    private suspend fun getAccessToken(context: Context, account: GoogleSignInAccount): String =
        withContext(Dispatchers.IO) {
            val androidAccount = account.account
                ?: throw IllegalStateException("Signed-in Google account has no underlying Account")
            GoogleAuthUtil.getToken(context, androidAccount, "oauth2:$DRIVE_SCOPE")
        }

    /** Full sync: uploads any not-yet-synced media files, then overwrites the JSON backup. */
    suspend fun syncNow(context: Context, repository: EntryRepository): Boolean = withContext(Dispatchers.IO) {
        val account = getSignedInAccount(context) ?: return@withContext false
        try {
            val token = getAccessToken(context, account)
            val folderId = ensureFolder(token)

            val unsynced = repository.getUnsynced()
            for (entry in unsynced) {
                if (!entry.mediaPath.isNullOrBlank()) {
                    val file = File(entry.mediaPath)
                    if (file.exists()) {
                        uploadMediaFile(token, folderId, file)
                    }
                }
                repository.markSynced(entry.id)
            }

            val all = repository.getAllOnce()
            uploadBackupJson(context, token, folderId, all)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun ensureFolder(token: String): String {
        val existing = findFile(token, name = FOLDER_NAME, mimeType = "application/vnd.google-apps.folder", parent = null)
        if (existing != null) return existing

        val metadata = JSONObject().apply {
            put("name", FOLDER_NAME)
            put("mimeType", "application/vnd.google-apps.folder")
        }
        val body = metadata.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://www.googleapis.com/drive/v3/files")
            .addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            val json = JSONObject(response.body?.string() ?: "{}")
            return json.getString("id")
        }
    }

    private fun findFile(token: String, name: String, mimeType: String, parent: String?): String? {
        val query = buildString {
            append("name = '${name.replace("'", "\\'")}' and mimeType = '$mimeType' and trashed = false")
            if (parent != null) append(" and '$parent' in parents")
        }
        val url = "https://www.googleapis.com/drive/v3/files?q=${java.net.URLEncoder.encode(query, "UTF-8")}&fields=files(id,name)"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            val json = JSONObject(response.body?.string() ?: "{}")
            val files = json.optJSONArray("files") ?: return null
            if (files.length() == 0) return null
            return files.getJSONObject(0).getString("id")
        }
    }

    private fun uploadMediaFile(token: String, folderId: String, file: File) {
        val mimeType = when (file.extension.lowercase()) {
            "m4a" -> "audio/mp4"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "mp4" -> "video/mp4"
            else -> "application/octet-stream"
        }
        val metadata = JSONObject().apply {
            put("name", file.name)
            put("parents", JSONArray().put(folderId))
        }
        val metadataBody = metadata.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val fileBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

        val multipart = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(MultipartBody.Part.create(
                okhttp3.Headers.headersOf("Content-Type", "application/json; charset=UTF-8"), metadataBody
            ))
            .addPart(MultipartBody.Part.create(
                okhttp3.Headers.headersOf("Content-Type", mimeType), fileBody
            ))
            .build()

        val request = Request.Builder()
            .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
            .addHeader("Authorization", "Bearer $token")
            .post(multipart)
            .build()
        client.newCall(request).execute().close()
    }

    private fun uploadBackupJson(context: Context, token: String, folderId: String, entries: List<Entry>) {
        val array = JSONArray()
        for (entry in entries) {
            array.put(JSONObject().apply {
                put("id", entry.id)
                put("type", entry.type.name)
                put("text", entry.text ?: JSONObject.NULL)
                put("mediaFileName", entry.mediaPath?.let { File(it).name } ?: JSONObject.NULL)
                put("durationMs", entry.durationMs)
                put("createdAt", entry.createdAt)
                put("updatedAt", entry.updatedAt)
            })
        }
        val backupJson = array.toString(2)
        val tempFile = File(context.cacheDir, BACKUP_FILE_NAME).apply { writeText(backupJson) }

        val existingId = prefs(context).getString(KEY_BACKUP_FILE_ID, null)
            ?: findFile(token, BACKUP_FILE_NAME, "application/json", folderId)

        val metadata = JSONObject().apply {
            put("name", BACKUP_FILE_NAME)
            if (existingId == null) put("parents", JSONArray().put(folderId))
        }
        val metadataBody = metadata.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val fileBody = tempFile.asRequestBody("application/json".toMediaTypeOrNull())

        val multipart = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(MultipartBody.Part.create(
                okhttp3.Headers.headersOf("Content-Type", "application/json; charset=UTF-8"), metadataBody
            ))
            .addPart(MultipartBody.Part.create(
                okhttp3.Headers.headersOf("Content-Type", "application/json"), fileBody
            ))
            .build()

        val url = if (existingId != null)
            "https://www.googleapis.com/upload/drive/v3/files/$existingId?uploadType=multipart"
        else
            "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart"

        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")

        val request = if (existingId != null) requestBuilder.patch(multipart).build()
        else requestBuilder.post(multipart).build()

        client.newCall(request).execute().use { response ->
            val json = JSONObject(response.body?.string() ?: "{}")
            json.optString("id", null)?.let {
                prefs(context).edit().putString(KEY_BACKUP_FILE_ID, it).apply()
            }
        }
        tempFile.delete()
    }
}