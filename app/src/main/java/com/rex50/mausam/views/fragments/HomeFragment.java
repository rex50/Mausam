package com.rex50.mausam.views.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rex50.mausam.interfaces.GetUnsplashSearchedPhotosListener;
import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos;
import com.rex50.mausam.model_classes.weather.WeatherModelClass;
import com.rex50.mausam.R;
import com.rex50.mausam.network.UnsplashHelper;
import com.rex50.mausam.utils.Utils;
import com.rex50.mausam.views.adapters.WeatherWallpapersAdapter;

import org.json.JSONArray;

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
    public static HomeFragment newInstance(WeatherModelClass weatherDetails/*, String param2*/) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, weatherDetails);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null) {
//            //Restore the fragment's instance
//            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
//        }
        if (getArguments() != null) {
            mWeatherDetails = (WeatherModelClass) getArguments().getSerializable(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
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
//        txtWeatherMsg = view.findViewById(R.id.txt_weather_msg);
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
        weatherWallpaperRecycler = view.findViewById(R.id.weather_wallpapers_recycler);
        locationWallpaperRecycler = view.findViewById(R.id.location_wallpapers_recycler);

        Bundle bundle = mListener.getLastLocationDetails();
        String location = "null", city = "", state = "", country = "";
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

//            Log.d(TAG, "location: " + location);

        }

        UnsplashHelper unsplashHelper = new UnsplashHelper(getContext());
        /*unsplashHelper.getPhotos(UnsplashHelper.ORDER_BY_DEFAULT, new GetUnsplashPhotosListener() {
            @Override
            public void onSuccess(List<UnsplashPhotos> photos) {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onFailed(JSONArray errors) {

            }
        });*/
        unsplashHelper.getSearchedPhotos(mWeatherDetails.getWeather().get(0).getMain() + " weather", 1, 20, new GetUnsplashSearchedPhotosListener() {
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

        unsplashHelper.getSearchedPhotos(location.isEmpty() && location.contains("null")? mWeatherDetails.getName() : location, 1, 20, new GetUnsplashSearchedPhotosListener() {
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
        });



        //for celsius = kelvin - 273.15
//        Double temp = mWeatherDetails.getMain().getTemp() - 273.15;
        txtWeather.setText(getFormattedString(convertToCelsius(mWeatherDetails.getMain().getTemp())));
//        txtWeatherMsg.setText(mWeatherDetails.getWeather().get(0).getDescription());
        if(!location.isEmpty()){
            txtLocation.setText(location);
        }else {
            txtLocation.setText(mWeatherDetails.getName());
        }
        txtWeatherDesc.setText(mWeatherDetails.getWeather().get(0).getMain());
        txtMaxTemp.setText(getFormattedString(convertToCelsius(mWeatherDetails.getMain().getTempMax())));
        txtMinTemp.setText(getFormattedString(convertToCelsius(mWeatherDetails.getMain().getTempMin())));
        txtPressure.setText(mWeatherDetails.getMain().getPressure().toString());
        txtHumidity.setText(mWeatherDetails.getMain().getHumidity().toString());
        txtWindSpeed.setText(mWeatherDetails.getWind().getSpeed().toString());
        txtWindDegree.setText(mWeatherDetails.getWind().getDeg() + "");


//        Animation cardAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_weather_card);
//        weatherDetailsHolder.startAnimation(cardAnimation);
        weatherDetailsHolder.setOnClickListener(v -> {

        });

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

    private Double convertToCelsius(Double kelvin){
        return kelvin - 273.15;
    }

    private String getFormattedString(Double someNumber){
        return String.format("%.1f", someNumber);
    }

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
//        void getWeatherWallpaper();

    }

}
