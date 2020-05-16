package com.rex50.mausam.storage.database.key_values;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "keyValues")
public class KeyValues {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "key")
    private String key;

    @NonNull
    @ColumnInfo(name = "value")
    private String value;

    public KeyValues(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey(){
        return this.key;
    }

    public String getValue(){
        return this.value;
    }

}
