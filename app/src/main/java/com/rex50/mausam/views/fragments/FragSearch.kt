package com.rex50.mausam.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseFragment
import com.rex50.mausam.interfaces.*
import com.rex50.mausam.model_classes.item_types.*
import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.unsplash.collection.Tag
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos
import com.rex50.mausam.model_classes.utils.*
import com.rex50.mausam.model_classes.weather.WeatherModelClass
import com.rex50.mausam.network.APIManager
import com.rex50.mausam.network.APIManager.WeatherAPICallBackResponse
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Utils.TextValidationInterface
import com.rex50.mausam.views.MausamApplication
import com.rex50.mausam.views.activities.ActImageEditor
import com.rex50.mausam.views.adapters.AdaptHome
import com.rex50.mausam.views.bottomsheets.BSDownload
import kotlinx.android.synthetic.main.frag_search.*
import kotlinx.android.synthetic.main.header_custom_general.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class FragSearch : BaseFragment(), AllContentModel.ContentInsertedListener {
    var searchOnlyCity = false

    private var adaptHome: AdaptHome? = null
    private var allData: AllContentModel? = null
    private var sequenceOfLayout: MutableList<String> = ArrayList()
    private var mListener: OnFragmentInteractionListener? = null

    override fun getResourceLayout(): Int = R.layout.frag_search

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {

        arguments?.getBoolean(ARG_PARAM1)?.apply {
            searchOnlyCity = this
        }

        mContext?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        gradientLine?.background = GradientHelper.getInstance(mContext)?.getRandomLeftRightGradient()

        if (resources.getBoolean(R.bool.hide_search_providers)) {
            containerSearchProviders?.hideView()
            tvPageTitle?.setText(R.string.search_photo_title)
            tvPageDesc?.setText(R.string.search_photo_desc)
            etvSearch?.setHint(R.string.search_box_hint)
        }

        if (searchOnlyCity) {
            containerSearchProviders?.hideView()
            btnNextSearch?.apply {
                showView()
                setOnClickListener { mListener?.nextBtnClicked() }
            }
            tvPageTitle?.setText(R.string.search_loc_title)
            tvPageDesc?.setText(R.string.search_loc_desc)
            etvSearch?.setHint(R.string.search_loc_box_hint)
        }

        etvSearch?.setOnTouchListener(OnTouchListener { _: View?, event: MotionEvent ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                etvSearch?.compoundDrawables?.get(DRAWABLE_RIGHT)?.bounds?.width()?.apply {
                    if (event.rawX >= etvSearch?.right!! - this) {
                        onSearchClickAction()
                        return@OnTouchListener true
                    }
                }
            }
            false
        })

        etvSearch?.setOnEditorActionListener(OnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchClickAction()
                return@OnEditorActionListener true
            }
            false
        })

        etvSearch?.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus)
                showKeyBoard()
            else
                hideKeyboard()
        }
    }

    override fun load() {
        lvCenter?.showView()
        prepareHomeRecycler(getSequenceOfLayout())
    }

    private fun getSequenceOfLayout(): List<String> = sequenceOfLayout.apply {
        clear()
        add(Constants.AvailableLayouts.POPULAR_TAGS)
        add(Constants.AvailableLayouts.BROWSE_BY_CATEGORIES)
        add(Constants.AvailableLayouts.FEATURED_COLLECTIONS)
        add(Constants.AvailableLayouts.POPULAR_PHOTOS)
        add(Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS)
        add(Constants.AvailableLayouts.BROWSE_BY_COLORS)
        add(Constants.AvailableLayouts.LOCATION_BASED_PHOTOS)
        add(Constants.AvailableLayouts.WEATHER_BASED_PHOTOS)
        add(Constants.AvailableLayouts.TIME_BASED_PHOTOS)
    }

    private fun prepareHomeRecycler(sequenceOfLayout: List<String>) {
        val unsplashHelper = UnsplashHelper(mContext)
        allData = AllContentModel()
        allData?.setContentInsertListener(this)
        adaptHome = AdaptHome(GradientHelper.getInstance(requireContext()), allData)

        allData?.apply {
            setSequenceOfLayouts(sequenceOfLayout)
            setAdapter(adaptHome)
            initItemClicks(this)
        }

        //mListener?.getMaterialSnackBar()?.showIndeterminateBar(R.string.preparing_home)

        if (sequenceOfLayout.contains(Constants.AvailableLayouts.WEATHER_BASED_PHOTOS)) {
            val seasons = arrayListOf("late winter", "spring", "monsoon", "autumn", "summer", "early winter")
            val season = StringUtils.capitalize(seasons.random())
            unsplashHelper.getSearchedPhotos(season, 1, 20, object : GetUnsplashSearchedPhotosListener {
                override fun onSuccess(photos: SearchedPhotos) {
                    allData?.addSequentially(Constants.AvailableLayouts.WEATHER_BASED_PHOTOS,
                            GenericModelFactory.getGeneralTypeObject(Constants.AvailableLayouts.WEATHER_BASED_PHOTOS, /*Constants.Providers.POWERED_BY_UNSPLASH*/ season, false, photos.results))
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(FragHome.TAG, "onFailed: $errors")
                    allData?.increaseResponseCount()
                }
            })
        }
        if (sequenceOfLayout.contains(Constants.AvailableLayouts.LOCATION_BASED_PHOTOS)) {
            val places = arrayListOf("Gujarat", "Orissa", "Bengaluru", "Hydrebad", "Kolkata",
                    "Hong Kong", "London", "Malaysia", "Punjab", "Mumbai", "Chennai", "Paris", "USA", "Sri Lanka",
                    "Russia", "Pakistan", "Bangladesh", "Tibet", "Berlin", "Bhutan", "Africa", "Australia", "England")
            val place = StringUtils.capitalize(places.random())
            unsplashHelper.getSearchedPhotos(place, 1, 20, object : GetUnsplashSearchedPhotosListener {
                override fun onSuccess(photos: SearchedPhotos) {
                    if(!photos.results.isNullOrEmpty()){
                        allData?.addSequentially(Constants.AvailableLayouts.LOCATION_BASED_PHOTOS,
                                GenericModelFactory.getGeneralTypeObject(Constants.AvailableLayouts.LOCATION_BASED_PHOTOS, /*Constants.Providers.POWERED_BY_UNSPLASH*/ place, false, photos.results))
                    }else
                        allData?.increaseResponseCount()
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(FragHome.TAG, "onFailed: $errors")
                    allData?.increaseResponseCount()
                }
            })
        }
        if (sequenceOfLayout.contains(Constants.AvailableLayouts.TIME_BASED_PHOTOS)) {
            unsplashHelper.getSearchedPhotos("Night", 1, 20, object : GetUnsplashSearchedPhotosListener {
                override fun onSuccess(photos: SearchedPhotos) {
                    allData?.addSequentially(Constants.AvailableLayouts.TIME_BASED_PHOTOS,
                            GenericModelFactory.getGeneralTypeObject(Constants.AvailableLayouts.TIME_BASED_PHOTOS, Constants.Providers.POWERED_BY_UNSPLASH, false, photos.results))
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(FragHome.TAG, "onFailed: $errors")
                    allData?.increaseResponseCount()
                }
            })
        }
        if (sequenceOfLayout.contains(Constants.AvailableLayouts.POPULAR_PHOTOS)) {
            unsplashHelper.getPhotosAndUsers(UnsplashHelper.ORDER_BY_POPULAR, object : GetUnsplashPhotosAndUsersListener {
                override fun onSuccess(photos: List<UnsplashPhotos>, userList: List<User>) {
                    allData?.addSequentially(Constants.AvailableLayouts.POPULAR_PHOTOS,
                            GenericModelFactory.getGeneralTypeObject(Constants.AvailableLayouts.POPULAR_PHOTOS, Constants.Providers.POWERED_BY_UNSPLASH, true, photos))
                    if (sequenceOfLayout.contains(Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS)) {
                        allData?.addSequentially(Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS,
                                GenericModelFactory.getUserTypeObject(Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS, Constants.Providers.POWERED_BY_UNSPLASH, false, userList))
                    }
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(FragHome.TAG, "onFailed: $errors")
                    allData?.apply {
                        increaseResponseCount()
                        if (sequenceOfLayout.contains(Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS)) {
                            increaseResponseCount()
                        }
                    }
                }
            })
        }
        if (sequenceOfLayout.contains(Constants.AvailableLayouts.FEATURED_COLLECTIONS)) {
            unsplashHelper.getCollectionsAndTags(1, 10, object : GetUnsplashCollectionsAndTagsListener {
                override fun onSuccess(collection: List<Collections>, tagsList: List<Tag>) {
                    allData?.addSequentially(Constants.AvailableLayouts.FEATURED_COLLECTIONS,
                            GenericModelFactory.getCollectionTypeObject(Constants.AvailableLayouts.FEATURED_COLLECTIONS, Constants.Providers.POWERED_BY_UNSPLASH, true, collection))
                    if (sequenceOfLayout.contains(Constants.AvailableLayouts.POPULAR_TAGS)) {
                        allData?.addSequentially(Constants.AvailableLayouts.POPULAR_TAGS,
                                GenericModelFactory.getTagTypeObject(Constants.AvailableLayouts.POPULAR_TAGS, Constants.Providers.POWERED_BY_UNSPLASH, false, tagsList, true))
                    }
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(FragHome.TAG, "onFailed: $errors")
                    allData?.apply {
                        increaseResponseCount()
                        if (sequenceOfLayout.contains(Constants.AvailableLayouts.POPULAR_TAGS)) {
                            increaseResponseCount()
                        }
                    }
                }
            })
        }
        if (sequenceOfLayout.contains(Constants.AvailableLayouts.BROWSE_BY_CATEGORIES)) {
            mContext?.apply {
                val categories = listOf<String>(*resources.getStringArray(R.array.array_categories_type))
                allData?.addSequentially(Constants.AvailableLayouts.BROWSE_BY_CATEGORIES, GenericModelFactory.getCategoryTypeObject(Constants.AvailableLayouts.BROWSE_BY_CATEGORIES,
                        Constants.Providers.POWERED_BY_UNSPLASH, false, CategoryTypeModel.createModelFromStringList(categories), true))
            }?: allData?.increaseResponseCount()
        }
        if (sequenceOfLayout.contains(Constants.AvailableLayouts.BROWSE_BY_COLORS)) {
            mContext?.apply {
                val colorsList = listOf<String>(*resources.getStringArray(R.array.array_colors_type))
                allData!!.addSequentially(Constants.AvailableLayouts.BROWSE_BY_COLORS, GenericModelFactory.getColorTypeObject(Constants.AvailableLayouts.BROWSE_BY_COLORS, Constants.Providers.POWERED_BY_UNSPLASH,
                        false, ColorTypeModel.createModelFromStringList(colorsList), true))
            }?: allData?.increaseResponseCount()
        }
        if (sequenceOfLayout.contains(Constants.AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES)) {
            unsplashHelper.getUserPhotos("rpnickson", 1, 20, object : GetUnsplashPhotosListener {
                override fun onSuccess(photos: List<UnsplashPhotos>) {
                    allData?.addSequentially(Constants.AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES, GenericModelFactory.getFavouritePhotographerTypeObject(
                            Constants.AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES, Constants.Providers.POWERED_BY_UNSPLASH, photos))
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(FragHome.TAG, "onFailed: $errors")
                    allData?.increaseResponseCount()
                }
            })
        }
    }

    private fun initItemClicks(allContentModel: AllContentModel) {

        val bsDownload: BSDownload? = BSDownload().also { it.isCancelable = false }

        allContentModel.setOnClickListener(object : OnGroupItemClickListener {

            override fun onItemClick(o: Any?, childImgView: ImageView?, groupPos: Int, childPos: Int) {

                object : GenericModelCastHelper(o) {

                    override fun onCollectionType(collectionTypeModel: CollectionTypeModel?) {
                        mListener?.startMorePhotosActivity(
                                MoreListData(
                                        Constants.ListModes.LIST_MODE_COLLECTION_PHOTOS,
                                        collectionInfo = collectionTypeModel?.collections?.get(childPos)
                                )
                        )
                    }

                    override fun onGeneralType(generalTypeModel: GeneralTypeModel?) {
                        generalTypeModel?.apply {

                            ImageViewerHelper(mContext).with(photosList,
                                    childImgView, childPos, object : ImageActionHelper.ImageActionListener() {

                                override fun onSetWallpaper(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(mContext, photoInfo, false, object : ImageActionHelper.ImageSaveListener{
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun onDownloadProgress(progress: Int) {
                                            bsDownload?.onProgress(progress)
                                        }

                                        override fun response(imageMeta: UnsplashPhotos?, msg: String) {
                                            bsDownload?.downloaded()
                                            startActivity(Intent(context, ActImageEditor::class.java).also {
                                                it.putExtra(Constants.IntentConstants.PHOTO_DATA, imageMeta)
                                            })
                                        }
                                    })
                                }

                                override fun onDownload(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(mContext, photoInfo, false, object : ImageActionHelper.ImageSaveListener{
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun onDownloadProgress(progress: Int) {
                                            bsDownload?.onProgress(progress)
                                        }

                                        override fun response(imageMeta: UnsplashPhotos?, msg: String) {
                                            bsDownload?.downloaded()
                                            if(msg.isNotEmpty()){
                                                showToast(msg)
                                            }
                                        }
                                    })
                                }

                                override fun onFavourite(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(mContext, photoInfo, true, object : ImageActionHelper.ImageSaveListener{
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun onDownloadProgress(progress: Int) {
                                            bsDownload?.onProgress(progress)
                                        }

                                        override fun response(imageMeta: UnsplashPhotos?, msg: String) {
                                            bsDownload?.downloaded()
                                            if(msg.isNotEmpty()){
                                                showToast(msg)
                                            }
                                        }
                                    })
                                }

                                override fun onShare(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.shareImage(mContext, "Share", photoInfo.user.name, photoInfo.links.html)
                                }

                                override fun onUserPhotos(user: User) {
                                    mListener?.startMorePhotosActivity(
                                            MoreListData(
                                                    Constants.ListModes.LIST_MODE_USER_PHOTOS,
                                                    user
                                            )
                                    )
                                }
                            }).setDataSaverMode(MausamApplication.getInstance()?.getSharedPrefs()?.isDataSaverMode ?: false).show()
                        }
                    }

                    override fun onFavPhotographerType(favPhotographerTypeModel: FavouritePhotographerTypeModel?) {
                        favPhotographerTypeModel?.apply {
                            ImageViewerHelper(mContext).with(photosList,
                                    childImgView, childPos, object : ImageActionHelper.ImageActionListener() {
                                override fun onSetWallpaper(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(mContext, photoInfo, false, object : ImageActionHelper.ImageSaveListener{
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                            showToast(getString(R.string.download_started))
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                            showToast(getString(R.string.failed_to_download_no_internet))
                                        }

                                        override fun onDownloadProgress(progress: Int) {
                                            bsDownload?.onProgress(progress)
                                        }

                                        override fun response(imageMeta: UnsplashPhotos?, msg: String) {
                                            bsDownload?.downloaded()
                                            startActivity(Intent(context, ActImageEditor::class.java).also {
                                                it.putExtra(Constants.IntentConstants.PHOTO_DATA, imageMeta)
                                            })
                                        }
                                    })
                                }

                                override fun onDownload(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(mContext, photoInfo, false, object : ImageActionHelper.ImageSaveListener{
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                            showToast(getString(R.string.download_started))
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun onDownloadProgress(progress: Int) {
                                            bsDownload?.onProgress(progress)
                                        }

                                        override fun response(imageMeta: UnsplashPhotos?, msg: String) {
                                            bsDownload?.downloaded()
                                            if(msg.isNotEmpty()){
                                                showToast(msg)
                                            }
                                        }
                                    })
                                }

                                override fun onFavourite(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(mContext, photoInfo, true, object : ImageActionHelper.ImageSaveListener{
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                            showToast(getString(R.string.adding_to_fav))
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun onDownloadProgress(progress: Int) {
                                            bsDownload?.onProgress(progress)
                                        }

                                        override fun response(imageMeta: UnsplashPhotos?, msg: String) {
                                            bsDownload?.downloaded()
                                            if(msg.isNotEmpty()){
                                                showToast(msg)
                                            }
                                        }
                                    })
                                }

                                override fun onShare(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.shareImage(mContext, "Share", photoInfo.user.name, photoInfo.links.html)
                                }
                            }).setDataSaverMode(MausamApplication.getInstance()?.getSharedPrefs()?.isDataSaverMode ?: false).show()
                        }
                    }

                    override fun onTagType(tagTypeModel: TagTypeModel?) {
                        mListener?.startMorePhotosActivity(
                                MoreListData(
                                        Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
                                        generalInfo = MoreData(
                                                tagTypeModel?.tagsList?.get(childPos)?.title,
                                                Constants.Providers.POWERED_BY_UNSPLASH
                                        )
                                )
                        )
                    }

                    override fun onColorType(colorTypeModel: ColorTypeModel?) {
                        mListener?.startMorePhotosActivity(
                                MoreListData(
                                        Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
                                        generalInfo = MoreData(
                                                colorTypeModel?.colorsList?.get(childPos)?.colorName,
                                                Constants.Providers.POWERED_BY_UNSPLASH
                                        )
                                )
                        )
                    }

                    override fun onCategoryType(categoryTypeModel: CategoryTypeModel?) {
                        mListener?.startMorePhotosActivity(
                                MoreListData(
                                        Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
                                        generalInfo = MoreData(
                                                categoryTypeModel?.categories?.get(childPos)?.categoryName,
                                                Constants.Providers.POWERED_BY_UNSPLASH
                                        )
                                )
                        )
                    }

                    override fun onUserType(userTypeModel: UserTypeModel?) {
                        mListener?.startMorePhotosActivity(
                            MoreListData(
                                Constants.ListModes.LIST_MODE_USER_PHOTOS,
                                userTypeModel?.usersList?.get(childPos)
                            )
                        )
                    }
                }

            }

            override fun onMoreClicked(o: Any?, title: String?, groupPos: Int) {

                object : GenericModelCastHelper(o){
                    override fun onCollectionType(collectionTypeModel: CollectionTypeModel?) {
                        mListener?.startMoreFeaturedCollections(
                                MoreListData(
                                        Constants.ListModes.LIST_MODE_COLLECTIONS,
                                        generalInfo = MoreData(
                                                title
                                        )
                                )
                        )
                    }

                    override fun onGeneralType(generalTypeModel: GeneralTypeModel?) {
                        mListener?.startMorePhotosActivity(
                                MoreListData(
                                        Constants.ListModes.LIST_MODE_POPULAR_PHOTOS,
                                        generalInfo = MoreData(
                                                title,
                                                Constants.Providers.POWERED_BY_UNSPLASH
                                        )
                                )
                        )
                    }
                }

            }
        })
    }

    private fun onSearchClickAction() {
        if (searchOnlyCity)
            validateCity(StringUtils.capitalize(etvSearch?.text.toString().trim()))
        else
            searchPhotos()
    }

    private fun searchPhotos() {
        val searchTerm = etvSearch?.text.toString().trim().replace(" +".toRegex(), " ");
        etvSearch.setText(searchTerm)
        mListener?.startMorePhotosActivity(
                MoreListData(
                        Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
                        generalInfo = MoreData(
                                searchTerm,
                                Constants.Providers.POWERED_BY_UNSPLASH
                        )
                )
        )
        hideKeyboard()
    }

    private fun validateCity(city: String) {
        Utils.validateText(city, object : TextValidationInterface {
            override fun correct() {
                etvSearch?.setDisabled()
                tvSearchError?.text = ""
                startWeatherSearch(city)
            }

            override fun containNumber() {
                tvSearchError?.setText(R.string.contain_number_error_msg)
            }

            override fun containSpecialChars() {
                tvSearchError?.setText(R.string.contain_special_chars_error_msg)
            }

            override fun empty() {
                tvSearchError?.setText(R.string.empty_error_msg)
            }
        })
    }

    fun setErrorMsg(msg: String?) {
        tvSearchError?.text = msg
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

    fun setFocusToSearchBox(state: Boolean){
        if(state)
            etvSearch?.requestFocus()
        else
            etvSearch?.clearFocus()
    }

    private fun startWeatherSearch(place: String) {
        val apiManager = APIManager.getInstance(mContext)
        val urlExtras = HashMap<String, String>()
        urlExtras["q"] = place
        apiManager?.searchWeather(APIManager.SERVICE_CURRENT_WEATHER, urlExtras, object : WeatherAPICallBackResponse {
            override fun onWeatherResponseSuccess(jsonObject: JSONObject) {
                if (searchOnlyCity) {
                    mListener?.apply {
                        sharedPrefs?.apply {
                            lastWeatherData = jsonObject
                            userLocation = etvSearch?.text.toString()
                        }
                        onWeatherSearchSuccess(DataParser.parseWeatherData(jsonObject))
                    }
                }
            }

            override fun onWeatherResponseFailure(errorCode: Int, msg: String) {
                etvSearch?.isEnabled = true
                when (errorCode) {
                    Utils.PAGE_NOT_FOUND -> mListener?.snackBar?.show(getString(R.string.something_wrong_msg), MaterialSnackBar.LENGTH_LONG)
                    Utils.CITY_NOT_FOUND -> {
                        setErrorMsg(String.format(getString(R.string.search_city_error), place))
                        etvSearch?.requestFocus()
                    }
                    else -> etvSearch?.requestFocus()
                }
            }
        })
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri?)
        fun onWeatherSearchSuccess(weatherDetails: WeatherModelClass?)
        fun nextBtnClicked()
        //fun startMorePhotosActivity(listMode: String?, searchTerm: String?, desc: String?)
        fun startMorePhotosActivity(data: MoreListData)
        fun startMoreFeaturedCollections(moreListData: MoreListData)
        val sharedPrefs: MausamSharedPrefs?
        val snackBar: MaterialSnackBar?
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val TAG = "HomeFragment"

        @JvmStatic
        fun newInstance(searchOnlyCity: Boolean = false): FragSearch {
            val fragment = FragSearch()
            val args = Bundle()
            args.putBoolean(ARG_PARAM1, searchOnlyCity)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAllContentLoaded() {
        CoroutineScope(Dispatchers.Main).launch {
            recDiscoverContent?.apply {
                layoutManager = LinearLayoutManager(mContext)
                setHasFixedSize(true)
                delay(300)
                lvCenter?.hideView()
                adapter = adaptHome
            }
        }
    }

    override fun onScrollToTop() {
        nsDiscover?.smoothScrollTo(0, 0)
        ablDiscover?.setExpanded(true, true)
    }


}