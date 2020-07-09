package com.rex50.mausam.views.activities

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.interfaces.GetUnsplashPhotosAndUsersListener
import com.rex50.mausam.interfaces.GetUnsplashPhotosListener
import com.rex50.mausam.interfaces.GetUnsplashSearchedPhotosListener
import com.rex50.mausam.interfaces.OnChildItemClickListener
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.GradientHelper
import com.rex50.mausam.views.adapters.AdaptContent
import kotlinx.android.synthetic.main.act_wallpaper_list.*
import org.json.JSONArray

class ActWallpapersList : BaseActivity() {

    companion object{
        private const val INITIAL_PAGE = 1
    }
    private var wallpapersList = ArrayList<UnsplashPhotos>()
    private var wallpapersModel: GenericModelFactory? = null
    private var adapter: AdaptContent? = null
    private var scrollToTopActive = false
    private var unsplashHelper: UnsplashHelper? = null
    private var listData: MoreListData? = null

    override val layoutResource: Int
        get() = R.layout.act_wallpaper_list

    override fun loadAct(savedInstanceState: Bundle?) {
        getArgs()
        init()
    }

    private fun getArgs() {
        intent?.getParcelableExtra<MoreListData>(Constants.IntentConstants.LIST_DATA)?.apply {
            listData = this
        }
    }

    private fun init() {

        initHeader()

        fabSearchedWallpaperBack?.setOnClickListener{ if(scrollToTopActive) scrollToTop() else onBackPressed() }

        unsplashHelper = UnsplashHelper(this)

        initRecycler()

        ivLoader?.showView()

        getWallpapersOf(1)

    }

    private fun initHeader() {
        when(listData?.listMode){
            Constants.ListModes.LIST_MODE_USER_PHOTOS -> {
                headerPhotographer?.apply {
                    showView()

                    findViewById<View>(R.id.gradientLine)?.background =
                            GradientHelper.getInstance(this@ActWallpapersList)?.getRandomLeftRightGradient()

                    findViewById<TextView>(R.id.tvPageTitle)?.text = listData?.getTitle(this@ActWallpapersList)

                    findViewById<TextView>(R.id.tvPageDesc)?.text = listData?.getDesc()

                    listData?.photographerInfo?.apply {
                        findViewById<ImageView>(R.id.ivUser)?.loadImageWithPreLoader(profileImage.large)

                        totalCollections?.takeIf { it > 0 }?.apply {
                            findViewById<Button>(R.id.btnBrowseUserCollections)?.apply {
                                val collectionText = getString(R.string.see_collections) + "($totalCollections)"
                                text = collectionText
                                showView()
                                setOnClickListener {
                                    showUserCollections(
                                            MoreListData(
                                                    Constants.ListModes.LIST_MODE_USER_COLLECTIONS,
                                                    listData?.photographerInfo
                                            )
                                    )
                                }
                            }
                        }

                        findViewById<Button>(R.id.btnAddFavUser)?.setOnClickListener{
                            showToast("Coming soon...")
                            //TODO : add user as favourite
                        }

                        findViewById<Button>(R.id.btnMore)?.setOnClickListener {
                            showToast("Work in progress, need to redesign the dialog")
                            //TODO: show a dialog with the options
                            /*findViewById<LinearLayout>(R.id.lnlHeaderPhotographer)?.startSimpleTransition()
                            findViewById<LinearLayout>(R.id.lnlUserButtons)?.apply {
                                if(isViewVisible()) hideView() else showView()
                            }*/
                        }
                    }
                }
            }

            else -> {
                headerGeneral?.apply {
                    showView()

                    findViewById<View>(R.id.gradientLine)?.background =
                            GradientHelper.getInstance(this@ActWallpapersList)?.getRandomLeftRightGradient()

                    listData?.apply {
                        findViewById<TextView>(R.id.tvPageTitle)?.text = getTitle(this@ActWallpapersList)

                        findViewById<TextView>(R.id.tvPageDesc)?.apply {
                            getDesc().takeIf { it.isNotEmpty() }?.apply {
                                text = this
                            }?: hideView()
                        }

                        getBgImgUrl().takeIf { it.isNotEmpty() }?.apply {
                            findViewById<ImageView>(R.id.ivHeaderImg)?.loadImageWithPreLoader(this, null)
                            findViewById<FrameLayout>(R.id.flHeaderBg)?.showView()
                        }

                    }
                }
            }
        }
    }

    private fun initRecycler() {

        val layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)

        wallpapersModel = GenericModelFactory.getFavouritePhotographerTypeObject(listData?.getTitle(this), listData?.getDesc(), wallpapersList)
        adapter = AdaptContent(this, wallpapersModel)
        adapter?.setChildClickListener(object : OnChildItemClickListener {
            override fun onItemClick(o: Any?, childImgView: ImageView?, childPos: Int) {
                object: GenericModelCastHelper(o){
                    override fun onFavPhotographerType(favPhotographerTypeModel: GenericModelFactory.FavouritePhotographerTypeModel?) {
                        favPhotographerTypeModel?.apply {
                            ImageViewerHelper(this@ActWallpapersList).with(photosList,
                                    childImgView, childPos, object : ImageViewerHelper.ImageActionListener() {

                                override fun onSetWallpaper(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(this@ActWallpapersList, photoInfo.links.download, name, name, false, object : ImageActionHelper.ImageSaveListener {
                                        override fun onDownloadStarted() {
                                            showToast(getString(R.string.download_started))
                                        }

                                        override fun onDownloadFailed() {
                                            showToast(getString(R.string.failed_to_download_no_internet))
                                        }

                                        override fun response(imageMeta: SavedImageMeta?, msg: String) {
                                            ImageActionHelper.setWallpaper(this@ActWallpapersList, imageMeta?.getUri())
                                        }
                                    })
                                }

                                override fun onDownload(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(this@ActWallpapersList, photoInfo.links.download, name, name, false, object : ImageActionHelper.ImageSaveListener {
                                        override fun onDownloadStarted() {
                                            showToast(getString(R.string.download_started))
                                        }

                                        override fun onDownloadFailed() {
                                            showToast(getString(R.string.failed_to_download_no_internet))
                                        }

                                        override fun response(imageMeta: SavedImageMeta?, msg: String) {
                                            if (msg.isNotEmpty()) {
                                                showToast(msg)
                                            }
                                        }
                                    })
                                }

                                override fun onFavourite(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(this@ActWallpapersList, photoInfo.links.download, name, name, true, object : ImageActionHelper.ImageSaveListener {
                                        override fun onDownloadStarted() {
                                            showToast(getString(R.string.adding_to_fav))
                                        }

                                        override fun onDownloadFailed() {
                                            showToast(getString(R.string.failed_to_download_no_internet))
                                        }

                                        override fun response(imageMeta: SavedImageMeta?, msg: String) {
                                            if (msg.isNotEmpty()) {
                                                showToast(msg)
                                            }
                                        }
                                    })
                                }

                                override fun onShare(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.shareImage(this@ActWallpapersList, "Share", photoInfo.user.name, photoInfo.links.html)
                                }

                                override fun onUserPhotos(user: User) {
                                    startMorePhotosActivity(
                                            MoreListData(
                                                    Constants.ListModes.LIST_MODE_USER_PHOTOS,
                                                    user,
                                                    null,
                                                    null
                                            )
                                    )
                                }
                            }).showPhotographer(listData?.photographerInfo?.isNull() ?: true)
                                    .setDataSaverMode(mausamSharedPrefs?.isDataSaverMode ?: false)
                                    .show()
                        }
                    }
                }
            }
        })

        recSearchedWallpapers?.layoutManager = layoutManager
        recSearchedWallpapers?.addItemDecoration(ItemOffsetDecoration(this, R.dimen.recycler_item_offset_grid))
        recSearchedWallpapers?.adapter = adapter

        val endlessScrollListener =  object: EndlessRecyclerOnScrollListener(layoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getWallpapersOf(page)
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

    private fun getWallpapersOf(page: Int){
        Constants.ListModes.apply {
            when(listData?.listMode){
                LIST_MODE_POPULAR_PHOTOS -> {
                    getPopularWallpapersOf(page)
                }

                LIST_MODE_USER_PHOTOS -> {
                    getPhotographerWallpapersOf(page)
                }

                LIST_MODE_GENERAL_PHOTOS -> {
                    getSearchedWallpapersOf(page)
                }

                LIST_MODE_COLLECTION_PHOTOS -> {
                    getCollectionPhotosOf(page)
                }
            }
        }
    }

    private fun getPopularWallpapersOf(page: Int){
        unsplashHelper?.getPhotosAndUsers(UnsplashHelper.ORDER_BY_POPULAR, page, 20, object : GetUnsplashPhotosAndUsersListener {
            override fun onSuccess(photos: List<UnsplashPhotos>, userList: List<User>) {
                ivLoader?.hideView()
                updateList(page, photos)
            }

            override fun onFailed(errors: JSONArray) {
                ivLoader?.hideView()
                showErrorMsg()
            }
        })
    }

    private fun getPhotographerWallpapersOf(page: Int){
        unsplashHelper?.getUserPhotos(listData?.photographerInfo?.username, page, 20, object : GetUnsplashPhotosListener {
            override fun onSuccess(photos: MutableList<UnsplashPhotos>?) {
                ivLoader?.hideView()
                photos?.apply {
                    updateList(page, this)
                }
            }

            override fun onFailed(errors: JSONArray) {
                ivLoader?.hideView()
                //TODO: implement proper error handling i.e. use errors
                showToast("No public photos found of " + listData?.photographerInfo?.name)
                finish()
            }
        })
    }

    private fun getSearchedWallpapersOf(page: Int) {
        unsplashHelper?.getSearchedPhotos(listData?.generalInfo?.term, page, 20, object: GetUnsplashSearchedPhotosListener {
            override fun onSuccess(photos: SearchedPhotos?) {
                ivLoader?.hideView()
                photos?.apply {
                    updateList(page, results)
                }
            }

            override fun onFailed(errors: JSONArray?) {
                ivLoader?.hideView()
                showErrorMsg()
            }
        })
    }

    private fun getCollectionPhotosOf(page: Int) {
        unsplashHelper?.getCollectionPhotos(listData?.collectionInfo?.id.toString(), page, 20, object: GetUnsplashPhotosListener {

            override fun onSuccess(photos: MutableList<UnsplashPhotos>?) {
                ivLoader?.hideView()
                photos?.apply {
                    updateList(page, this)
                }
            }

            override fun onFailed(errors: JSONArray?) {
                ivLoader?.hideView()
                showErrorMsg()
            }
        })
    }

    private fun updateList(page: Int, results: List<UnsplashPhotos>) {
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

    private fun showErrorMsg(){
        materialSnackBar?.showActionSnackBar(
                R.string.failed_getting_wallpapers_error_msg,
                R.string.ok_caps,
                MaterialSnackBar.LENGTH_INDEFINITE
                , object : MaterialSnackBar.SnackBarListener{
            override fun onActionPressed() {
                if(wallpapersList.isEmpty())
                    onBackPressed()
                materialSnackBar?.dismiss()
            }
        })
    }

    private fun startMorePhotosActivity(data: MoreListData) {
        startActivity(
                Intent(
                        this,
                        ActWallpapersList::class.java
                ).putExtra(
                        Constants.IntentConstants.LIST_DATA,
                        data
                )
        )
    }

    private fun showUserCollections(data: MoreListData){
        startActivity(
                Intent(
                        this,
                        ActCollectionsList::class.java
                ).putExtra(
                        Constants.IntentConstants.LIST_DATA,
                        data
                )
        )
    }

    private fun scrollToTop(){
        recSearchedWallpapers?.smoothScrollToPosition(0)
        ablWallpaperList?.setExpanded(true)
    }

    override fun internetStatus(internetType: Int) {

    }
}