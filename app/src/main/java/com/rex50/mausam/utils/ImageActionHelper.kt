package com.rex50.mausam.utils

import android.app.WallpaperManager
import android.content.*
import android.content.Intent.*
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.network.APIManager
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.storage.database.key_values.KeyValuesRepository
import com.rex50.mausam.utils.ImageViewerHelper.Companion.getFormattedDesc
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.Extras
import kotlinx.coroutines.*
import java.io.*

class ImageActionHelper {
    companion object{

        const val TAG = "ImageActionHelper"

        private fun setWallpaper(context: Context?, path: String?){
            //TODO: work in progress
            context?.takeIf { path != null }?.apply {
                CoroutineScope(Dispatchers.IO).launch {
                    path?.toUri()?.asCompressedBitmap(context)?.let{
                        WallpaperManager.getInstance(context).setBitmap(it)
                        withContext(Dispatchers.Main){
                            context.showToast("Wallpaper set successfully")
                        }
                    }
                }
                //val finalUri: Uri? = Uri.parse(uri.toString().replace("file://", ""))
//                File(path!!).let {
//                    val options = BitmapFactory.Options()
//                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
//                    val bitmap = BitmapFactory.decodeFile(path, options)
//                    //TODO: compress bitmap before using
//                    bitmap?.let {
//                        WallpaperManager.getInstance(context).setBitmap(bitmap)
//                    }
////                    val intent = Intent(ACTION_ATTACH_DATA)
////                            .apply {
////                                addCategory(CATEGORY_DEFAULT)
////                                setDataAndType(finalUri, "image/*")
////                                putExtra("mimeType", "image/*")
////                                addFlags(FLAG_GRANT_READ_URI_PERMISSION)
////                                addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)
////                            }
////                    startActivity(createChooser(intent, "Set as:"))
//                }
            }
        }

        fun shareImage(context: Context?, subject: String, photographer: String, link: String){
            context?.apply {
                val body = getString(R.string.share_body, link, photographer)
                startActivity(
                        Intent(ACTION_SEND)
                                .setType("text/plain")
                                .putExtra(EXTRA_SUBJECT, subject)
                                .putExtra(EXTRA_TEXT, body)
                )
            }
        }

        fun deleteImage(context: Context?, unsplashPhotos: UnsplashPhotos, onResult: (Boolean) -> Unit) {
            context?.let { ctx ->
                val dBKey = unsplashPhotos.dbId

                val repo = KeyValuesRepository(ctx)

                CoroutineScope(Dispatchers.Main).launch {
                    val imageMeta = repo.getValue(dBKey)
                    if(!imageMeta.isNullOrEmpty()) {
                        //Delete from Storage if data is present
                        if(checkIfFileExistsInStorage(unsplashPhotos.relativePath)) {
                            val file = File(unsplashPhotos.relativePath ?: "")
                            file.apply {
                                try {
                                    if (this.exists())
                                        delete()

                                    //Delete from DB
                                    repo.delete(dBKey)

                                    onResult(true)

                                } catch (e: SecurityException) {
                                    Log.e(TAG, "deleteImage: ", e)
                                    onResult(false)
                                }
                            }
                        } else {
                            //Delete from DB
                            repo.delete(dBKey)
                            onResult(true)
                        }
                    } else {
                        onResult(false)
                    }
                }
            }
        }


        fun saveImage(context: Context?, unsplashPhotos: UnsplashPhotos, isAddToFav: Boolean, listener: ImageSaveListener?){
            context?.let { ctx ->

                val url = unsplashPhotos.urls.getSelectedQuality()

                val name = getFormattedDesc(unsplashPhotos.description, unsplashPhotos.altDescription)

                val postUrl = unsplashPhotos.links.downloadLocation

                var downloadTries = 0

                val dBKey = unsplashPhotos.dbId

                val repo = KeyValuesRepository(ctx)

                fun download(){

                    downloadTries++

                    fun prepareFile(): String?{
                        return if(ctx.isStoragePermissionGranted()) {
                            val file = File(unsplashPhotos.createRelativePath(name)) as File?
                            file?.apply {
                                file.parentFile?.mkdirs()
                                if (!file.exists())
                                    file.createNewFile()
                            }
                            file?.absolutePath
                        } else
                            null
                    }

                    prepareFile()?.let { localImagePath ->
                        val fetchConfiguration = FetchConfiguration.Builder(ctx)
                                .setDownloadConcurrentLimit(3)
                                .build()

                        val fetch = Fetch.Impl.getInstance(fetchConfiguration)

                        val connectionChecker = ConnectionChecker(ctx)

                        fetch.apply {
                            removeAllWithStatus(Status.DOWNLOADING)
                            removeAllWithStatus(Status.PAUSED)
                            removeAllWithStatus(Status.QUEUED)
                        }


                        var fetchListener: FetchListener? = null

                        fun removeListener() {
                            fetchListener?.let {
                                fetch.removeListener(it)
                            }
                        }

                        fun requestImage(imgRequest: Request){
                            imgRequest.enqueueAction = EnqueueAction.REPLACE_EXISTING
                            fetch.enqueue(imgRequest, {
                                CoroutineScope(Dispatchers.Main).launch {
                                    listener?.onDownloadStarted(it)
                                }
                            }, {
                                Log.e("ImageHelper", "download: ", it.throwable)
                                removeListener()
                                CoroutineScope(Dispatchers.Main).launch {
                                    listener?.onDownloadFailed(ctx.getString(R.string.no_storage_permission_msg))
                                }
                            })
                        }

                        val request = Request(url.second, localImagePath).also { req ->
                            req.priority = Priority.HIGH
                            req.extras = Extras(HashMap<String, String>().also {
                                it[Constants.IntentConstants.PHOTO_DATA] = unsplashPhotos.json
                                it[Constants.IntentConstants.NAME] = name
                                it[Constants.IntentConstants.IS_ADD_FAV] = isAddToFav.toString()
                            })
                        }

                        val downloadingTxt = ctx.getString(R.string.downloading, url.first.text)

                        fetchListener = object : AbstractFetchListener() {

                            override fun onCompleted(download: Download) {
                                removeListener()

                                fetch.close()

                                //Inform server that image is downloaded
                                postUrl?.takeIf { it.trim().isNotEmpty() }?.let {
                                    //TODO: remove manual object creation instead get as param
                                    UnsplashHelper(repo, APIManager.getInstance(ctx)!!, connectionChecker).trackDownload(it)
                                }

                                val extras = download.extras
                                val imgData = extras.map[Constants.IntentConstants.PHOTO_DATA] ?: ""
                                val imgName = extras.map[Constants.IntentConstants.NAME] ?: "picture"
                                val imgAddFav = extras.map[Constants.IntentConstants.IS_ADD_FAV]?.toBoolean() ?: false

                                UnsplashPhotos.getModelFromJSON(imgData)?.let {
                                    it.isFavorite = imgAddFav
                                    it.relativePath = it.createRelativePath(imgName)
                                    CoroutineScope(Dispatchers.Main).launch {
                                        listener?.response(it, if(imgAddFav) ctx.getString(R.string.added_to_fav) else ctx.getString(R.string.saved_to_downloads))
                                        repo.insert(dBKey, it.json)
                                    }
                                } ?: listener?.onDownloadFailed(ctx.getString(R.string.no_storage_permission_msg))
                            }

                            override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
                                Log.v("ImageHelper", "onProgress: " + download.progress)
                                listener?.onDownloadProgress("${download.progress}%\n$downloadingTxt")
                            }

                            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                                //Handle download error
                                if(downloadTries <= 3) {
                                    requestImage(request)
                                    listener?.onDownloadProgress("${download.progress}%\nProblem while downloading. Please wait retrying...")
                                } else {
                                    Log.e("ImageHelper", "onError: ", throwable)
                                    if (throwable != null) {
                                        FirebaseCrashlytics.getInstance().recordException(throwable)
                                    }
                                    removeListener()
                                    listener?.onDownloadFailed(ctx.getString(R.string.no_storage_permission_msg))
                                }
                            }

                            override fun onCancelled(download: Download) {
                                removeListener()
                            }

                            override fun onDeleted(download: Download) {
                                removeListener()
                            }
                        }

                        if(connectionChecker.isNetworkConnected()) {

                            fetch.addListener(fetchListener)

                            requestImage(request)

                        }

                    } ?: CoroutineScope(Dispatchers.Main).launch {
                        listener?.onDownloadFailed(ctx.getString(R.string.no_storage_permission_msg))
                    }

                }

                val scope = CoroutineScope(Dispatchers.IO)

                scope.launch {
                    checkIfAvailableOffline(repo, dBKey, { photo ->
                        scope.launch {
                            if(!photo.isFavorite && isAddToFav){

                                val value = photo.also { image ->
                                    image.isFavorite = true
                                }
                                repo.update(dBKey, value.json)
                                withContext(Dispatchers.Main) {
                                    listener?.response(value, ctx.getString(R.string.added_to_fav))
                                }

                            }else if(photo.isFavorite && isAddToFav){

                                val value = photo.also { image ->
                                    image.isFavorite = false
                                }

                                repo.update(dBKey, value.json)
                                withContext(Dispatchers.Main) {
                                    listener?.response(value, ctx.getString(R.string.removed_from_fav))
                                }

                            }else withContext(Dispatchers.Main) {

                                listener?.response(photo, ctx.getString(R.string.already_downloaded))

                            }
                        }
                    }, {
                        download()
                    })
                }

            }
        }

        suspend fun checkIfAvailableOffline(repo: KeyValuesRepository, dBKey: String, available:(UnsplashPhotos) -> Unit, notAvailable:() -> Unit) = withContext(Dispatchers.IO) {
            //check if data available in DB
            val response = repo.getValue(dBKey)

            response?.takeIf { it.isNotEmpty() }?.apply {

                val photo = UnsplashPhotos.getModelFromJSON(this)

                val uri: Uri? = photo?.let {
                    Uri.parse(photo.relativePath)
                }

                suspend fun reDownload() = with(Dispatchers.IO) {
                    repo.delete(dBKey)
                    notAvailable()
                }

                //check if data available in Storage
                if(checkIfFileExistsInStorage(photo?.relativePath)){
                    uri?.apply {
                        available(photo)
                    }?:
                    reDownload()
                }else{
                    reDownload()
                }
            }?: notAvailable()
        }

        private fun insertImage(cr: ContentResolver,
                                source: Bitmap?,
                                saveToPath: String,
                                title: String,
                                description: String?): Uri? {
            var url: Uri? = null
            val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues()
                values.apply {
                    put(MediaStore.Images.Media.TITLE, title)
                    put(MediaStore.Images.Media.DISPLAY_NAME, title)
                    put(MediaStore.Images.Media.DESCRIPTION, description)
                    put(MediaStore.Images.Media.MIME_TYPE, Constants.Image.SAVE_MIME_TYPE)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, saveToPath)
                    put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                    put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                }
                val imageUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                imageUri?.let {
                    url = it
                    cr.openOutputStream(imageUri)
                }
            } else {
                val imagesDir: String = Environment.getExternalStoragePublicDirectory(saveToPath).toString()
                val image: File? = File(imagesDir, title)
                image?.let {
                    if(!it.exists()){
                        it.parentFile?.mkdirs()
                        it.createNewFile()
                        //val exifInterface = ExifInterface(it)
                    }
                    url = Uri.fromFile(image)
                    FileOutputStream(image)
                }
            }
            fos?.apply {
                source?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                close()
            }








            /*val values = ContentValues()
            values.apply {
                put(MediaStore.Images.Media.TITLE, title)
                put(MediaStore.Images.Media.DISPLAY_NAME, title)
                put(MediaStore.Images.Media.DESCRIPTION, description)
                put(MediaStore.Images.Media.MIME_TYPE, Constants.Image.SAVE_MIME_TYPE)
                //put(MediaStore.MediaColumns.RELATIVE_PATH, saveToPath)
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
            var url: Uri? = null
            try {
                url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                source?.takeIf { url != null }?.apply {
                    cr.openOutputStream(url!!)?.apply {
                        use { outputStream ->
                            source.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        }
                        //val id: Long = ContentUris.parseId(url)
                        // Wait until MINI_KIND thumbnail is generated.
                        //val miniThumb: Bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null)
                        // This is for backward compatibility.
                        //storeThumbnail(cr, miniThumb, id, 50f, 50f, MediaStore.Images.Thumbnails.MICRO_KIND)
                    }
                }?: let {
                    url?.apply {
                        cr.delete(url!!, null, null)
                        url = null
                    }
                }
            } catch (e: java.lang.Exception) {
                url?.apply {
                    cr.delete(url!!, null, null)
                    url = null
                }
            }*/
            return url
        }

        //Use for backward compatibility
        //Use { MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null) } for creating thumbnail
        //eg.
        //val miniThumb: Bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null)
        //storeThumbnail(cr, miniThumb, id, 50f, 50f, MediaStore.Images.Thumbnails.MICRO_KIND)
        private fun storeThumbnail(
                cr: ContentResolver?,
                source: Bitmap,
                id: Long,
                width: Float,
                height: Float,
                kind: Int): Bitmap? {

            // create the matrix to scale it
            val matrix = Matrix()
            val scaleX = width / source.width
            val scaleY = height / source.height
            matrix.setScale(scaleX, scaleY)
            val thumb = Bitmap.createBitmap(source, 0, 0,
                    source.width,
                    source.height, matrix,
                    true
            )
            val values = ContentValues(4)
            values.apply {
                put(MediaStore.Images.Thumbnails.KIND, kind)
                put(MediaStore.Images.Thumbnails.IMAGE_ID, id.toInt())
                put(MediaStore.Images.Thumbnails.HEIGHT, thumb.height)
                put(MediaStore.Images.Thumbnails.WIDTH, thumb.width)
            }
            val url = cr?.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values)
            return try {
                val thumbOut = cr?.openOutputStream(url!!)
                thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut)
                thumbOut!!.close()
                thumb
            } catch (ex: FileNotFoundException) {
                null
            } catch (ex: IOException) {
                null
            }
        }
    }

    interface ImageSaveListener{
        fun onDownloadStarted(request: Request)
        fun onDownloadProgress(progress: String) {}
        fun onDownloadFailed(msg: String)
        fun response(imageMeta: UnsplashPhotos?, msg: String)
    }

    sealed class DownloadStatus {
        data class Started(val progress: Int): DownloadStatus()
        data class Downloading(val progress: String): DownloadStatus()
        data class Success(val downloadedData: UnsplashPhotos?, val msg: String = ""): DownloadStatus()
        data class Error(val msg: String): DownloadStatus()
    }

    abstract class ImageActionListener {

        open fun onSetWallpaper(photoInfo: UnsplashPhotos, name: String){}

        open fun onDownload(photoInfo: UnsplashPhotos, name: String){}

        open fun onFavourite(photoInfo: UnsplashPhotos, name: String){}

        open fun onShare(photoInfo: UnsplashPhotos, name: String){}

        open fun onDelete(photoInfo: UnsplashPhotos){}

        open fun onMore(){}

        open fun onUserPhotos(user: User) {}

    }
}