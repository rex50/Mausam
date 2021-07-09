package com.rex50.mausam.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
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
import com.bumptech.glide.request.target.Target
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.stfalcon.imageviewer.StfalconImageViewer
import com.thekhaeng.pushdownanim.PushDownAnim
import org.apache.commons.lang3.StringUtils

class ImageViewerHelper (){

    private var isDataSaverMode = false
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
    private var btnRightSwipe: FrameLayout? = null
    private var btnLeftSwipe: FrameLayout? = null

    private var btnSetWall: ImageView? = null
    private var btnDownload: ImageView? = null
    private var btnFav: ImageView? = null
    private var btnShare: ImageView? = null
    private var btnDelete: ImageView? = null
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

    private val defaultTools = arrayListOf(Tools.SET_WALLPAPER, Tools.DOWNLOAD_PHOTO, Tools.SHARE_PHOTO, Tools.MORE)

    private var toolsToShow = defaultTools

    var onDismissListener: (() -> Unit)? = null

    var onPageChangeListener: ((Int) -> Unit)? = null

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
                btnRightSwipe = findViewById(R.id.btnRightSwipe)
                btnLeftSwipe = findViewById(R.id.btnLeftSwipe)

                btnSetWall = findViewById(R.id.btnSetWallpaper)
                btnDownload = findViewById(R.id.btnDownloadImage)
                btnFav = findViewById(R.id.btnFavImage)
                btnShare = findViewById(R.id.btnShareImage)
                btnDelete = findViewById(R.id.btnDelete)
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

    fun with(photosList: List<UnsplashPhotos>, childImgView: ImageView? = null, childPos: Int = 0, actionListener: ImageActionHelper.ImageActionListener? = null) : ImageViewerHelper {
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
                setAnimation(R.raw.l_anim_error_lochness_monster)
                scale = 0.2F
                speed = 0.8F
            }

            imageViewer = getViewer(this)

            handleNavBtnVisibility(childPos, photosList.lastIndex)

            btnLeftSwipe?.setOnClickListener{
                handleNavBtnVisibility(--selectedPhotoPos, photosList.lastIndex)
                imageViewer?.setCurrentPosition(selectedPhotoPos)
            }

            btnRightSwipe?.setOnClickListener {
                handleNavBtnVisibility(++selectedPhotoPos, photosList.lastIndex)
                imageViewer?.setCurrentPosition(selectedPhotoPos)
            }
        }
        return this
    }

    fun showPhotographer(showPhotographer: Boolean) : ImageViewerHelper{
        this.showPhotographer = showPhotographer
        return this
    }

    fun setDataSaverMode(isDataSaverMode: Boolean): ImageViewerHelper{
        this.isDataSaverMode = isDataSaverMode
        return this
    }

    fun setTools(toolsToShow: ArrayList<Tools>): ImageViewerHelper {
        this.toolsToShow.clear()
        this.toolsToShow.addAll(toolsToShow)
        return this
    }

    fun updateImages(photos: ArrayList<UnsplashPhotos>) {
        photosList = photos
        imageViewer?.updateImages(photosList)
        handleNavBtnVisibility(imageViewer?.currentPosition() ?: childPos, photosList.lastIndex)
    }

    fun show(){
        photosList[childPos].apply {
            bindPhotographerInfo(this)
            bindImageInfo(this)
        }
        handleNavBtnVisibility(selectedPhotoPos, photosList.lastIndex)
        updateToolsVisibility()
        imageViewer?.show()
    }

    private fun updateToolsVisibility() {
        toolsToShow.forEach {
            when (it) {
                Tools.SET_WALLPAPER -> btnSetWall?.showView()
                Tools.DOWNLOAD_PHOTO -> btnDownload?.showView()
                Tools.FAV_PHOTO -> btnFav?.showView()
                Tools.SHARE_PHOTO -> btnShare?.showView()
                Tools.MORE -> btnMore?.showView()
                Tools.DELETE -> btnDelete?.showView()
            }
        }
    }

    private fun getViewer(context: Context?): StfalconImageViewer<UnsplashPhotos>? {

        val imageViewerBuilder = StfalconImageViewer.Builder(context, photosList)
        { imageView: ImageView?, unsplashPhotos: UnsplashPhotos ->
            lnlError?.hideView()
            preLoader?.showView()
            imageView?.apply {
                Glide.with(this)
                    .load(unsplashPhotos.urls.getRegular(isDataSaverMode))
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            preLoader?.hideView()
                            lnlError?.showView()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            preLoader?.hideView()
                            lnlError?.hideView()
                            return false
                        }

                    })
                    .into(this)
            }
        }
        .withHiddenStatusBar(true)
        .withStartPosition(childPos)
        .withBackgroundColorResource(R.color.white_to_black)
        .withOverlayView(dialogLayout)
        .withBackgroundView(preLoaderAnimLayout)
        .withDismissListener {
            onDismissListener?.invoke()
        }
        .withImageChangeListener {
            selectedPhotoPos = it
            onPageChangeListener?.invoke(selectedPhotoPos)
            handleNavBtnVisibility(selectedPhotoPos, photosList.lastIndex)
            photosList[selectedPhotoPos].apply {
                bindPhotographerInfo(this)
                bindImageInfo(this)
            }
        }

        childImgView?.apply{
            //Disabled animation
            //imageViewerBuilder?.withTransitionFrom(this)
        }
        return imageViewerBuilder?.build()
    }

    private fun handleNavBtnVisibility(currPos: Int, lastPos: Int) {
        if(lastPos == 0) {
            btnLeftSwipe?.hideView()
            btnRightSwipe?.hideView()
        } else {
            when (currPos) {
                0 -> {
                    btnLeftSwipe?.hideView()
                }
                in 1 until lastPos -> {
                    btnRightSwipe?.showView()
                    btnLeftSwipe?.showView()
                }
                lastPos -> {
                    btnRightSwipe?.hideView()
                }
            }
        }
    }

    private fun initClicks(actionListener: ImageActionHelper.ImageActionListener? = null) {
        context?.apply {
            PushDownAnim.setPushDownAnimTo(btnSetWall, btnDownload, btnFav, btnShare, btnDelete, btnMore, rlUserInfo,
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

                        R.id.btnDelete -> {
                            photosList[selectedPhotoPos].apply {
                                actionListener?.onDelete(this)
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
                                    btnMore?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_more_vertical))
                                }else {
                                    animatePhotographer = false
                                    rlUserInfo?.hideView()
                                    showView()
                                    //lnlOtherButtons?.hideView()
                                    startSimpleTransition()
                                    btnMore?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close))
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
                            openUrl(photosList[selectedPhotoPos].links.html + "?utm_source=${getString(
                                R.string.app_name)}&utm_medium=referral")
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
                ivPhotographer?.loadImageWithPreLoader(profileImage.medium)
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

    private fun getFormattedLikes(likes: String) : String = likes.addBefore(context?.getString(R.string.likes))

    private fun getFormattedHeight(height: String) : String = height.addBefore(context?.getString(R.string.height)) + Constants.Units.PX

    private fun getFormattedWidth(width: String) : String = width.addBefore(context?.getString(R.string.width)) + Constants.Units.PX

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

    fun dismiss() {
        imageViewer?.close()
    }

    companion object {
        fun getFormattedDesc(desc: String?, altDesc: String?): String = let {
            desc?.takeIf { it.isEmpty() }?.apply {
                return@let StringUtils.capitalize(this)
            }?: altDesc?.takeIf { it.isNotEmpty() }?.apply {
                return@let StringUtils.capitalize(this)
            }?: ""
        }
    }

    enum class Tools {
        SET_WALLPAPER, DOWNLOAD_PHOTO, FAV_PHOTO, SHARE_PHOTO, MORE, DELETE
    }

}