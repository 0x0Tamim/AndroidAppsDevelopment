package com.echojournal.app.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object FileUtils {

    private fun mediaDir(context: Context): File {
        val dir = File(context.getExternalFilesDir(null), "media")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun newAudioFile(context: Context): File =
        File(mediaDir(context), "voice_${UUID.randomUUID()}.m4a")

    /** Copies a picked image/video Uri into app storage so it survives even if the
     *  original is removed from the gallery, and returns the new file's path. */
    fun copyUriToAppStorage(context: Context, uri: Uri, extension: String): String? {
        return try {
            val outFile = File(mediaDir(context), "media_${UUID.randomUUID()}.$extension")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
            outFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun deleteFile(path: String?) {
        if (path.isNullOrBlank()) return
        val file = File(path)
        if (file.exists()) file.delete()
    }
}
