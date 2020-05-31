package com.rex50.mausam.views.activities

import android.animation.LayoutTransition
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.interfaces.GetUnsplashSearchedPhotosListener
import com.rex50.mausam.interfaces.OnChildItemClickListener
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.*
import com.rex50.mausam.views.adapters.AdaptContent
import kotlinx.android.synthetic.main.act_wallpaper_list.*
import kotlinx.android.synthetic.main.header_custom_general.*
import org.apache.commons.lang3.StringUtils
import org.json.JSONArray

class ActWallpapersList : BaseActivity() {

    companion object{
        const val INITIAL_PAGE = 1
    }
    private var pageTitle = ""
    private var pageDescription = ""
    private var wallpapersList = ArrayList<UnsplashPhotos>()
    private var wallpapersModel: GenericModelFactory? = null
    private var adapter: AdaptContent? = null
    private var scrollToTopActive = false

    override fun getLayoutResource(): Int = R.layout.act_wallpaper_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getArgs()

        init()

    }

    private fun getArgs() {
        intent?.apply {
            getStringExtra(Constants.IntentConstants.SEARCH_TERM)?.apply {
                pageTitle = this
            }

            getStringExtra(Constants.IntentConstants.SEARCH_DESC)?.apply {
                pageDescription = this
            }
        }
    }

    private fun init() {

        txtPageTitle?.text = pageTitle.takeIf { it.isNotEmpty() }?.let {
            StringUtils.capitalize(pageTitle)
        }?: getString(R.string.Images)

        pageDescription.takeIf { it.isNotEmpty() }?.apply {
            txtPageDesc?.text = this
        }?: txtPageDesc?.hideView()

        fabSearchedWallpaperBack?.setOnClickListener{onBackPressed()}

        initRecycler()

        ivLoader?.showView()

        getSearchedWallpapersOf(1)

    }

    private fun initRecycler() {

        val imageViewer: ImageViewerHelper? = ImageViewerHelper(this)

        val layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)

        wallpapersModel = GenericModelFactory.getFavouritePhotographerTypeObject(pageTitle, pageDescription, wallpapersList)
        adapter = AdaptContent(this, wallpapersModel)
        adapter?.setChildClickListener(object : OnChildItemClickListener {
            override fun onItemClick(o: Any?, childImgView: ImageView?, childPos: Int) {
                imageViewer?.showImagesInFullScreen(wallpapersList, childImgView, childPos, object : ImageViewerHelper.ImageActionListener() {
                    override fun onDownload(photoInfo: UnsplashPhotos) {
                        super.onDownload(photoInfo)

                    }
                })
            }
        })

        recSearchedWallpapers?.layoutManager = layoutManager
        recSearchedWallpapers?.adapter = adapter

        val endlessScrollListener =  object: EndlessRecyclerOnScrollListener(layoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getSearchedWallpapersOf(page)
            }
        }

        endlessScrollListener.apply {
            setInitialPage(INITIAL_PAGE)
            setVisibleThreshold(4)
        }

        val layoutTrans = rlWallpapers?.layoutTransition
        layoutTrans?.setDuration(700)
        layoutTrans?.enableTransitionType(LayoutTransition.CHANGING)

        val scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                fabSearchedWallpaperBack?.apply {
                    if(layoutManager.findFirstVisibleItemPosition() > 0){
                        scrollToTopActive = true
                        toRightAndRotate()
                    }else{
                        scrollToTopActive = false
                        toNormal()
                    }
                }
            }

        }

        recSearchedWallpapers.addOnScrollListener(endlessScrollListener)
        recSearchedWallpapers.addOnScrollListener(scrollListener)

    }

    private fun getSearchedWallpapersOf(page: Int) {
        UnsplashHelper(this).getSearchedPhotos(pageTitle, page, 20, object: GetUnsplashSearchedPhotosListener {
            override fun onSuccess(photos: SearchedPhotos?) {
                ivLoader?.hideView()
                photos?.apply {
                    when(page){
                        INITIAL_PAGE -> {
                            wallpapersList.clear()
                            wallpapersList.addAll(results)
                            adapter?.notifyItemRangeInserted(0, 20)
                        }

                        else -> {
                            val lastSize = wallpapersList.size
                            wallpapersList.addAll(results)
                            adapter?.notifyItemRangeInserted(lastSize, 20)
                        }
                    }
                }
            }

            override fun onFailed(errors: JSONArray?) {
                ivLoader?.hideView()
                materialSnackBar.showActionSnackBar(R.string.failed_getting_wallpapers_error_msg, R.string.ok_caps,
                        MaterialSnackBar.LENGTH_INDEFINITE){
                    if(wallpapersList.isEmpty())
                        onBackPressed()
                    materialSnackBar.dismiss()
                }
            }
        })
    }

    override fun onBackPressed() {
        if(scrollToTopActive)
            recSearchedWallpapers?.smoothScrollToPosition(0)
        else
            super.onBackPressed()
    }

    override fun internetStatus(internetType: Int) {

    }
}
