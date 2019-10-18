package com.rex50.mausam.Views.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rex50.mausam.ModelClasses.WeatherModelClass;
import com.rex50.mausam.R;

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

    private LinearLayout weatherDetailsHolder;
    private ImageView btnSearch;
    private TextView txtWeather,txtWeatherDesc, txtLocation, txtWeatherMsg;

    private Double latitude = null,longitude = null;

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
        txtWeatherMsg = view.findViewById(R.id.txt_weather_msg);

        btnSearch = view.findViewById(R.id.btn_search);
        weatherDetailsHolder = view.findViewById(R.id.weather_details_holder);

        //for celsius = kelvin - 273.15
        Double temp = mWeatherDetails.getMain().getTemp() - 273.15;
        txtWeather.setText(String.format("%.1f", temp));
        txtWeatherMsg.setText(mWeatherDetails.getWeather().get(0).getDescription());
        txtLocation.setText(mWeatherDetails.getName());
        txtWeatherDesc.setText(mWeatherDetails.getWeather().get(0).getMain());

        Animation cardAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_weather_card);
        weatherDetailsHolder.startAnimation(cardAnimation);

        btnSearch.setOnClickListener(v -> mListener.startSearchScreen());
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
    }

}
