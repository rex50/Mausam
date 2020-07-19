package com.rex50.mausam.storage.database.key_values

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keyValues")
class KeyValues(
        @ColumnInfo(name = "key") @PrimaryKey val key: String,
        @ColumnInfo(name = "value") val value: String
)