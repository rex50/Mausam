package com.rex50.mausam.views.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.rex50.mausam.model_classes.weather.WeatherModelClass
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.Constants
import com.rex50.mausam.utils.Constants.AvailableLayouts
import com.rex50.mausam.utils.Utils
import com.rex50.mausam.utils.hideView
import com.rex50.mausam.utils.showView
import com.rex50.mausam.views.adapters.HomeAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_weather_card.*
import org.json.JSONArray
import java.util.*

class HomeFragment : BaseFragment() {
    private var allData: AllContentModel? = null
    private var sequenceOfLayout: MutableList<String> = ArrayList()
    private var homeAdapter: HomeAdapter? = null

    private var mWeatherDetails: WeatherModelClass? = null
    private val mParam2: String? = null
    private var mListener: OnFragmentInteractionListener? = null


    override fun getResourceLayout(): Int = R.layout.fragment_home

    override fun initView() {
        arguments?.getSerializable(ARG_PARAM1)?.apply {
            mWeatherDetails = this as WeatherModelClass?
        }
    }

    override fun load() {
        initWallpapers()
    }

    private fun initWallpapers(){
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
                    weatherWallpapersAdapter = new WeatherWallpapersAdapter(getContext(), photos.getResults());
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
                    locationWallpaperAdapter = new WeatherWallpapersAdapter(getContext(), photos.getResults());
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
            if (Utils.getConnectivityStatus(activity) != Utils.TYPE_NOT_CONNECTED) {
                mListener?.startSearchScreen()
            } else {
                Toast.makeText(activity, "Please connect to internet first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSequenceOfLayout(): List<String> = sequenceOfLayout.apply {
        clear()
        add(AvailableLayouts.WEATHER_BASED_WALLPAPERS)
        add(AvailableLayouts.LOCATION_BASED_WALLPAPERS)
        add(AvailableLayouts.TIME_BASED_WALLPAPERS)
        add(AvailableLayouts.POPULAR_WALLPAPERS)
        add(AvailableLayouts.POPULAR_PHOTOGRAPHERS)
        add(AvailableLayouts.FEATURED_COLLECTIONS)
        add(AvailableLayouts.POPULAR_TAGS)
        add(AvailableLayouts.BROWSE_BY_COLORS)
        add(AvailableLayouts.BROWSE_BY_CATEGORIES)
        add(AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES)
    }

    private fun prepareHomeRecycler(sequenceOfLayout: List<String>) {
        val unsplashHelper = UnsplashHelper(activity)
        allData = AllContentModel(activity)
        homeAdapter = HomeAdapter(activity, allData)
        recHomeContent?.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = homeAdapter
        }

        allData?.apply {
            setSequenceOfLayouts(sequenceOfLayout)
            setAdapter(homeAdapter)
            setOnClickListener()
        }

        if (sequenceOfLayout.contains(AvailableLayouts.WEATHER_BASED_WALLPAPERS)) {
            unsplashHelper.getSearchedPhotos("Summer", 1, 20, object : GetUnsplashSearchedPhotosListener {
                override fun onSuccess(photos: SearchedPhotos) {
                    allData?.addSequentially(AvailableLayouts.WEATHER_BASED_WALLPAPERS,
                            GenericModelFactory.getGeneralTypeObject(AvailableLayouts.WEATHER_BASED_WALLPAPERS, Constants.Providers.POWERED_BY_UNSPLASH, false, photos.results))
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(TAG, "onFailed: $errors")
                }
            })
        }
        if (sequenceOfLayout.contains(AvailableLayouts.LOCATION_BASED_WALLPAPERS)) {
            unsplashHelper.getSearchedPhotos("Surat", 1, 20, object : GetUnsplashSearchedPhotosListener {
                override fun onSuccess(photos: SearchedPhotos) {
                    allData?.addSequentially(AvailableLayouts.LOCATION_BASED_WALLPAPERS,
                            GenericModelFactory.getGeneralTypeObject(AvailableLayouts.LOCATION_BASED_WALLPAPERS, Constants.Providers.POWERED_BY_UNSPLASH, false, photos.results))
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(TAG, "onFailed: $errors")
                }
            })
        }
        if (sequenceOfLayout.contains(AvailableLayouts.TIME_BASED_WALLPAPERS)) {
            unsplashHelper.getSearchedPhotos("Night", 1, 20, object : GetUnsplashSearchedPhotosListener {
                override fun onSuccess(photos: SearchedPhotos) {
                    allData?.addSequentially(AvailableLayouts.TIME_BASED_WALLPAPERS,
                            GenericModelFactory.getGeneralTypeObject(AvailableLayouts.TIME_BASED_WALLPAPERS, Constants.Providers.POWERED_BY_UNSPLASH, false, photos.results))
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(TAG, "onFailed: $errors")
                }
            })
        }
        if (sequenceOfLayout.contains(AvailableLayouts.POPULAR_WALLPAPERS)) {
            unsplashHelper.getPhotosAndUsers(UnsplashHelper.ORDER_BY_DEFAULT, object : GetUnsplashPhotosAndUsersListener {
                override fun onSuccess(photos: List<UnsplashPhotos>, userList: List<User>) {
                    allData?.addSequentially(AvailableLayouts.POPULAR_WALLPAPERS,
                            GenericModelFactory.getGeneralTypeObject(AvailableLayouts.POPULAR_WALLPAPERS, Constants.Providers.POWERED_BY_UNSPLASH, true, photos))
                    if (sequenceOfLayout.contains(AvailableLayouts.POPULAR_PHOTOGRAPHERS)) {
                        allData!!.addSequentially(AvailableLayouts.POPULAR_PHOTOGRAPHERS,
                                GenericModelFactory.getUserTypeObject(AvailableLayouts.POPULAR_PHOTOGRAPHERS, Constants.Providers.POWERED_BY_UNSPLASH, false, userList))
                    }
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(TAG, "onFailed: $errors")
                }
            })
        }
        if (sequenceOfLayout.contains(AvailableLayouts.FEATURED_COLLECTIONS)) {
            unsplashHelper.getCollectionsAndTags(1, 10, object : GetUnsplashCollectionsAndTagsListener {
                override fun onSuccess(collection: List<Collections>, tagsList: List<Tag>) {
                    allData!!.addSequentially(AvailableLayouts.FEATURED_COLLECTIONS,
                            GenericModelFactory.getCollectionTypeObject(AvailableLayouts.FEATURED_COLLECTIONS, Constants.Providers.POWERED_BY_UNSPLASH, false, collection))
                    if (sequenceOfLayout.contains(AvailableLayouts.POPULAR_TAGS)) {
                        allData?.addSequentially(AvailableLayouts.POPULAR_TAGS,
                                GenericModelFactory.getTagTypeObject(AvailableLayouts.POPULAR_TAGS, Constants.Providers.POWERED_BY_UNSPLASH, false, tagsList, true))
                    }
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(TAG, "onFailed: $errors")
                }
            })
        }
        if (sequenceOfLayout.contains(AvailableLayouts.BROWSE_BY_CATEGORIES)) {
            activity?.apply {
                val categories = listOf<String>(*resources.getStringArray(R.array.categories))
                allData?.addSequentially(AvailableLayouts.BROWSE_BY_CATEGORIES, GenericModelFactory.getCategoryTypeObject(AvailableLayouts.BROWSE_BY_CATEGORIES,
                        Constants.Providers.POWERED_BY_UNSPLASH, false, categories, true))
            }
        }
        if (sequenceOfLayout.contains(AvailableLayouts.BROWSE_BY_COLORS)) {
            activity?.apply {
                val colorsList = listOf<String>(*resources.getStringArray(R.array.colors))
                allData!!.addSequentially(AvailableLayouts.BROWSE_BY_COLORS, GenericModelFactory.getColorTypeObject(AvailableLayouts.BROWSE_BY_COLORS, Constants.Providers.POWERED_BY_UNSPLASH,
                        false, GenericModelFactory.ColorTypeModel.createModelFromStringList(colorsList), true))
            }
        }
        if (sequenceOfLayout.contains(AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES)) {
            unsplashHelper.getUserPhotos("rpnickson", object : GetUnsplashPhotosListener {
                override fun onSuccess(photos: List<UnsplashPhotos>) {
                    allData?.addSequentially(AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES, GenericModelFactory.getFavouritePhotographerTypeObject(
                            AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES, Constants.Providers.POWERED_BY_UNSPLASH, photos))
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(TAG, "onFailed: $errors")
                }
            })
        }
    }

    private fun convertToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }

    private fun getFormattedString(someNumber: Double): String {
        return String.format("%.1f", someNumber)
    }

    /*private EndlessRecyclerOnScrollListener endlessListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadMore() {

        }
    };*/
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri?)

        fun startSearchScreen()
        val lastLocationDetails: Bundle?
        fun requestWeather(listener: WeatherResultListener?) //        void getWeatherWallpaper();
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val TAG = "HomeFragment"

        fun newInstance(weatherDetails: WeatherModelClass?): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, weatherDetails)
            fragment.arguments = args
            return fragment
        }
    }
}