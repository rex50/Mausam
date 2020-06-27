package com.rex50.mausam.storage.database.key_values;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface KeyValuesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(KeyValues keyValues);

    @Update
    void update(KeyValues keyValues);

    @Query("DELETE FROM KeyValues WHERE `key` = :key")
    void delete(String key);

    @Query("DELETE FROM KeyValues")
    void deleteAll();

    @Query("SELECT value from KeyValues where `key` = :key")
    String getValue(String key);

}
