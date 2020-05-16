package com.rex50.mausam.storage.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rex50.mausam.storage.database.key_values.KeyValues;
import com.rex50.mausam.storage.database.key_values.KeyValuesDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {KeyValues.class}, version = 1, exportSchema = false)
public abstract class MausamRoomDatabase extends RoomDatabase {

    public abstract KeyValuesDao keyValuesDao();

    private static volatile MausamRoomDatabase INSTANCE;
    private static Context mContext;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MausamRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (MausamRoomDatabase.class){
                if(INSTANCE == null){
                    mContext = context;
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(), MausamRoomDatabase.class, "Mausam_Database")
                            //TODO: remove below line and find a better solution
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
