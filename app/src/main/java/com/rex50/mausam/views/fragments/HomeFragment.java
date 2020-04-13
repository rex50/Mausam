package com.rex50.mausam.views.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rex50.mausam.R;
import com.rex50.mausam.interfaces.GetUnsplashPhotosListener;
import com.rex50.mausam.interfaces.GetUnsplashSearchedPhotosListener;
import com.rex50.mausam.interfaces.WeatherResultListener;
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;
import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos;
import com.rex50.mausam.model_classes.utils.AllContentModel;
import com.rex50.mausam.model_classes.utils.GenericModelFactory;
import com.rex50.mausam.model_classes.weather.WeatherModelClass;
import com.rex50.mausam.network.UnsplashHelper;
import com.rex50.mausam.utils.Utils;
import com.rex50.mausam.views.adapters.HomeAdapter;
import com.rex50.mausam.views.adapters.WeatherWallpapersAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.rex50.mausam.utils.Constants.AvailableLayouts.BROWSE_BY_CATEGORIES;
import static com.rex50.mausam.utils.Constants.AvailableLayouts.BROWSE_BY_COLORS;
import static com.rex50.mausam.utils.Constants.AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES;
import static com.rex50.mausam.utils.Constants.AvailableLayouts.FEATURED_COLLECTIONS;
import static com.rex50.mausam.utils.Constants.AvailableLayouts.LOCATION_BASED_WALLPAPERS;
import static com.rex50.mausam.utils.Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS;
import static com.rex50.mausam.utils.Constants.AvailableLayouts.POPULAR_TAGS;
import static com.rex50.mausam.utils.Constants.AvailableLayouts.POPULAR_WALLPAPERS;
import static com.rex50.mausam.utils.Constants.AvailableLayouts.TIME_BASED_WALLPAPERS;
import static com.rex50.mausam.utils.Constants.AvailableLayouts.WEATHER_BASED_WALLPAPERS;
import static com.rex50.mausam.utils.Constants.Providers.POWERED_BY_UNSPLASH;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "HomeFragment";

    private LinearLayout weatherDetailsHolder, extraWeatherDetailsHolder;
    private ImageView btnSearch;
    private TextView txtWeather,txtWeatherDesc, txtLocation, txtWeatherMsg, txtMaxTemp,
            txtMinTemp, txtPressure, txtHumidity, txtWindSpeed, txtWindDegree, btnWeatherMoreDetails;
    private RecyclerView weatherWallpaperRecycler, locationWallpaperRecycler;
    private WeatherWallpapersAdapter weatherWallpapersAdapter, locationWallpaperAdapter;

    private AllContentModel allData;

    private List<String> sequenceOfLayout;

    private RecyclerView mainContentRecycler;

    // TODO: Rename and change types of parameters
    private WeatherModelClass mWeatherDetails;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View view;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(WeatherModelClass weatherDetails) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, weatherDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWeatherDetails = (WeatherModelClass) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        init();

        return view;
    }

    private void init() {
        txtWeather = view.findViewById(R.id.txt_current_weather);
        txtLocation = view.findViewById(R.id.txt_current_location);
        txtWeatherDesc = view.findViewById(R.id.txt_weather_description);
        txtPressure = view.findViewById(R.id.txt_pressure);
        txtHumidity = view.findViewById(R.id.txt_humidity);
        txtMaxTemp = view.findViewById(R.id.txt_max_temp);
        txtMinTemp = view.findViewById(R.id.txt_min_temp);
        txtWindDegree = view.findViewById(R.id.txt_wind_degree);
        txtWindSpeed = view.findViewById(R.id.txt_wind_speed);
        btnSearch = view.findViewById(R.id.btn_search);
        btnWeatherMoreDetails = view.findViewById(R.id.btn_more_weather_details);
        weatherDetailsHolder = view.findViewById(R.id.weather_details_holder);
        extraWeatherDetailsHolder = view.findViewById(R.id.weather_extra_info_holder);
        mainContentRecycler = view.findViewById(R.id.main_content);
        allData = new AllContentModel();
        sequenceOfLayout = new ArrayList<>();
        sequenceOfLayout.add(WEATHER_BASED_WALLPAPERS);
        sequenceOfLayout.add(LOCATION_BASED_WALLPAPERS);
        sequenceOfLayout.add(TIME_BASED_WALLPAPERS);
        sequenceOfLayout.add(POPULAR_WALLPAPERS);

        prepareHomeRecycler(sequenceOfLayout);


        //weatherWallpaperRecycler = view.findViewById(R.id.weather_wallpapers_recycler);
        //locationWallpaperRecycler = view.findViewById(R.id.location_wallpapers_recycler);

        String location = "null", city = "", state = "", country = "";
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
        unsplashHelper.getSearchedPhotos(*//*location.isEmpty() && location.contains("null")? mWeatherDetails.getName() : location*//* "Surat", 1, 20, new GetUnsplashSearchedPhotosListener() {
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

        try {
            //for celsius = kelvin - 273.15
//        Double temp = mWeatherDetails.getMain().getTemp() - 273.15;
            //txtWeather.setText(getFormattedString(convertToCelsius(mWeatherDetails.getMain().getTemp())));
//        txtWeatherMsg.setText(mWeatherDetails.getWeather().get(0).getDescription());
            if (!location.isEmpty()) {
                txtLocation.setText(location);
            } else {
                txtLocation.setText(mWeatherDetails.getName());
            }
           /* txtWeatherDesc.setText(mWeatherDetails.getWeather().get(0).getMain());
            txtMaxTemp.setText(getFormattedString(convertToCelsius(mWeatherDetails.getMain().getTempMax())));
            txtMinTemp.setText(getFormattedString(convertToCelsius(mWeatherDetails.getMain().getTempMin())));
            txtPressure.setText(mWeatherDetails.getMain().getPressure().toString());
            txtHumidity.setText(mWeatherDetails.getMain().getHumidity().toString());
            txtWindSpeed.setText(mWeatherDetails.getWind().getSpeed().toString());
            txtWindDegree.setText(mWeatherDetails.getWind().getDeg() + "");*/


//        Animation cardAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_weather_card);
//        weatherDetailsHolder.startAnimation(cardAnimation);
        /*weatherDetailsHolder.setOnClickListener(v -> {

        });*/
        }catch (Exception e){
            Log.e(TAG, "Error setting text to views: ", e);
        }

        /*AllContentModel model = new AllContentModel();
        model.addAllModel(AllContentModel.getDummyModelsList(20));
        HomeAdapter adapter = new HomeAdapter(getContext(), model);
        mainContentRecycler.setLayoutManager(new LinearLayoutManager( getContext()));
        mainContentRecycler.setHasFixedSize(true);
        mainContentRecycler.setAdapter(adapter);*/


        //mainContentRecycler.addOnScrollListener(endlessListener);

        /*SearchedPhotos photos = new SearchedPhotos();

        RendererRecyclerViewAdapter adapter = new RendererRecyclerViewAdapter();
        adapter.registerRenderer(new ViewBinder(R.layout.item_category, SearchedPhotos.class, (photos, finder, payloads) -> {
            finder.setText(R.id.txt_category_title)
        }));*/

        btnWeatherMoreDetails.setOnClickListener(v -> {
            if(extraWeatherDetailsHolder.getVisibility() == View.VISIBLE){
                extraWeatherDetailsHolder.setVisibility(View.GONE);
                btnWeatherMoreDetails.setText("Show more details");
            }else {
                extraWeatherDetailsHolder.setVisibility(View.VISIBLE);
                btnWeatherMoreDetails.setText("Show less details");
            }
        });

        btnSearch.setOnClickListener(v -> {
            if(Utils.getConnectivityStatus(getContext()) != Utils.TYPE_NOT_CONNECTED){
                mListener.startSearchScreen();
            }else{
                Toast.makeText(getContext(), "Please connect to internet first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prepareHomeRecycler(List<String> sequenceOfLayout){

        UnsplashHelper unsplashHelper = new UnsplashHelper(getContext());

        HomeAdapter adapter = new HomeAdapter(getContext(), allData);
        mainContentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mainContentRecycler.setHasFixedSize(true);
        mainContentRecycler.setAdapter(adapter);
        //TODO: fetch and add data to list

        if(sequenceOfLayout.contains(WEATHER_BASED_WALLPAPERS)){
            unsplashHelper.getSearchedPhotos("Summer", 1, 20, new GetUnsplashSearchedPhotosListener() {
                @Override
                public void onSuccess(SearchedPhotos photos) {
                    Integer pos = addSequentially(WEATHER_BASED_WALLPAPERS,
                            GenericModelFactory.getGeneralTypeObject(WEATHER_BASED_WALLPAPERS, POWERED_BY_UNSPLASH, false, photos.getResults()));
                    if(pos != null){
                        adapter.notifyItemInserted(pos);
                    }
                }

                @Override
                public void onFailed(JSONArray errors) {
                    Log.e(TAG, "onFailed: " + errors);
                }
            });
        }

        if(sequenceOfLayout.contains(LOCATION_BASED_WALLPAPERS)){
            unsplashHelper.getSearchedPhotos("Surat", 1, 20, new GetUnsplashSearchedPhotosListener() {
                @Override
                public void onSuccess(SearchedPhotos photos) {
                    Integer pos = addSequentially(LOCATION_BASED_WALLPAPERS,
                            GenericModelFactory.getGeneralTypeObject(LOCATION_BASED_WALLPAPERS, POWERED_BY_UNSPLASH, false, photos.getResults()));
                    if(pos != null){
                        adapter.notifyItemInserted(pos);
                    }
                }

                @Override
                public void onFailed(JSONArray errors) {
                    Log.e(TAG, "onFailed: " + errors);
                }
            });
        }

        if(sequenceOfLayout.contains(TIME_BASED_WALLPAPERS)){
            unsplashHelper.getSearchedPhotos("Night", 1, 20, new GetUnsplashSearchedPhotosListener() {
                @Override
                public void onSuccess(SearchedPhotos photos) {
                    Integer pos = addSequentially(TIME_BASED_WALLPAPERS,
                            GenericModelFactory.getGeneralTypeObject(TIME_BASED_WALLPAPERS, POWERED_BY_UNSPLASH, false, photos.getResults()));
                    if(pos != null){
                        adapter.notifyItemInserted(pos);
                    }
                }

                @Override
                public void onFailed(JSONArray errors) {
                    Log.e(TAG, "onFailed: " + errors);
                }
            });
        }

        if(sequenceOfLayout.contains(POPULAR_WALLPAPERS)){
            unsplashHelper.getPhotos(UnsplashHelper.ORDER_BY_DEFAULT, new GetUnsplashPhotosListener() {
                @Override
                public void onSuccess(List<UnsplashPhotos> photos) {
                    Integer pos = addSequentially(TIME_BASED_WALLPAPERS,
                            GenericModelFactory.getGeneralTypeObject(POPULAR_WALLPAPERS, POWERED_BY_UNSPLASH,false, photos));
                    if(pos != null){
                        adapter.notifyItemInserted(pos);
                    }
                }

                @Override
                public void onFailed(JSONArray errors) {
                    Log.e(TAG, "onFailed: " + errors);
                }
            });
        }

        if(sequenceOfLayout.contains(POPULAR_PHOTOGRAPHERS)){
            //TODO: DO API call
        }

        if(sequenceOfLayout.contains(POPULAR_TAGS)){
            //TODO: DO API call
        }

        if(sequenceOfLayout.contains(FEATURED_COLLECTIONS)){
            //TODO: DO API call
        }

        if(sequenceOfLayout.contains(BROWSE_BY_CATEGORIES)){
            //TODO: DO API call
        }

        if(sequenceOfLayout.contains(BROWSE_BY_COLORS)){
            //TODO: DO API call
        }

        if(sequenceOfLayout.contains(FAVOURITE_PHOTOGRAPHER_IMAGES)){
            //TODO: DO API call
        }

    }

    private synchronized Integer addSequentially(String type, GenericModelFactory model){
        try {
            if(allData.size() == 0) {
                allData.addModel(type, model);
                return 0;
            }
            int pos = sequenceOfLayout.indexOf(type);
            for (int i = 0; i < allData.size(); i++) {
                int addedPos = sequenceOfLayout.indexOf(allData.getType(i));
                if(addedPos > pos){
                    allData.addModel(pos, type, model);
                    return i;
                }
            }
            allData.addModel(type, model);
            return allData.size() - 1;
        }catch (Exception e){
            Log.e(TAG, "addSequentially: ", e);
            return null;
        }
    }

    private Double convertToCelsius(Double kelvin){
        return kelvin - 273.15;
    }

    private String getFormattedString(Double someNumber){
        return String.format("%.1f", someNumber);
    }

    /*private EndlessRecyclerOnScrollListener endlessListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadMore() {

        }
    };*/

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void startSearchScreen();
        Bundle getLastLocationDetails();
        void requestWeather(WeatherResultListener listener);
//        void getWeatherWallpaper();

    }

}
