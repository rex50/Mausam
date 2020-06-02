package com.rex50.mausam.utils

import android.content.Context
import androidx.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.Transition
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.imageviewer.loader.ImageLoader
import com.thekhaeng.pushdownanim.PushDownAnim
import com.transitionseverywhere.extra.Scale
import org.apache.commons.lang3.StringUtils


class ImageViewerHelper (){

    private var context: Context? = null
    private var selectedPhotoPos = 0
    private var animatePhotographer = true

    private var dialogLayout: View? = null

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

    private var cardPhotographerImg: CardView? = null
    private var ivPhotographer: ImageView? = null
    private var tvPhotographerName: TextView? = null
    private var vUserButtons: View? = null

    constructor(context: Context?) : this() {
        this.context = context
        initLayout(context)
    }

    private fun initLayout(context: Context?) {
        context?.apply {

            dialogLayout = View.inflate(this, R.layout.overlay_full_screen_image, null)

            lnlOtherButtons = dialogLayout?.findViewById(R.id.lnlOtherButtons)
            lnlExtraInfo = dialogLayout?.findViewById(R.id.lnlExtraInfo)
            rlUserInfo = dialogLayout?.findViewById(R.id.rlUserInfo)
            rlImageOverlay = dialogLayout?.findViewById(R.id.rlImageOverlay)

            btnSetWall = dialogLayout?.findViewById(R.id.btnSetWallpaper)
            btnDownload = dialogLayout?.findViewById(R.id.btnDownloadImage)
            btnFav = dialogLayout?.findViewById(R.id.btnFavImage)
            btnShare = dialogLayout?.findViewById(R.id.btnShareImage)
            btnMore = dialogLayout?.findViewById(R.id.btnMoreAboutImage)

            tvDesc = dialogLayout?.findViewById(R.id.tvPhotoDesc)
            tvCreated = dialogLayout?.findViewById(R.id.tvPhotoCreated)
            tvColor = dialogLayout?.findViewById(R.id.tvPhotoColor)
            tvLikes = dialogLayout?.findViewById(R.id.tvPhotoLikes)
            tvHeight = dialogLayout?.findViewById(R.id.tvPhotoHeight)
            tvWidth = dialogLayout?.findViewById(R.id.tvPhotoWidth)

            cardPhotographerImg = dialogLayout?.findViewById(R.id.cardPhotographerImg)
            ivPhotographer = dialogLayout?.findViewById(R.id.ivPhotographer)
            tvPhotographerName = dialogLayout?.findViewById(R.id.tvPhotographerName)
            vUserButtons = dialogLayout?.findViewById(R.id.vUserButtons)
        }
    }

    fun showImagesInFullScreen(photosList: List<UnsplashPhotos>, childImgView: ImageView? = null, childPos: Int = 0, actionListener: ImageActionListener? = null) {
        context?.apply {
            selectedPhotoPos = childPos
            animatePhotographer = true

            initClicks(photosList, actionListener)

            photosList[childPos].apply {
                bindPhotographerInfo(this)
                bindImageInfo(this)
            }

            val imageViewerBuilder: StfalconImageViewer.Builder<*> = StfalconImageViewer.Builder(this, photosList,
                    ImageLoader { imageView: ImageView?, unsplashPhotos: UnsplashPhotos ->
                        imageView?.apply {
                            Glide.with(this)
                                    .load(unsplashPhotos.urls.regular)
                                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                                    .thumbnail(Glide.with(this).load(R.drawable.ic_loader))
                                    .into(imageView)
                        }
                    })
                    .withHiddenStatusBar(false)
                    .withStartPosition(childPos)
                    .withBackgroundColorResource(R.color.white_to_black)
                    .withOverlayView(dialogLayout)
                    .withImageChangeListener {
                        selectedPhotoPos = it
                        photosList[selectedPhotoPos].apply {
                            bindPhotographerInfo(this)
                            bindImageInfo(this)
                        }
                    }
            childImgView?.apply{
                imageViewerBuilder.withTransitionFrom(this)
            }
            imageViewerBuilder.show()
        }
    }

    private fun initClicks(photosList: List<UnsplashPhotos>, actionListener: ImageActionListener? = null) {
        context?.apply {
            PushDownAnim.setPushDownAnimTo(btnSetWall, btnDownload, btnFav, btnShare, btnMore, rlUserInfo)
                    .setScale(0.9F)
                    .setOnClickListener { v: View ->
                        when (v.id) {

                            R.id.btnSetWallpaper -> {
                                actionListener?.onSetWallpaper(photosList[selectedPhotoPos])
                            }

                            R.id.btnDownloadImage -> {
                                actionListener?.onDownload(photosList[selectedPhotoPos])
                            }

                            R.id.btnFavImage -> {
                                actionListener?.onFavourite(photosList[selectedPhotoPos])
                            }

                            R.id.btnShareImage -> {
                                actionListener?.onShare(photosList[selectedPhotoPos])
                            }

                            R.id.btnMoreAboutImage -> {
                                actionListener?.onMore()
                                lnlExtraInfo?.apply {
                                    startSimpleTransition()
                                    if(isViewVisible()) {
                                        animatePhotographer = true
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
                                vUserButtons?.apply {
                                    if(isViewVisible()) hideView() else showView()
                                }
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
        if(animatePhotographer)
            startFadeTransition()
        photo.user.apply {
            ivPhotographer?.apply {
                Glide.with(this)
                        .load(profileImage.medium)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .thumbnail(Glide.with(this).load(R.drawable.ic_loader))
                        .into(this)
            }
            tvPhotographerName?.text = name
            if(animatePhotographer)
                rlUserInfo?.showView()
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

        open fun onSetWallpaper(photoInfo: UnsplashPhotos){}

        open fun onDownload(photoInfo: UnsplashPhotos){}

        open fun onFavourite(photoInfo: UnsplashPhotos){}

        open fun onShare(photoInfo: UnsplashPhotos){}

        open fun onMore(){}

    }
}