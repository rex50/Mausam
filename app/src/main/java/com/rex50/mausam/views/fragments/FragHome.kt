package com.rex50.mausam.views.fragments

import android.animation.LayoutTransition
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseFragment
import com.rex50.mausam.interfaces.*
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.model_classes.weather.WeatherModelClass
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.*
import com.rex50.mausam.views.activities.ActPhotosList
import com.rex50.mausam.views.adapters.AdaptContent
import com.rex50.mausam.views.bottomsheets.BSDownload
import com.thekhaeng.pushdownanim.PushDownAnim
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.android.synthetic.main.act_photos_list.*
import kotlinx.android.synthetic.main.frag_home.*
import kotlinx.android.synthetic.main.header_custom_home.*
import kotlinx.android.synthetic.main.item_weather_card.*
import org.json.JSONArray

class FragHome : BaseFragment() {

    private var photosList = ArrayList<UnsplashPhotos>()
    private var photosModel: GenericModelFactory? = null
    private var mWeatherDetails: WeatherModelClass? = null
    private val mParam2: String? = null
    private var mListener: OnFragmentInteractionListener? = null
    private var unsplashHelper: UnsplashHelper? = null
    private var adapter: AdaptContent? = null


    override fun getResourceLayout(): Int = R.layout.frag_home

    override fun initView() {
        arguments?.getSerializable(ARG_PARAM1)?.apply {
            mWeatherDetails = this as WeatherModelClass?
        }
        PushDownAnim.setPushDownAnimTo(btnSettings)
                .setScale(0.8F)
                .setOnClickListener { mListener?.startSettings() }
    }

    override fun load() {
        initHome()
    }
    
    private fun initHome(){
        unsplashHelper = UnsplashHelper(context)
        initDataModel()
        initRecycler()
        lvCenter?.showView()
        getPopularPhotosOf(INITIAL_PAGE)
        initClicks()
    }

    private fun initDataModel(){
        photosModel = GenericModelFactory.getFavouritePhotographerTypeObject(Constants.AvailableLayouts.POPULAR_PHOTOS, Constants.Providers.POWERED_BY_UNSPLASH, photosList)
    }

    private fun initRecycler() {

        val layoutManager = GridLayoutManager(context , 1,  LinearLayoutManager.VERTICAL, false)

        adapter = AdaptContent(context, photosModel)

        recHomeContent?.layoutManager = layoutManager
        if (recHomeContent?.itemDecorationCount ?: 0 > 0)
            recHomeContent?.addItemDecoration(ItemOffsetDecoration(context, R.dimen.recycler_item_offset_grid))
        recHomeContent?.adapter = ScaleInAnimationAdapter(adapter!!).apply {
            setFirstOnly(false)
        }

        val endlessScrollListener =  object: EndlessRecyclerOnScrollListener(layoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                lvBottom?.showView()
                getPopularPhotosOf(page)
            }
        }

        endlessScrollListener.apply {
            setInitialPage(INITIAL_PAGE)
            setVisibleThreshold(4)
        }


        val layoutTrans = rlPhotos?.layoutTransition
        layoutTrans?.setDuration(700)
        layoutTrans?.enableTransitionType(LayoutTransition.CHANGING)

        val scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                fabSearchedPhotosBack?.apply {
                    if(layoutManager.findFirstVisibleItemPosition() > 0){
                        var scrollToTopActive = true
                        toRightAndRotate()
                    }else{
                        var scrollToTopActive = false
                        toNormal()
                    }
                }
            }

        }

        recHomeContent?.apply {
            clearOnScrollListeners()
            addOnScrollListener(endlessScrollListener)
            //addOnScrollListener(scrollListener)
        }

    }

    private fun initClicks(){

        val bsDownload: BSDownload? = BSDownload().also { it.isCancelable = false }

        adapter?.setChildClickListener(object : OnChildItemClickListener {
            override fun onItemClick(o: Any?, childImgView: ImageView?, childPos: Int) {
                object: GenericModelCastHelper(o){
                    override fun onFavPhotographerType(favPhotographerTypeModel: GenericModelFactory.FavouritePhotographerTypeModel?) {
                        favPhotographerTypeModel?.apply {
                            ImageViewerHelper(context).with(photosList,
                                    childImgView, childPos, object : ImageViewerHelper.ImageActionListener() {

                                override fun onSetWallpaper(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(context, photoInfo.links.download, name, name, false, object : ImageActionHelper.ImageSaveListener {
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun response(imageMeta: SavedImageMeta?, msg: String) {
                                            bsDownload?.downloaded()
                                            ImageActionHelper.setWallpaper(context, imageMeta?.getUri())
                                        }
                                    })
                                }

                                override fun onDownload(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(context, photoInfo.links.download, name, name, false, object : ImageActionHelper.ImageSaveListener {
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun response(imageMeta: SavedImageMeta?, msg: String) {
                                            bsDownload?.downloaded()
                                            if (msg.isNotEmpty()) {
                                                showToast(msg)
                                            }
                                        }
                                    })
                                }

                                override fun onFavourite(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(context, photoInfo.links.download, name, name, true, object : ImageActionHelper.ImageSaveListener {
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                            showToast(getString(R.string.adding_to_fav))
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun response(imageMeta: SavedImageMeta?, msg: String) {
                                            bsDownload?.downloaded()
                                            if (msg.isNotEmpty()) {
                                                showToast(msg)
                                            }
                                        }
                                    })
                                }

                                override fun onShare(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.shareImage(context, "Share", photoInfo.user.name, photoInfo.links.html)
                                }

                                override fun onUserPhotos(user: User) {
                                    mListener?.startMorePhotosActivity(
                                            MoreListData(
                                                    Constants.ListModes.LIST_MODE_USER_PHOTOS,
                                                    user,
                                                    null,
                                                    null
                                            )
                                    )
                                }
                            }).showPhotographer( true)
                                    .setDataSaverMode(mListener?.isDataSaverMode() ?: false)
                                    .show()
                        }
                    }
                }
            }
        })
    }


    private fun getPopularPhotosOf(page: Int){
        unsplashHelper?.getPhotosAndUsers(UnsplashHelper.ORDER_BY_POPULAR, page, 20, object : GetUnsplashPhotosAndUsersListener {
            override fun onSuccess(photos: List<UnsplashPhotos>, userList: List<User>) {
                lvCenter?.hideView()
                lvBottom?.hideView()
                updateList(page, photos)
            }

            override fun onFailed(errors: JSONArray) {
                lvCenter?.hideView()
                lvBottom?.hideView()
                //TODO: showErrorMsg()
            }
        })
    }

    private fun showErrorMsg(){
        mListener?.getMaterialSnackBar()?.showActionSnackBar(
                R.string.failed_getting_photos_error_msg,
                R.string.ok_caps,
                MaterialSnackBar.LENGTH_INDEFINITE
                , object : MaterialSnackBar.SnackBarListener{
            override fun onActionPressed() {

                mListener?.getMaterialSnackBar()?.dismiss()
            }
        })
    }

    private fun updateList(page: Int, results: List<UnsplashPhotos>) {
        when(page){
            INITIAL_PAGE -> {
                photosList.clear()
                results.takeIf { it.isNotEmpty() }?.apply {
                    photosList.addAll(results)
                    adapter?.notifyItemRangeInserted(0, results.size)
                }
            }

            else -> {
                results.takeIf { it.isNotEmpty() }?.apply {
                    photosList.addAll(results)
                    adapter?.notifyItemRangeInserted(photosList.size - results.size, results.size)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri?)
        fun startMorePhotosActivity(data: MoreListData)
        fun startSearchScreen()
        fun isDataSaverMode(): Boolean?
        val lastLocationDetails: Bundle?
        fun getMaterialSnackBar(): MaterialSnackBar?
        fun requestWeather(listener: WeatherResultListener?) //void getWeatherWallpaper();
        fun startMoreFeaturedCollections(data: MoreListData)
        fun startSettings()
    }

    companion object {

        const val INITIAL_PAGE: Int = 0

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        const val TAG = "HomeFragment"

        fun newInstance(weatherDetails: WeatherModelClass?): FragHome {
            val fragment = FragHome()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, weatherDetails)
            fragment.arguments = args
            return fragment
        }
    }
}