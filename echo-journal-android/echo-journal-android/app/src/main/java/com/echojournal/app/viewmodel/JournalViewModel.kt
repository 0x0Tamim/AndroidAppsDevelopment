package com.echojournal.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.echojournal.app.data.AppDatabase
import com.echojournal.app.data.Entry
import com.echojournal.app.data.EntryType
import com.echojournal.app.repository.EntryRepository
import com.echojournal.app.repository.SortOption
import com.echojournal.app.sync.DriveSyncManager
import com.echojournal.app.sync.DriveSyncWorker
import com.echojournal.app.util.FileUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EntryRepository(AppDatabase.getInstance(application).entryDao())

    private val _sortOption = MutableStateFlow(SortOption.NEWEST)
    val sortOption: StateFlow<SortOption> = _sortOption

    val entries: StateFlow<List<Entry>> = _sortOption
        .flatMapLatest { repository.observeEntries(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isDriveSignedIn = MutableStateFlow(DriveSyncManager.isSignedIn(application))
    val isDriveSignedIn: StateFlow<Boolean> = _isDriveSignedIn

    init {
        DriveSyncWorker.schedulePeriodic(application)
    }

    fun setSort(option: SortOption) {
        _sortOption.value = option
    }

    fun addTextEntry(text: String) {
        val now = System.currentTimeMillis()
        val entry = Entry(
            id = UUID.randomUUID().toString(),
            type = EntryType.TEXT,
            text = text,
            createdAt = now,
            updatedAt = now
        )
        save(entry)
    }

    fun addVoiceEntry(mediaPath: String, durationMs: Long, caption: String? = null) {
        val now = System.currentTimeMillis()
        val entry = Entry(
            id = UUID.randomUUID().toString(),
            type = EntryType.VOICE,
            text = caption,
            mediaPath = mediaPath,
            durationMs = durationMs,
            createdAt = now,
            updatedAt = now
        )
        save(entry)
    }

    fun addImageEntry(mediaPath: String, caption: String? = null) {
        val now = System.currentTimeMillis()
        val entry = Entry(
            id = UUID.randomUUID().toString(),
            type = EntryType.IMAGE,
            text = caption,
            mediaPath = mediaPath,
            createdAt = now,
            updatedAt = now
        )
        save(entry)
    }

    fun addVideoEntry(mediaPath: String, caption: String? = null) {
        val now = System.currentTimeMillis()
        val entry = Entry(
            id = UUID.randomUUID().toString(),
            type = EntryType.VIDEO,
            text = caption,
            mediaPath = mediaPath,
            createdAt = now,
            updatedAt = now
        )
        save(entry)
    }

    fun updateEntryText(entry: Entry, newText: String) {
        viewModelScope.launch {
            repository.updateEntry(entry.copy(text = newText, updatedAt = System.currentTimeMillis(), synced = false))
            triggerSync()
        }
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
            FileUtils.deleteFile(entry.mediaPath)
        }
    }

    private fun save(entry: Entry) {
        viewModelScope.launch {
            repository.addEntry(entry)
            triggerSync()
        }
    }

    fun triggerSync() {
        DriveSyncWorker.syncOnceNow(getApplication())
    }

    fun refreshDriveSignInState() {
        _isDriveSignedIn.value = DriveSyncManager.isSignedIn(getApplication())
    }
}
