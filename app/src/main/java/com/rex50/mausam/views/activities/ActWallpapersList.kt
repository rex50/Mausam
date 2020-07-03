package com.rex50.mausam.views.activities

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.widget.Button
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
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.GradientHelper
import com.rex50.mausam.views.activities.ActMain.wallpaperListMode
import com.rex50.mausam.views.adapters.AdaptContent
import kotlinx.android.synthetic.main.act_wallpaper_list.*
import org.apache.commons.lang3.StringUtils
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
    private var listData: MoreWallpaperListData? = null

    override fun getLayoutResource(): Int = R.layout.act_wallpaper_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getArgs()

        init()

    }

    private fun getArgs() {
        intent?.getParcelableExtra<MoreWallpaperListData>(Constants.IntentConstants.WALLPAPER_LIST_DATA)?.apply {
            listData = this
        }
    }

    private fun init() {

        initHeader()

        fabSearchedWallpaperBack?.setOnClickListener{ onBackPressed() }

        unsplashHelper = UnsplashHelper(this)

        initRecycler()

        ivLoader?.showView()

        getWallpapersOf(1)

    }

    private fun initHeader() {
        when(listData?.listMode){
            Constants.IntentConstants.LIST_MODE_PHOTOGRAPHER_WALLPAPER -> {
                headerPhotographer?.apply {
                    showView()
                    findViewById<View>(R.id.gradientLine)?.background =
                            GradientHelper.getInstance(this@ActWallpapersList)?.getRandomLeftRightGradient()

                    listData?.photographerInfo?.apply {
                        findViewById<ImageView>(R.id.user_img)?.loadImage(profileImage.large)

                        findViewById<TextView>(R.id.tvPageTitle)?.text = name?.takeIf { it.isNotEmpty() }?.let {
                            getString(R.string.photo_by_photographer, StringUtils.capitalize(name))
                        }?: getString(R.string.photographer_photo)

                        findViewById<TextView>(R.id.tvPageDesc)?.text = listData?.getDesc()

                        totalCollections?.takeIf { it > 0 }?.apply {
                            findViewById<Button>(R.id.btnBrowseUserCollections)?.apply {
                                val collectionText = getString(R.string.see_collections) + "($totalCollections)"
                                text = collectionText
                                showView()
                                setOnClickListener {
                                    showToast("Work in progress")
                                    //TODO : start user's collection list
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

                    listData?.generalInfo?.apply {
                        findViewById<TextView>(R.id.tvPageTitle)?.text = searchTerm?.takeIf { it.isNotEmpty() }?.let {
                            StringUtils.capitalize(searchTerm)
                        }?: getString(R.string.images)

                        desc?.takeIf { it.isNotEmpty() }?.apply {
                            findViewById<TextView>(R.id.tvPageDesc)?.text = this
                        }?: findViewById<TextView>(R.id.tvPageDesc).hideView()
                    }
                }
            }
        }
    }

    private fun initRecycler() {

        val layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)

        wallpapersModel = GenericModelFactory.getFavouritePhotographerTypeObject(listData?.getTitle(), listData?.getDesc(), wallpapersList)
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
                                            MoreWallpaperListData(
                                                    Constants.IntentConstants.LIST_MODE_PHOTOGRAPHER_WALLPAPER,
                                                    user,
                                                    null
                                            )
                                    )
                                }
                            }).showPhotographer(listData?.photographerInfo?.isNull() ?: true).show()
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
        Constants.IntentConstants.apply {
            when(listData?.listMode){
                LIST_MODE_POPULAR_WALLPAPER -> {
                    getPopularWallpapersOf(page)
                }

                LIST_MODE_PHOTOGRAPHER_WALLPAPER -> {
                    getPhotographerWallpapersOf(page)
                }

                LIST_MODE_GENERAL_WALLPAPER -> {
                    getSearchedWallpapersOf(page)
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
        unsplashHelper?.getSearchedPhotos(listData?.getTitle(), page, 20, object: GetUnsplashSearchedPhotosListener {
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
        materialSnackBar.showActionSnackBar(
                R.string.failed_getting_wallpapers_error_msg,
                R.string.ok_caps,
                MaterialSnackBar.LENGTH_INDEFINITE
                , object : MaterialSnackBar.SnackBarListener{
            override fun onActionPressed() {
                if(wallpapersList.isEmpty())
                    onBackPressed()
                materialSnackBar.dismiss()
            }
        })
    }

    private fun startMorePhotosActivity(data: MoreWallpaperListData) {
        startActivity(
                Intent(
                        this,
                        ActWallpapersList::class.java
                ).putExtra(
                        Constants.IntentConstants.WALLPAPER_LIST_DATA,
                        data
                )
        )
    }

    override fun onBackPressed() {
        if(scrollToTopActive) {
            recSearchedWallpapers?.smoothScrollToPosition(0)
            ablWallpaperList?.setExpanded(true)
        }else
            super.onBackPressed()
    }

    override fun internetStatus(internetType: Int) {

    }
}

data class MoreWallpaperListData(
        @wallpaperListMode var listMode: String? = Constants.IntentConstants.LIST_MODE_GENERAL_WALLPAPER,
        var photographerInfo: User?,
        var generalInfo: MoreData?
): Parcelable {

    fun getTitle(): String = generalInfo?.searchTerm ?: ""

    fun getDesc(): String = photographerInfo?.bio?.takeIf { it.isNotEmpty() } ?: generalInfo?.desc ?: Constants.Providers.POWERED_BY_UNSPLASH

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(User::class.java.classLoader),
            parcel.readParcelable(MoreData::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(listMode)
        parcel.writeParcelable(photographerInfo, flags)
        parcel.writeParcelable(generalInfo, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MoreWallpaperListData> {
        override fun createFromParcel(parcel: Parcel): MoreWallpaperListData {
            return MoreWallpaperListData(parcel)
        }

        override fun newArray(size: Int): Array<MoreWallpaperListData?> {
            return arrayOfNulls(size)
        }
    }
}

data class MoreData(
        var searchTerm: String?,
        var desc: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(searchTerm)
        parcel.writeString(desc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MoreData> {
        override fun createFromParcel(parcel: Parcel): MoreData {
            return MoreData(parcel)
        }

        override fun newArray(size: Int): Array<MoreData?> {
            return arrayOfNulls(size)
        }
    }
}
