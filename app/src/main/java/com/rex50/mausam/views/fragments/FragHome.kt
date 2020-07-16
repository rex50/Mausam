package com.rex50.mausam.views.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseFragment
import com.rex50.mausam.interfaces.*
import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.unsplash.collection.Tag
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos
import com.rex50.mausam.model_classes.utils.AllContentModel
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.model_classes.utils.MoreData
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.model_classes.weather.WeatherModelClass
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.AvailableLayouts
import com.rex50.mausam.views.MausamApplication
import com.rex50.mausam.views.adapters.AdaptHome
import com.rex50.mausam.views.bottomsheets.BSDownload
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.frag_home.*
import kotlinx.android.synthetic.main.header_custom_home.*
import kotlinx.android.synthetic.main.item_weather_card.*
import org.apache.commons.lang3.StringUtils
import org.json.JSONArray
import java.util.*

class FragHome : BaseFragment(), AllContentModel.ContentInsertedListener {
    private var allData: AllContentModel? = null
    private var sequenceOfLayout: MutableList<String> = ArrayList()
    private var adaptHome: AdaptHome? = null

    private var mWeatherDetails: WeatherModelClass? = null
    private val mParam2: String? = null
    private var mListener: OnFragmentInteractionListener? = null


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
        prepareHomeRecycler(getSequenceOfLayout())
    }

    private fun init() {

        //weatherWallpaperRecycler = view1.findViewById(R.id.weather_wallpapers_recycler);
//locationWallpaperRecycler = view1.findViewById(R.id.location_wallpapers_recycler);
        var location = "null"
        val city = ""
        val state = ""
        val country = ""
        /*try {
            Bundle bundle = mListener.getLastLocationDetails();
            if(!bundle.isEmpty()){
                if(!Utils.optString(bundle.getString("city")).equalsIgnoreCase("null")){
                    location = bundle.getString("city");
                    city = location;
                }
                if(!Utils.optString(bundle.getString("state")).equalsIgnoreCase("null")){
                    location = location + ", " + bundle.getString("state");
                    state = bundle.getString("state");
                }
                if(!Utils.optString(bundle.getString("country")).equalsIgnoreCase("null")){
                    location = location + ", " + bundle.getString("country");
                    country = bundle.getString("country");
                }

                if (null != location && location.equalsIgnoreCase("null")) {
                    if (!Utils.optString(bundle.getString("feature")).equalsIgnoreCase("null")) {
                        location = bundle.getString("feature");
                    }
                }

            }
        }catch (Exception e){
            Log.e(TAG, "error getting location: ", e);
        }finally {
            if(location.equalsIgnoreCase("null"))
                location = "Error detecting location";
        }*/
/*unsplashHelper.getPhotos(UnsplashHelper.ORDER_BY_DEFAULT, new GetUnsplashPhotosListener() {
            @Override
            public void onSuccess(List<UnsplashPhotos> photos) {
                allData.addModel(0, GenericModelFactory.getGeneralTypeObject("Just Images", "This is the testing for multi-view recycler",
                        false, photos));

                adapter.notifyItemInserted(0);
            }

            @Override
            public void onFailed(JSONArray errors) {

            }
        });*/
//TODO : use "mWeatherDetails.getWeather().get(0).getMain()" instead of static weather
/*unsplashHelper.getSearchedPhotos( "Cold weather", 1, 20, new GetUnsplashSearchedPhotosListener() {
            @Override
            public void onSuccess(SearchedPhotos photos) {
                if(null == weatherWallpapersAdapter) {
                    weatherWallpapersAdapter = new AdaptWeatherWallpapers(getContext(), photos.getResults());
                    weatherWallpaperRecycler.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false));
                    weatherWallpaperRecycler.setAdapter(weatherWallpapersAdapter);
                }else{
                    weatherWallpapersAdapter.addItemsToList(photos.getResults());
                    weatherWallpapersAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailed(JSONArray errors) {

            }
        });

        //TODO : Remove static location also handle "Error detecting location"
        unsplashHelper.getSearchedPhotos(*/
/*location.isEmpty() && location.contains("null")? mWeatherDetails.getName() : location*/ /* "Surat", 1, 20, new GetUnsplashSearchedPhotosListener() {
            @Override
            public void onSuccess(SearchedPhotos photos) {
                if(null == locationWallpaperAdapter) {
                    locationWallpaperAdapter = new AdaptWeatherWallpapers(getContext(), photos.getResults());
                    locationWallpaperRecycler.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false));
                    locationWallpaperRecycler.setAdapter(locationWallpaperAdapter);
                }else{
                    locationWallpaperAdapter.addItemsToList(photos.getResults());
                    locationWallpaperAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailed(JSONArray errors) {

            }
        });*/
//TODO: remove below line once location module is attached
        location = "Surat"
        try { //for celsius = kelvin - 273.15
//        Double temp = mWeatherDetails.getMain().getTemp() - 273.15;
//tvCurrentWeather.setText(getFormattedString(convertToCelsius(mWeatherDetails.getMain().getTemp())));
//        txtWeatherMsg.setText(mWeatherDetails.getWeather().get(0).getDescription());
            if (location.isNotEmpty() && !location.equals("null", ignoreCase = true)) {
                tvCurrentLocation?.text = location
            } else {
                tvCurrentLocation?.text = mWeatherDetails!!.name
            }
            /* tvWeatherDescription.setText(mWeatherDetails.getWeather().get(0).getMain());
            tvMaxTemp.setText(getFormattedString(convertToCelsius(mWeatherDetails.getMain().getTempMax())));
            tvMinTemp.setText(getFormattedString(convertToCelsius(mWeatherDetails.getMain().getTempMin())));
            tvPressure.setText(mWeatherDetails.getMain().getPressure().toString());
            tvHumidity.setText(mWeatherDetails.getMain().getHumidity().toString());
            tvWindSpeed.setText(mWeatherDetails.getWind().getSpeed().toString());
            tvWindDegree.setText(mWeatherDetails.getWind().getDeg() + "");*/
//        Animation cardAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_weather_card);
//        weatherDetailsHolder.startAnimation(cardAnimation);
/*weatherDetailsHolder.setOnClickListener(v -> {

        });*/
        } catch (e: Exception) {
            Log.e(TAG, "Error setting text to views: ", e)
        }
        /*AllContentModel model = new AllContentModel();
        model.addAllModel(AllContentModel.getDummyModelsList(20));
        HomeAdapter adapter = new HomeAdapter(getContext(), model);
        recHomeContent.setLayoutManager(new LinearLayoutManager( getContext()));
        recHomeContent.setHasFixedSize(true);
        recHomeContent.setAdapter(adapter);*/
//recHomeContent.addOnScrollListener(endlessListener);
/*SearchedPhotos photos = new SearchedPhotos();

        RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
        adapter.registerRenderer(new ViewBinder(R.layout.item_category, SearchedPhotos.class, (photos, finder, payloads) -> {
            finder.setText(R.id.txt_category_title)
        }));*/

        btnMoreWeatherDetails?.setOnClickListener {
            /*if(weatherExtraInfoHolder.getVisibility() == View.VISIBLE){
                weatherExtraInfoHolder.setVisibility(View.GONE);
                btnMoreWeatherDetails.setText("Show more details");
            }else {
                weatherExtraInfoHolder.setVisibility(View.VISIBLE);
                btnMoreWeatherDetails.setText("Show less details");
            }*/

            if (weatherExtraInfoHolder?.visibility != View.VISIBLE) {
                weatherExtraInfoHolder?.showView()
                //btnMoreWeatherDetails.setText("Show less details");
                btnMoreWeatherDetails?.hideView()
            }
        }
        btnSearch?.setOnClickListener {
            if (Utils.getConnectivityStatus(mContext) != Utils.TYPE_NOT_CONNECTED) {
                mListener?.startSearchScreen()
            } else {
                Toast.makeText(mContext, "Please connect to internet first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSequenceOfLayout(): List<String> = sequenceOfLayout.apply {
        clear()
        add(AvailableLayouts.WEATHER_BASED_PHOTOS)
        add(AvailableLayouts.LOCATION_BASED_PHOTOS)
        add(AvailableLayouts.TIME_BASED_PHOTOS)
        add(AvailableLayouts.FEATURED_COLLECTIONS)
        add(AvailableLayouts.POPULAR_PHOTOGRAPHERS)
        add(AvailableLayouts.BROWSE_BY_CATEGORIES)
        add(AvailableLayouts.POPULAR_PHOTOS)
        add(AvailableLayouts.POPULAR_TAGS)
        add(AvailableLayouts.BROWSE_BY_COLORS)
        //add(AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES)

        //Use below format when implemented in search screen
        /*add(AvailableLayouts.POPULAR_TAGS)
        add(AvailableLayouts.FEATURED_COLLECTIONS)
        add(AvailableLayouts.POPULAR_WALLPAPERS)
        add(AvailableLayouts.POPULAR_PHOTOGRAPHERS)
        add(AvailableLayouts.BROWSE_BY_CATEGORIES)
        add(AvailableLayouts.BROWSE_BY_COLORS)
        add(AvailableLayouts.LOCATION_BASED_WALLPAPERS)
        add(AvailableLayouts.WEATHER_BASED_WALLPAPERS)
        add(AvailableLayouts.TIME_BASED_WALLPAPERS)*/
    }

    private fun prepareHomeRecycler(sequenceOfLayout: List<String>) {
        val unsplashHelper = UnsplashHelper(mContext)
        allData = AllContentModel()
        allData?.setContentInsertListener(this)
        adaptHome = AdaptHome(mContext, allData)
        recHomeContent?.apply {
            layoutManager = LinearLayoutManager(mContext)
            setHasFixedSize(true)
            adapter = adaptHome
        }

        allData?.apply {
            setSequenceOfLayouts(sequenceOfLayout)
            setAdapter(adaptHome)
            initItemClicks(this)
        }

        mListener?.getMaterialSnackBar()?.showIndeterminateBar(R.string.preparing_home)

        if (sequenceOfLayout.contains(AvailableLayouts.WEATHER_BASED_PHOTOS)) {
            val seasons = arrayListOf("late winter", "spring", "monsoon", "autumn", "summer", "early winter")
            val season = StringUtils.capitalize(seasons.random())
            unsplashHelper.getSearchedPhotos(season, 1, 20, object : GetUnsplashSearchedPhotosListener {
                override fun onSuccess(photos: SearchedPhotos) {
                    allData?.addSequentially(AvailableLayouts.WEATHER_BASED_PHOTOS,
                            GenericModelFactory.getGeneralTypeObject(AvailableLayouts.WEATHER_BASED_PHOTOS, /*Constants.Providers.POWERED_BY_UNSPLASH*/ season, false, photos.results))
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(TAG, "onFailed: $errors")
                    allData?.increaseResponseCount()
                }
            })
        }
        if (sequenceOfLayout.contains(AvailableLayouts.LOCATION_BASED_PHOTOS)) {
            val places = arrayListOf("Gujarat", "Orissa", "Bengaluru", "Hydrebad", "Kolkata",
                    "Hong Kong", "London", "Malaysia", "Punjab", "Mumbai", "Chennai", "Paris", "USA", "Sri Lanka",
                    "Russia", "Pakistan", "Bangladesh", "Tibet", "Berlin", "Bhutan", "Africa", "Australia", "England")
            val place = StringUtils.capitalize(places.random())
            unsplashHelper.getSearchedPhotos(place, 1, 20, object : GetUnsplashSearchedPhotosListener {
                override fun onSuccess(photos: SearchedPhotos) {
                    if(!photos.results.isNullOrEmpty()){
                        allData?.addSequentially(AvailableLayouts.LOCATION_BASED_PHOTOS,
                                GenericModelFactory.getGeneralTypeObject(AvailableLayouts.LOCATION_BASED_PHOTOS, /*Constants.Providers.POWERED_BY_UNSPLASH*/ place, false, photos.results))
                    }else
                        allData?.increaseResponseCount()
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(TAG, "onFailed: $errors")
                    allData?.increaseResponseCount()
                }
            })
        }
        if (sequenceOfLayout.contains(AvailableLayouts.TIME_BASED_PHOTOS)) {
            unsplashHelper.getSearchedPhotos("Night", 1, 20, object : GetUnsplashSearchedPhotosListener {
                override fun onSuccess(photos: SearchedPhotos) {
                    allData?.addSequentially(AvailableLayouts.TIME_BASED_PHOTOS,
                            GenericModelFactory.getGeneralTypeObject(AvailableLayouts.TIME_BASED_PHOTOS, Constants.Providers.POWERED_BY_UNSPLASH, false, photos.results))
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(TAG, "onFailed: $errors")
                    allData?.increaseResponseCount()
                }
            })
        }
        if (sequenceOfLayout.contains(AvailableLayouts.POPULAR_PHOTOS)) {
            unsplashHelper.getPhotosAndUsers(UnsplashHelper.ORDER_BY_POPULAR, object : GetUnsplashPhotosAndUsersListener {
                override fun onSuccess(photos: List<UnsplashPhotos>, userList: List<User>) {
                    allData?.addSequentially(AvailableLayouts.POPULAR_PHOTOS,
                            GenericModelFactory.getGeneralTypeObject(AvailableLayouts.POPULAR_PHOTOS, Constants.Providers.POWERED_BY_UNSPLASH, true, photos))
                    if (sequenceOfLayout.contains(AvailableLayouts.POPULAR_PHOTOGRAPHERS)) {
                        allData?.addSequentially(AvailableLayouts.POPULAR_PHOTOGRAPHERS,
                                GenericModelFactory.getUserTypeObject(AvailableLayouts.POPULAR_PHOTOGRAPHERS, Constants.Providers.POWERED_BY_UNSPLASH, false, userList))
                    }
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(TAG, "onFailed: $errors")
                    allData?.apply {
                        increaseResponseCount()
                        if (sequenceOfLayout.contains(AvailableLayouts.POPULAR_PHOTOGRAPHERS)) {
                            increaseResponseCount()
                        }
                    }
                }
            })
        }
        if (sequenceOfLayout.contains(AvailableLayouts.FEATURED_COLLECTIONS)) {
            unsplashHelper.getCollectionsAndTags(1, 10, object : GetUnsplashCollectionsAndTagsListener {
                override fun onSuccess(collection: List<Collections>, tagsList: List<Tag>) {
                    allData?.addSequentially(AvailableLayouts.FEATURED_COLLECTIONS,
                            GenericModelFactory.getCollectionTypeObject(AvailableLayouts.FEATURED_COLLECTIONS, Constants.Providers.POWERED_BY_UNSPLASH, true, collection))
                    if (sequenceOfLayout.contains(AvailableLayouts.POPULAR_TAGS)) {
                        allData?.addSequentially(AvailableLayouts.POPULAR_TAGS,
                                GenericModelFactory.getTagTypeObject(AvailableLayouts.POPULAR_TAGS, Constants.Providers.POWERED_BY_UNSPLASH, false, tagsList, true))
                    }
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(TAG, "onFailed: $errors")
                    allData?.apply {
                        increaseResponseCount()
                        if (sequenceOfLayout.contains(AvailableLayouts.POPULAR_TAGS)) {
                            increaseResponseCount()
                        }
                    }
                }
            })
        }
        if (sequenceOfLayout.contains(AvailableLayouts.BROWSE_BY_CATEGORIES)) {
            mContext?.apply {
                val categories = listOf<String>(*resources.getStringArray(R.array.array_categories_type))
                allData?.addSequentially(AvailableLayouts.BROWSE_BY_CATEGORIES, GenericModelFactory.getCategoryTypeObject(AvailableLayouts.BROWSE_BY_CATEGORIES,
                        Constants.Providers.POWERED_BY_UNSPLASH, false, GenericModelFactory.CategoryTypeModel.createModelFromStringList(categories), true))
            }?: allData?.increaseResponseCount()
        }
        if (sequenceOfLayout.contains(AvailableLayouts.BROWSE_BY_COLORS)) {
            mContext?.apply {
                val colorsList = listOf<String>(*resources.getStringArray(R.array.array_colors_type))
                allData!!.addSequentially(AvailableLayouts.BROWSE_BY_COLORS, GenericModelFactory.getColorTypeObject(AvailableLayouts.BROWSE_BY_COLORS, Constants.Providers.POWERED_BY_UNSPLASH,
                        false, GenericModelFactory.ColorTypeModel.createModelFromStringList(colorsList), true))
            }?: allData?.increaseResponseCount()
        }
        if (sequenceOfLayout.contains(AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES)) {
            unsplashHelper.getUserPhotos("rpnickson", 1, 20, object : GetUnsplashPhotosListener {
                override fun onSuccess(photos: List<UnsplashPhotos>) {
                    allData?.addSequentially(AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES, GenericModelFactory.getFavouritePhotographerTypeObject(
                            AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES, Constants.Providers.POWERED_BY_UNSPLASH, photos))
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(TAG, "onFailed: $errors")
                    allData?.increaseResponseCount()
                }
            })
        }
    }

    private fun initItemClicks(allContentModel: AllContentModel) {

        val bsDownload: BSDownload? = BSDownload()
        bsDownload?.isCancelable = false

        allContentModel.setOnClickListener(object : OnGroupItemClickListener{

            override fun onItemClick(o: Any?, childImgView: ImageView?, groupPos: Int, childPos: Int) {

                object : GenericModelCastHelper(o) {

                    override fun onCollectionType(collectionTypeModel: GenericModelFactory.CollectionTypeModel?) {
                        mListener?.startMorePhotosActivity(
                                MoreListData(
                                        Constants.ListModes.LIST_MODE_COLLECTION_PHOTOS,
                                        collectionInfo = collectionTypeModel?.collections?.get(childPos)
                                )
                        )
                    }

                    override fun onGeneralType(generalTypeModel: GenericModelFactory.GeneralTypeModel?) {
                        generalTypeModel?.apply {

                            ImageViewerHelper(mContext).with(photosList,
                                    childImgView, childPos, object : ImageViewerHelper.ImageActionListener() {

                                override fun onSetWallpaper(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(mContext, photoInfo.links.download, name, name, false, object : ImageActionHelper.ImageSaveListener{
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun response(imageMeta: SavedImageMeta?, msg: String) {
                                            bsDownload?.downloaded()
                                            ImageActionHelper.setWallpaper(mContext, imageMeta?.getUri())
                                        }
                                    })
                                }

                                override fun onDownload(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(mContext, photoInfo.links.download, name, name, false, object : ImageActionHelper.ImageSaveListener{
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun response(imageMeta: SavedImageMeta?, msg: String) {
                                            bsDownload?.downloaded()
                                            if(msg.isNotEmpty()){
                                                showToast(msg)
                                            }
                                        }
                                    })
                                }

                                override fun onFavourite(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(mContext, photoInfo.links.download, name, name, true, object : ImageActionHelper.ImageSaveListener{
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun response(imageMeta: SavedImageMeta?, msg: String) {
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

                    override fun onFavPhotographerType(favPhotographerTypeModel: GenericModelFactory.FavouritePhotographerTypeModel?) {
                        favPhotographerTypeModel?.apply {
                            ImageViewerHelper(mContext).with(photosList,
                                    childImgView, childPos, object : ImageViewerHelper.ImageActionListener() {
                                override fun onSetWallpaper(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(mContext, photoInfo.links.download, name, name, false, object : ImageActionHelper.ImageSaveListener{
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                            showToast(getString(R.string.download_started))
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                            showToast(getString(R.string.failed_to_download_no_internet))
                                        }

                                        override fun response(imageMeta: SavedImageMeta?, msg: String) {
                                            bsDownload?.downloaded()
                                            ImageActionHelper.setWallpaper(mContext, Uri.parse(imageMeta?.relativePath))
                                        }
                                    })
                                }

                                override fun onDownload(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(mContext, photoInfo.links.download, name, name, false, object : ImageActionHelper.ImageSaveListener{
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                            showToast(getString(R.string.download_started))
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun response(imageMeta: SavedImageMeta?, msg: String) {
                                            bsDownload?.downloaded()
                                            if(msg.isNotEmpty()){
                                                showToast(msg)
                                            }
                                        }
                                    })
                                }

                                override fun onFavourite(photoInfo: UnsplashPhotos, name: String) {
                                    ImageActionHelper.saveImage(mContext, photoInfo.links.download, name, name, true, object : ImageActionHelper.ImageSaveListener{
                                        override fun onDownloadStarted() {
                                            bsDownload?.downloadStarted(childFragmentManager)
                                            showToast(getString(R.string.adding_to_fav))
                                        }

                                        override fun onDownloadFailed() {
                                            bsDownload?.downloadError()
                                        }

                                        override fun response(imageMeta: SavedImageMeta?, msg: String) {
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

                    override fun onTagType(tagTypeModel: GenericModelFactory.TagTypeModel?) {
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

                    override fun onColorType(colorTypeModel: GenericModelFactory.ColorTypeModel?) {
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

                    override fun onCategoryType(categoryTypeModel: GenericModelFactory.CategoryTypeModel?) {
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

                    override fun onUserType(userTypeModel: GenericModelFactory.UserTypeModel?) {
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
                    override fun onCollectionType(collectionTypeModel: GenericModelFactory.CollectionTypeModel?) {
                        mListener?.startMoreFeaturedCollections(
                                MoreListData(
                                        Constants.ListModes.LIST_MODE_COLLECTIONS,
                                        generalInfo = MoreData(
                                                title
                                        )
                                )
                        )
                    }

                    override fun onGeneralType(generalTypeModel: GenericModelFactory.GeneralTypeModel?) {
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

    private fun convertToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }

    private fun getFormattedString(someNumber: Double): String {
        return String.format("%.1f", someNumber)
    }

    override fun onContentAddedCount(loadedCount: Int, totalCount: Int) {
        //TODO: calculate percentage
    }

    override fun onAllContentLoaded() {
        Handler().postDelayed({
            mListener?.getMaterialSnackBar()?.dismiss()
        }, 300)
    }

    override fun onResume() {
        super.onResume()
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
        val lastLocationDetails: Bundle?
        fun getMaterialSnackBar(): MaterialSnackBar?
        fun requestWeather(listener: WeatherResultListener?) //void getWeatherWallpaper();
        fun startMoreFeaturedCollections(data: MoreListData)
        fun startSettings()
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val TAG = "HomeFragment"

        fun newInstance(weatherDetails: WeatherModelClass?): FragHome {
            val fragment = FragHome()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, weatherDetails)
            fragment.arguments = args
            return fragment
        }
    }
}