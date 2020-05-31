package com.rex50.mausam.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.imageviewer.loader.ImageLoader
import com.thekhaeng.pushdownanim.PushDownAnim
import org.apache.commons.lang3.StringUtils

class ImageViewerHelper (private val context: Context?){

    fun showImagesInFullScreen(photosList: List<UnsplashPhotos>, childImgView: ImageView? = null, childPos: Int = 0, actionListener: ImageActionListener? = null) {
        context?.apply {
            var selectedPhotoPos = childPos

            val dialogLayout = View.inflate(this, R.layout.overlay_full_screen_image, null)

            val btnSetWall = dialogLayout.findViewById<ImageView>(R.id.btnSetWallpaper)
            val btnDownload = dialogLayout.findViewById<ImageView>(R.id.btnDownloadImage)
            val btnFav = dialogLayout.findViewById<ImageView>(R.id.btnFavImage)
            val btnShare = dialogLayout.findViewById<ImageView>(R.id.btnShareImage)
            val btnMore = dialogLayout.findViewById<ImageView>(R.id.btnMoreAboutImage)

            val lnlOtherButtons = dialogLayout.findViewById<LinearLayout>(R.id.lnlOtherButtons)
            val lnlExtraInfo = dialogLayout.findViewById<LinearLayout>(R.id.lnlExtraInfo)




            val tvDesc = dialogLayout.findViewById<TextView>(R.id.tvPhotoDesc)
            val tvCreated = dialogLayout.findViewById<TextView>(R.id.tvPhotoCreated)
            val tvColor = dialogLayout.findViewById<TextView>(R.id.tvPhotoColor)
            val tvLikes = dialogLayout.findViewById<TextView>(R.id.tvPhotoLikes)
            val tvHeight = dialogLayout.findViewById<TextView>(R.id.tvPhotoHeight)
            val tvWidth = dialogLayout.findViewById<TextView>(R.id.tvPhotoWidth)

            fun setInfoToViews(photo: UnsplashPhotos){
                photo.apply {
                    getFormattedDesc(description?.toString(), altDescription?.toString()).takeIf { it.isNotEmpty() }?.apply {
                        tvDesc?.showView()
                        tvDesc?.text = this
                    }?: tvDesc.hideView()
                    tvCreated?.text = getFormattedDate(createdAt.toDateFormat("dd-MM-YYY"))
                    tvColor?.text = getFormattedColor(color.toString())
                    tvLikes?.text = getFormattedLikes(likes.toString())
                    tvHeight?.text = getFormattedHeight(height.toString())
                    tvWidth?.text = getFormattedWidth(width.toString())
                }
            }

            setInfoToViews(photosList[childPos])

            PushDownAnim.setPushDownAnimTo(btnSetWall, btnDownload, btnFav, btnShare, btnMore)
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
                                    if(isViewVisible()) {
                                        btnMore?.setImageDrawable(getDrawable(R.drawable.ic_more_vertical))
                                        //lnlOtherButtons?.showView()
                                        hideView()
                                    }else {
                                        btnMore?.setImageDrawable(getDrawable(R.drawable.ic_close))
                                        //lnlOtherButtons?.hideView()
                                        showView()
                                    }
                                }
                            }
                        }
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
                        setInfoToViews(photosList[selectedPhotoPos])
                    }
            childImgView?.apply{
                imageViewerBuilder.withTransitionFrom(this)
            }
            imageViewerBuilder.show()
        }
    }

    private fun getFormattedLikes(likes: String) : String = context?.getString(R.string.likes) + "  " + likes

    private fun getFormattedHeight(height: String) : String = context?.getString(R.string.height) + "  " + height + "px"

    private fun getFormattedWidth(width: String) : String = context?.getString(R.string.width) + "  " + width + "px"

    private fun getFormattedDesc(desc: String?, altDesc: String?): String = let {
        desc?.takeIf { it.isEmpty() }?.apply {
            return@let StringUtils.capitalize(this)
        }?: altDesc?.takeIf { it.isNotEmpty() }?.apply {
            return@let StringUtils.capitalize(this)
        }?: ""
    }

    private fun getFormattedDate(date: String): String? =  context?.getString(R.string.created_on) + " " + date

    private fun getFormattedColor(color: String): String? =  context?.getString(R.string.color) + "  " + color

    abstract class ImageActionListener(){

        open fun onSetWallpaper(photoInfo: UnsplashPhotos){}

        open fun onDownload(photoInfo: UnsplashPhotos){}

        open fun onFavourite(photoInfo: UnsplashPhotos){}

        open fun onShare(photoInfo: UnsplashPhotos){}

        open fun onMore(){}

    }
}