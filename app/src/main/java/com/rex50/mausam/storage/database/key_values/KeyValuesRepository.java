package com.rex50.mausam.storage.database.key_values;

import android.content.Context;

import androidx.annotation.Nullable;

import com.rex50.mausam.storage.database.MausamRoomDatabase;

public class KeyValuesRepository {

    public static void insert(Context context, String key, String value){
        MausamRoomDatabase.databaseWriteExecutor.execute(() -> {
            MausamRoomDatabase.getDatabase(context).keyValuesDao().insert(new KeyValues(key, value));
        });
    }

    public static void update(Context context, String key, String value){
        MausamRoomDatabase.databaseWriteExecutor.execute(() -> {
            MausamRoomDatabase.getDatabase(context).keyValuesDao().update(new KeyValues(key, value));
        });
    }

    public static @Nullable String getValue(Context context, String key){
        return MausamRoomDatabase.getDatabase(context).keyValuesDao().getValue(key);
    }

    public static void delete(Context context, String key){
        MausamRoomDatabase.databaseWriteExecutor.execute(() -> {
            MausamRoomDatabase.getDatabase(context).keyValuesDao().delete(key);
        });
    }

}
