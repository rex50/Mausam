package com.rex50.mausam.utils

import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.atomic.AtomicLong

object FileUtils {

    fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (child in children) {
                    val success = deleteDir(File(dir, child))
                    if (!success) {
                        return false
                    }
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    fun folderSize(dir: File?): Long {
        var length: Long = 0
        try {
            if (dir != null) {
                //Check if device version is greater than API 25 then use newer apis available
                //else use reflections
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return folderSize(Paths.get(dir.path))
                }
                val files = dir.listFiles()
                if (files != null) {
                    for (file in files) {
                        length += if (file.isFile) file.length() else folderSize(file)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("folderSize", "Error : ", e)
        }
        return length
    }

    fun folderSize(path: Path): Long {
        val size = AtomicLong(0)
        try {
            //Check if device version is greater than API 25 then use newer apis available
            //else use reflections
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.walkFileTree(path, object : SimpleFileVisitor<Path?>() {
                    override fun visitFile(
                        file: Path?,
                        attrs: BasicFileAttributes?
                    ): FileVisitResult {
                        attrs?.size()?.let { size.addAndGet(it) }
                        return FileVisitResult.CONTINUE
                    }

                    override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
                        Log.e("folderSize", "skipped: $file ($exc)")
                        // Skip folders that can't be traversed
                        return FileVisitResult.CONTINUE
                    }

                    override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
                        if (exc != null) Log.e(
                            "folderSize",
                            "had trouble traversing: $dir ($exc)"
                        )
                        // Ignore errors traversing a folder
                        return FileVisitResult.CONTINUE
                    }
                })
            } else {
                size.addAndGet(folderSize(File(path.toString())))
            }
        } catch (e: IOException) {
            throw AssertionError("walkFileTree will not throw IOException if the FileVisitor does not")
        }
        return size.get()
    }

}