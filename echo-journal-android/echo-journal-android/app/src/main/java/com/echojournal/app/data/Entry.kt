package com.echojournal.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey
    val id: String,
    val type: EntryType,
    // For TEXT entries: the written text. For VOICE/IMAGE/VIDEO: an optional caption.
    val text: String? = null,
    // Absolute file path on device storage for voice/image/video entries.
    val mediaPath: String? = null,
    // Duration in milliseconds, only used for VOICE entries.
    val durationMs: Long = 0,
    // Auto-set at creation time, used for default sorting.
    val createdAt: Long,
    // Updated whenever the entry is edited.
    val updatedAt: Long,
    // Marks the entry as sent to Google Drive (used to know what still needs syncing).
    val synced: Boolean = false
)
