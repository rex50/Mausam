package com.rex50.mausam.Views.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.rex50.mausam.ModelClasses.WeatherModelClass;
import com.rex50.mausam.Network.APIManager;
import com.rex50.mausam.R;
import com.rex50.mausam.Utils.Utils;
import com.rex50.mausam.Views.Activities.MainActivity;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView startSearchBtn;
    private EditText searchETxt;
    private TextView txtError;

    private LinearLayout searchFieldsHolder;
    private MainActivity mainActivity;

    private ImageView btn_back;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        init(view);

        // Inflate the layout for this fragment
        return view;

    }

//    public void setMaterialSnackBar(MaterialSnackBar materialSnackBar){
//        this.materialSnackBar = materialSnackBar;
//    }

    private void init(View v) {
        mainActivity = (MainActivity) getActivity();
        searchETxt = v.findViewById(R.id.etxt_search);
        startSearchBtn = v.findViewById(R.id.btn_start_search);
        txtError = v.findViewById(R.id.txt_error);
        searchFieldsHolder = v.findViewById(R.id.searchFieldsHolder);
        btn_back = v.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v12 -> mListener.goBack());
        Animation cardAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_weather_card);
        searchFieldsHolder.startAnimation(cardAnimation);
        startSearchBtn.setOnClickListener(v1 -> Utils.validateText(searchETxt.getText().toString(), new Utils.TextValidationInterface() {
            @Override
            public void correct() {
                startSearch(searchETxt.getText().toString().trim());
            }

            @Override
            public void cointainNumber() {
                txtError.setText("Please write a location name without Number");
            }

            @Override
            public void cointainSpecialChars() {
                txtError.setText("Please write a location name without using space or special characters");
            }

            @Override
            public void empty() {
                txtError.setText("Please enter location name to continue");
            }
        }));
    }

    public void setErrorMsg(String msg){
        txtError.setText(msg);
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

    private void startSearch(String place) {
        APIManager apiManager = APIManager.getInstance(getContext());
        HashMap<String, String> urlExtras = new HashMap<>();
        urlExtras.put("q", place);
        apiManager.searchWeather(APIManager.SERVICE_CURRENT_WEATHER, urlExtras, new APIManager.CallBackResponse() {
            @Override
            public void onWeatherResponseSuccess(WeatherModelClass weatherDetails) {
                mListener.onSearchSuccess(weatherDetails);
            }

            @Override
            public void onWeatherResponseFailure(String msg) {
                setErrorMsg(msg);
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onSearchSuccess(WeatherModelClass weatherDetails);
        void goBack();
    }
}
