package com.rex50.mausam.storage.database.key_values

import androidx.room.*

@Dao
interface KeyValuesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keyValues: KeyValues?)

    @Update
    suspend fun update(keyValues: KeyValues?)

    @Query("DELETE FROM KeyValues WHERE `key` = :key")
    suspend fun delete(key: String?) : Void

    @Query("DELETE FROM KeyValues")
    suspend fun deleteAll()

    @Query("SELECT * from KeyValues where `key` = :key")
    fun getValue(key: String?): KeyValues?
}