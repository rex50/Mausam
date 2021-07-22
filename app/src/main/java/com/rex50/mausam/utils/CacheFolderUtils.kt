package com.rex50.mausam.utils

import android.content.Context
import android.util.Log
import com.rex50.mausam.utils.FileUtils.deleteDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object CacheFolderUtils {

    fun getAppCacheDir(context: Context?): File? {
        var dir: File? = null
        if (context != null) dir = context.cacheDir
        return dir
    }


    suspend fun getCacheFolderSize(context: Context?): Long = withContext(Dispatchers.IO) {
        return@withContext FileUtils.folderSize(getAppCacheDir(context))
    }

    suspend fun deleteCache(context: Context?): Boolean = withContext(Dispatchers.IO) {
        try {
            if (context != null) {
                val dir = getAppCacheDir(context)
                return@withContext deleteDir(dir)
            }
        } catch (e: Exception) {
            Log.e("deleteCache", "Error : ", e)
        }
        return@withContext false
    }

}