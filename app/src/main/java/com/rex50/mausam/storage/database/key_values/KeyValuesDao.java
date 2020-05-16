package com.rex50.mausam.storage.database.key_values;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface KeyValuesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(KeyValues keyValues);

    @Query("DELETE FROM KeyValues")
    void deleteAll();

    @Query("SELECT value from KeyValues where `key` = :key")
    String getValue(String key);

}
