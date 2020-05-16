package com.rex50.mausam.storage.database.key_values;

import android.content.Context;

import com.rex50.mausam.storage.database.MausamRoomDatabase;

public class KeyValuesRepository {

    public static void insert(Context context, String key, String value){
        MausamRoomDatabase.databaseWriteExecutor.execute(() -> {
            MausamRoomDatabase.getDatabase(context).keyValuesDao().insert(new KeyValues(key, value));
        });
    }

    public static String getValue(Context context, String key){
        return MausamRoomDatabase.getDatabase(context).keyValuesDao().getValue(key);
    }

}
