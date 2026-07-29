package com.echojournal.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    @Query("SELECT * FROM entries ORDER BY createdAt DESC")
    fun getAllByNewest(): Flow<List<Entry>>

    @Query("SELECT * FROM entries ORDER BY createdAt ASC")
    fun getAllByOldest(): Flow<List<Entry>>

    @Query("SELECT * FROM entries ORDER BY type ASC, createdAt DESC")
    fun getAllByType(): Flow<List<Entry>>

    @Query("SELECT * FROM entries WHERE synced = 0")
    suspend fun getUnsynced(): List<Entry>

    @Query("SELECT * FROM entries ORDER BY createdAt DESC")
    suspend fun getAllOnce(): List<Entry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Entry)

    @Update
    suspend fun update(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)

    @Query("UPDATE entries SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
