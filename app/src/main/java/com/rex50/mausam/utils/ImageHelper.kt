package com.rex50.mausam.utils

import android.content.*
import android.content.Intent.*
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.storage.database.key_values.KeyValuesRepository
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.imageviewer.loader.ImageLoader
import com.thekhaeng.pushdownanim.PushDownAnim
import com.transitionseverywhere.extra.Scale
import org.apache.commons.lang3.StringUtils
import java.io.*


class ImageViewerHelper (){

    private var context: Context? = null
    private var selectedPhotoPos = 0
    private var animatePhotographer = true
    private var showPhotographer = true

    private lateinit var photosList: List<UnsplashPhotos>
    private var childImgView: ImageView? = null
    private var childPos: Int = 0

    private var dialogLayout: View? = null
    private var preLoaderAnimLayout: View? = null

    private var lnlOtherButtons: LinearLayout? = null
    private var lnlExtraInfo: LinearLayout? = null
    private var rlUserInfo: RelativeLayout? = null
    private var rlImageOverlay: RelativeLayout? = null

    private var btnSetWall: ImageView? = null
    private var btnDownload: ImageView? = null
    private var btnFav: ImageView? = null
    private var btnShare: ImageView? = null
    private var btnMore: ImageView? = null

    private var tvDesc: TextView? = null
    private var tvCreated: TextView? = null
    private var tvColor: TextView? = null
    private var tvLikes: TextView? = null
    private var tvHeight: TextView? = null
    private var tvWidth: TextView? = null
    
    private var btnUserPortfolio: Button? = null
    private var btnUserInstagram: Button? = null
    private var btnUserTwitter: Button? = null
    private var btnUserMorePhotos: Button? = null
    private var btnUserVisitThisPage: Button? = null

    private var cardPhotographerImg: CardView? = null
    private var ivPhotographer: ImageView? = null
    private var tvPhotographerName: TextView? = null
    private var lnlUserButtons: LinearLayout? = null
    private var preLoader: LottieAnimationView? = null
    private var animError: LottieAnimationView? = null
    private var lnlError: LinearLayout? = null

    private var imageViewer: StfalconImageViewer<UnsplashPhotos>? = null

    constructor(context: Context?) : this() {
        this.context = context
        initLayout(context)
    }

    private fun initLayout(context: Context?) {
        context?.apply {

            preLoaderAnimLayout = View.inflate(this, R.layout.anim_view, null)
            preLoaderAnimLayout?.apply {
                preLoader = findViewById(R.id.preLoader)
                animError = findViewById(R.id.animError)
                lnlError = findViewById(R.id.lnlError)
            }

            dialogLayout = View.inflate(this, R.layout.overlay_full_screen_image, null)
            dialogLayout?.apply {
                lnlOtherButtons = findViewById(R.id.lnlOtherButtons)
                lnlExtraInfo = findViewById(R.id.lnlExtraInfo)
                rlUserInfo = findViewById(R.id.rlUserInfo)
                rlImageOverlay = findViewById(R.id.rlImageOverlay)

                btnSetWall = findViewById(R.id.btnSetWallpaper)
                btnDownload = findViewById(R.id.btnDownloadImage)
                btnFav = findViewById(R.id.btnFavImage)
                btnShare = findViewById(R.id.btnShareImage)
                btnMore = findViewById(R.id.btnMoreAboutImage)

                tvDesc = findViewById(R.id.tvPhotoDesc)
                tvCreated = findViewById(R.id.tvPhotoCreated)
                tvColor = findViewById(R.id.tvPhotoColor)
                tvLikes = findViewById(R.id.tvPhotoLikes)
                tvHeight = findViewById(R.id.tvPhotoHeight)
                tvWidth = findViewById(R.id.tvPhotoWidth)

                btnUserInstagram = findViewById(R.id.btnUserInstagram)
                btnUserPortfolio = findViewById(R.id.btnUserPortfolio)
                btnUserTwitter = findViewById(R.id.btnUserTwitter)
                btnUserMorePhotos = findViewById(R.id.btnUserMorePhotos)
                btnUserVisitThisPage = findViewById(R.id.btnUserVisitThisPage)

                cardPhotographerImg = findViewById(R.id.cardPhotographerImg)
                ivPhotographer = findViewById(R.id.ivPhotographer)
                tvPhotographerName = findViewById(R.id.tvPhotographerName)
                lnlUserButtons = findViewById(R.id.lnlUserButtons)
            }
        }
    }

    fun with(photosList: List<UnsplashPhotos>, childImgView: ImageView? = null, childPos: Int = 0, actionListener: ImageActionListener? = null) : ImageViewerHelper {
        context?.apply {
            selectedPhotoPos = childPos
            animatePhotographer = true

            this@ImageViewerHelper.photosList = photosList
            this@ImageViewerHelper.childPos = childPos
            this@ImageViewerHelper.childImgView = childImgView

            initClicks(actionListener)

            preLoader?.apply {
                scale = 0.5F
                speed = 0.8F
            }
            animError?.apply {
                setAnimation(R.raw.error_lochness_monster)
                scale = 0.2F
                speed = 0.8F
            }

            imageViewer = getViewer(this)
        }
        return this
    }

    fun showPhotographer(showPhotographer: Boolean) : ImageViewerHelper{
        this.showPhotographer = showPhotographer
        return this
    }

    fun show(){
        photosList[childPos].apply {
            bindPhotographerInfo(this)
            bindImageInfo(this)
        }
        imageViewer?.show()
    }

    private fun getViewer(context: Context?): StfalconImageViewer<UnsplashPhotos>? {

        val imageViewerBuilder = StfalconImageViewer.Builder(context, photosList,
                ImageLoader { imageView: ImageView?, unsplashPhotos: UnsplashPhotos ->
                    lnlError?.hideView()
                    preLoader?.showView()
                    imageView?.apply {
                        Glide.with(this)
                                .load(unsplashPhotos.urls.regular)
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .addListener(object: RequestListener<Drawable>{
                                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                        preLoader?.hideView()
                                        lnlError?.showView()
                                        return false
                                    }

                                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                        preLoader?.hideView()
                                        lnlError?.hideView()
                                        return false
                                    }

                                })
                                .into(this)
                    }
                })
                .withHiddenStatusBar(true)
                .withStartPosition(childPos)
                .withBackgroundColorResource(R.color.white_to_black)
                .withOverlayView(dialogLayout)
                .withBackgroundView(preLoaderAnimLayout)
                .withImageChangeListener {
                    selectedPhotoPos = it
                    photosList[selectedPhotoPos].apply {
                        bindPhotographerInfo(this)
                        bindImageInfo(this)
                    }
                }
        childImgView?.apply{
            imageViewerBuilder?.withTransitionFrom(this)
        }
        return imageViewerBuilder?.build()
    }

    private fun initClicks(actionListener: ImageActionListener? = null) {
        context?.apply {
            PushDownAnim.setPushDownAnimTo(btnSetWall, btnDownload, btnFav, btnShare, btnMore, rlUserInfo,
                    btnUserPortfolio, btnUserInstagram, btnUserTwitter, btnUserMorePhotos, btnUserVisitThisPage)
                    .setScale(0.9F)
                    .setOnClickListener { v: View ->
                        when (v.id) {

                            R.id.btnSetWallpaper -> {
                                photosList[selectedPhotoPos].apply {
                                    val name = getFormattedDesc(description, altDescription)
                                    //if(name.length > 20) name = name.substring(0, 20)
                                    actionListener?.onSetWallpaper(this, name)
                                }
                            }

                            R.id.btnDownloadImage -> {
                                photosList[selectedPhotoPos].apply {
                                    val name = getFormattedDesc(description, altDescription)
                                    //if(name.length > 20) name = name.substring(0, 20)
                                    actionListener?.onDownload(this, name)
                                }
                            }

                            R.id.btnFavImage -> {
                                //btnFav?.setColorFilter(ContextCompat.getColor(this, R.color.satinRed))
                                photosList[selectedPhotoPos].apply {
                                    val name = getFormattedDesc(description, altDescription)
                                    //if(name.length > 20) name = name.substring(0, 20)
                                    actionListener?.onFavourite(this, name)
                                }
                            }

                            R.id.btnShareImage -> {
                                photosList[selectedPhotoPos].apply {
                                    val name = getFormattedDesc(description, altDescription)
                                    //if(name.length > 20) name = name.substring(0, 20)
                                    actionListener?.onShare(this, name)
                                }
                            }

                            R.id.btnMoreAboutImage -> {
                                actionListener?.onMore()
                                lnlExtraInfo?.apply {
                                    startSimpleTransition()
                                    if(isViewVisible()) {
                                        animatePhotographer = true
                                        if(showPhotographer)
                                            rlUserInfo?.showView()
                                        hideView()
                                        //lnlOtherButtons?.showView()
                                        startSimpleTransition()
                                        btnMore?.setImageDrawable(getDrawable(R.drawable.ic_more_vertical))
                                    }else {
                                        animatePhotographer = false
                                        rlUserInfo?.hideView()
                                        showView()
                                        //lnlOtherButtons?.hideView()
                                        startSimpleTransition()
                                        btnMore?.setImageDrawable(getDrawable(R.drawable.ic_close))
                                    }
                                }
                            }

                            R.id.rlUserInfo -> {
                                startSimpleTransition()
                                lnlUserButtons?.apply {
                                    if(isViewVisible()) hideView() else showView()
                                }
                            }

                            R.id.btnUserPortfolio -> {
                                openUrl(photosList[selectedPhotoPos].user.portfolioUrl)
                                rlUserInfo?.performClick()
                            }

                            R.id.btnUserInstagram -> {
                                openInstagramProfile(photosList[selectedPhotoPos].user.instagramUsername)
                                rlUserInfo?.performClick()
                            }

                            R.id.btnUserTwitter -> {
                                openTwitterProfile(photosList[selectedPhotoPos].user.twitterUsername)
                                rlUserInfo?.performClick()
                            }

                            R.id.btnUserMorePhotos -> {
                                photosList[selectedPhotoPos].user?.apply {
                                    actionListener?.onUserPhotos(this)
                                }
                                rlUserInfo?.performClick()
                            }

                            R.id.btnUserVisitThisPage -> {
                                openUrl(photosList[selectedPhotoPos].links.html)
                                rlUserInfo?.performClick()
                            }
                        }
                    }
        }
    }

    private fun bindImageInfo(photo: UnsplashPhotos){
        photo.apply {
            getFormattedDesc(description?.toString(), altDescription?.toString()).takeIf { it.isNotEmpty() }?.apply {
                tvDesc?.showView()
                tvDesc?.text = this
            }?: tvDesc?.hideView()
            tvCreated?.text = getFormattedDate(createdAt.toDateFormat("dd-MM-YYY"))
            tvColor?.text = getFormattedColor(color.toString())
            tvLikes?.text = getFormattedLikes(likes.toString())
            tvHeight?.text = getFormattedHeight(height.toString())
            tvWidth?.text = getFormattedWidth(width.toString())
        }
    }

    private fun bindPhotographerInfo(photo: UnsplashPhotos){
        rlUserInfo?.hideView()
        if(showPhotographer) {
            if (animatePhotographer)
                startFadeTransition()
            photo.user.apply {
                ivPhotographer?.loadImage(profileImage.medium)
                tvPhotographerName?.text = name
                btnUserPortfolio?.apply {
                    if (portfolioUrl.isNullOrEmpty()) hideView() else showView()
                }
                btnUserInstagram?.apply {
                    if (instagramUsername.isNullOrEmpty()) hideView() else showView()
                }
                btnUserTwitter?.apply {
                    if (twitterUsername.isNullOrEmpty()) hideView() else showView()
                }
                btnUserVisitThisPage?.apply {
                    if (photo.links.html.isNullOrEmpty()) hideView() else showView()
                }
                if (animatePhotographer)
                    rlUserInfo?.showView()
            }
        }
    }

    private fun openTwitterProfile(twitterUsername: String) {
        context?.apply {
            try {
                startActivity(Intent(ACTION_VIEW, Uri.parse("twitter://user?screen_name=$twitterUsername")))
            } catch (e: ActivityNotFoundException) {
                openUrl("https://twitter.com/#!/$twitterUsername")
            }
        }
    }

    private fun openInstagramProfile(userName: String) {
        context?.apply {
            try {
                startActivity(Intent(ACTION_VIEW, Uri.parse("http://instagram.com/_u/$userName")).setPackage("com.instagram.android"))
            } catch (e: ActivityNotFoundException) {
                openUrl("http://instagram.com/$userName")
            }
        }
    }

    private fun openUrl(url: String){
        context?.apply {
            startActivity(Intent(ACTION_VIEW, Uri.parse(url)))
        }
    }

    private fun getFormattedLikes(likes: String) : String = likes.addBefore(context?.getString(R.string.likes))

    private fun getFormattedHeight(height: String) : String = height.addBefore(context?.getString(R.string.height)) + Constants.Units.PX

    private fun getFormattedWidth(width: String) : String = width.addBefore(context?.getString(R.string.width)) + Constants.Units.PX

    private fun getFormattedDesc(desc: String?, altDesc: String?): String = let {
        desc?.takeIf { it.isEmpty() }?.apply {
            return@let StringUtils.capitalize(this)
        }?: altDesc?.takeIf { it.isNotEmpty() }?.apply {
            return@let StringUtils.capitalize(this)
        }?: ""
    }

    private fun getFormattedDate(date: String): String? = date.addBefore(context?.getString(R.string.created_on))

    private fun getFormattedColor(color: String): String? = color.addBefore(context?.getString(R.string.color))

    private fun startSimpleTransition(){
        val transition: Transition = ChangeBounds()
        transition.duration = 300
        transition.interpolator = FastOutSlowInInterpolator()
        transition.startDelay = 200
        TransitionManager.beginDelayedTransition(rlImageOverlay as ViewGroup, transition)
    }

    private fun startFadeTransition(){
        TransitionManager.beginDelayedTransition(rlImageOverlay as ViewGroup, Fade())
    }

    private fun startScaleTransition(){
        TransitionManager.beginDelayedTransition(rlImageOverlay as ViewGroup, Scale())
    }

    abstract class ImageActionListener {

        open fun onSetWallpaper(photoInfo: UnsplashPhotos, name: String){}

        open fun onDownload(photoInfo: UnsplashPhotos, name: String){}

        open fun onFavourite(photoInfo: UnsplashPhotos, name: String){}

        open fun onShare(photoInfo: UnsplashPhotos, name: String){}

        open fun onMore(){}

        open fun onUserPhotos(user: User) {}

    }
}

class ImageActionHelper {
    companion object{
        fun setWallpaper(context: Context?, uri: Uri?){
            context?.takeIf { uri != null }?.apply {
                val finalUri: Uri? = Uri.parse(uri.toString().replace("file://", ""))
                File(finalUri?.path!!)?.let {file ->
                    val intent = Intent(ACTION_ATTACH_DATA)
                            .apply {
                                addCategory(CATEGORY_DEFAULT)
                                setDataAndType(finalUri, "image/*")
                                putExtra("mimeType", "image/*")
                                addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                                addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)
                            }
                    startActivity(createChooser(intent, "Set as:"))
                }
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

        fun saveImage(context: Context?, url: String, name: String, desc: String, isAddToFav: Boolean, listener: ImageSaveListener?){
            context?.let {
                val dBKey = Constants.Image.DOWNLOAD_RELATIVE_PATH+name
                val nameWithExtension = Constants.Image.JPEG_NAME_PATTERN.format(name)

                fun download(){
                    listener?.onDownloadStarted()
                    Glide.with(context)
                            .asBitmap()
                            .load(url)
                            .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                            .into(object: CustomTarget<Bitmap>(){
                                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                                    val path = insertImage(context.contentResolver,
                                            resource,
                                            Constants.Image.DOWNLOAD_RELATIVE_PATH,
                                            nameWithExtension,
                                            desc)

                                    path?.apply {
                                        /*val msg = if (isAddToFav) it.getString(R.string.saved_to_fav) else it.getString(R.string.saved_to_downloads)
                                        listener?.response(path, msg)*/
                                        val imageMeta = SavedImageMeta(name, name, SavedImageMeta.createRelPath(name), ".jpg", isAddToFav)
                                        imageMeta.setUri(path)
                                        listener?.response(imageMeta, it.getString(R.string.saved_to_downloads))
                                        KeyValuesRepository.insert(context, dBKey, imageMeta.getJSON())
                                    }?: listener?.response(null, it.getString(R.string.failed_to_download_no_space))
                                }

                                override fun onLoadFailed(errorDrawable: Drawable?) {
                                    listener?.onDownloadFailed()
                                    super.onLoadFailed(errorDrawable)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                    //Use when required
                                }

                            })
                }

                //check if data available in DB
                val response = KeyValuesRepository.getValue(it, dBKey)

                response?.apply {

                    val imageMeta = SavedImageMeta.getModelFromJSON(this)

                    val uri: Uri? = Uri.parse(imageMeta?.relativePath)

                    fun checkIfFileExistsInStorage(): Boolean {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val retCol = arrayOf(MediaStore.MediaColumns.TITLE, MediaStore.MediaColumns.RELATIVE_PATH)
                            var cId: String?
                            imageMeta?.getUri().toString()?.split("/").apply {
                                cId = this?.get(lastIndex)
                            }
                            context.contentResolver.query(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    retCol,
                                    MediaStore.MediaColumns._ID + "='" + cId + "'", null, null
                            )?.use { cur ->
                                if (cur.count == 0) {
                                    return false
                                }
                                cur.moveToFirst()
                                val id = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns.TITLE))
                                id?.takeIf { isNotEmpty() }?.apply {
                                    return true
                                }
                            }
                        }else{
                            val file: File? = File(imageMeta?.relativePath!!)
                            file?.apply {
                                if(this.exists())
                                    return true
                            }
                        }
                        return false
                    }

                    fun reDownload(){
                        KeyValuesRepository.delete(context, dBKey)
                        download()
                    }

                    //check if data available in Storage
                    if(checkIfFileExistsInStorage()){
                        uri?.apply {
                            if(imageMeta?.isFavorite == false && isAddToFav){
                                val value = imageMeta.also {image ->
                                    image.isFavorite = true
                                }
                                KeyValuesRepository.update(it, dBKey, value.getJSON())
                                listener?.response(value, it.getString(R.string.added_to_fav))
                            }else if(imageMeta?.isFavorite == true && isAddToFav){
                                val value = imageMeta.also {image ->
                                    image.isFavorite = false
                                }
                                KeyValuesRepository.update(it, dBKey, value.getJSON())
                                listener?.response(value, it.getString(R.string.removed_from_fav))
                            }else
                                listener?.response(imageMeta, it.getString(R.string.already_downloaded))
                        }?: reDownload()
                    }else{
                        reDownload()
                    }
                }?: download()
            }
        }

        private fun insertImage(cr: ContentResolver,
                                source: Bitmap?,
                                saveToPath: String,
                                title: String,
                                description: String?): Uri? {
            var url: Uri? = null
            val fos: OutputStream?
            fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
        fun onDownloadStarted()
        fun onDownloadFailed()
        fun response(imageMeta: SavedImageMeta?, msg: String)
    }
}

data class SavedImageMeta(
        @SerializedName("imgName")
        var name: String,

        @SerializedName("imgDesc")
        var desc: String? = null,

        @SerializedName("imgRelPath")
        var relativePath: String,

        @SerializedName("imgExt")
        var extension: String,

        @SerializedName("imgIsFav")
        var isFavorite: Boolean = false,

        @SerializedName("fileUri")
        private var uri: String = ""
): Serializable {
    companion object{
        fun getModelFromJSON(jsonObject: String): SavedImageMeta?{
            return Gson().fromJson(jsonObject, SavedImageMeta::class.java)
        }

        fun createRelPath(filename: String): String = "/storage/emulated/0/Pictures/Mausam/Downloads/%s.jpg".format(filename)
    }

    fun getUri() = Uri.parse(uri)

    fun setUri(uri: Uri){
        this.uri = uri.toString()
    }

    fun getJSON(): String{
        return Gson().toJson(this)
    }
}