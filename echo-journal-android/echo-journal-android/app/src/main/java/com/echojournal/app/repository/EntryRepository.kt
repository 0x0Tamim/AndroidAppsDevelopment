package com.echojournal.app.repository

import com.echojournal.app.data.Entry
import com.echojournal.app.data.EntryDao
import kotlinx.coroutines.flow.Flow

enum class SortOption { NEWEST, OLDEST, TYPE }

class EntryRepository(private val dao: EntryDao) {

    fun observeEntries(sort: SortOption): Flow<List<Entry>> = when (sort) {
        SortOption.NEWEST -> dao.getAllByNewest()
        SortOption.OLDEST -> dao.getAllByOldest()
        SortOption.TYPE -> dao.getAllByType()
    }

    suspend fun addEntry(entry: Entry) = dao.insert(entry)

    suspend fun updateEntry(entry: Entry) = dao.update(entry)

    suspend fun deleteEntry(entry: Entry) = dao.delete(entry)

    suspend fun getUnsynced(): List<Entry> = dao.getUnsynced()

    suspend fun getAllOnce(): List<Entry> = dao.getAllOnce()

    suspend fun markSynced(id: String) = dao.markSynced(id)
}
