package com.rex50.mausam.views.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.rex50.mausam.R;
import com.rex50.mausam.model_classes.weather.WeatherModelClass;
import com.rex50.mausam.network.APIManager;
import com.rex50.mausam.utils.DataParser;
import com.rex50.mausam.utils.MaterialSnackBar;
import com.rex50.mausam.utils.MausamSharedPrefs;
import com.rex50.mausam.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.HashMap;

import static com.rex50.mausam.utils.Utils.CITY_NOT_FOUND;
import static com.rex50.mausam.utils.Utils.PAGE_NOT_FOUND;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    boolean searchOnlyCity = false;

//    private ImageView startSearchBtn;
    private EditText eTxtSearch;
    private TextView txtError, txtPageTitle, txtPageDesc;
    private Button btnNext;
    private LinearLayout containerSearchProviders;

//    private LinearLayout searchFieldsHolder;
//    private MainActivity mainActivity;

//    private ImageView btn_back;

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
    public static SearchFragment newInstance(boolean searchOnlyCity) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, searchOnlyCity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchOnlyCity = getArguments().getBoolean(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getActivity() != null)
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        init(view);

        // Inflate the layout for this fragment
        return view;

    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(View v) {
        txtPageTitle = v.findViewById(R.id.txt_page_title);
        txtPageDesc = v.findViewById(R.id.txt_page_desc);
        containerSearchProviders = v.findViewById(R.id.container_search_providers);
        eTxtSearch = v.findViewById(R.id.etxt_search);
        txtError = v.findViewById(R.id.txt_error);
        btnNext = v.findViewById(R.id.btn_next_search);

        if(searchOnlyCity){
            containerSearchProviders.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setOnClickListener(v1 -> mListener.nextBtnClicked());
            txtPageTitle.setText(R.string.search_loc_title);
            txtPageDesc.setText(R.string.search_loc_desc);
            eTxtSearch.setHint(R.string.search_loc_box_hint);
        }

        eTxtSearch.setOnTouchListener((v1, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (eTxtSearch.getRight() - eTxtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if(searchOnlyCity)
                        validateCity(StringUtils.capitalize(eTxtSearch.getText().toString().trim()));
                    return true;
                }
            }
            return false;
        });

        eTxtSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if(searchOnlyCity)
                    validateCity(StringUtils.capitalize(eTxtSearch.getText().toString().trim()));
                return true;
            }
            return false;
        });

    }

    private void validateCity(String city) {
        Utils.validateText(city, new Utils.TextValidationInterface() {
            @Override
            public void correct() {
                eTxtSearch.setEnabled(false);
                txtError.setText("");
                startWeatherSearch(city);
            }

            @Override
            public void containNumber() {
                txtError.setText(R.string.contain_number_error_msg);
            }

            @Override
            public void containSpecialChars() {
                txtError.setText(R.string.contain_speciall_chars_error_msg);
            }

            @Override
            public void empty() {
                txtError.setText(R.string.empty_error_msg);
            }
        });
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

    private void startWeatherSearch(String place) {
        APIManager apiManager = APIManager.getInstance(getContext());
        HashMap<String, String> urlExtras = new HashMap<>();
        urlExtras.put("q", place);
        apiManager.searchWeather(APIManager.SERVICE_CURRENT_WEATHER, urlExtras, new APIManager.WeatherAPICallBackResponse() {
            @Override
            public void onWeatherResponseSuccess(JSONObject jsonObject) {
                if(searchOnlyCity){
                    mListener.getSharedPrefs().setLastWeatherData(jsonObject);
                    mListener.getSharedPrefs().setUserLocation(eTxtSearch.getText().toString());
                    mListener.onWeatherSearchSuccess(new DataParser().parseWeatherData(jsonObject));
                }
            }

            @Override
            public void onWeatherResponseFailure(int errorCode, String msg) {
                eTxtSearch.setEnabled(true);
                switch (errorCode){
                    case PAGE_NOT_FOUND : //TODO : page not found (show material snackbar)
                        mListener.getSnackBar().show("Something is wrong. Please try again", MaterialSnackBar.LENGTH_LONG);
                        break;

                    case CITY_NOT_FOUND :
                        setErrorMsg(String.format(getString(R.string.search_city_error), place));
                        if(getContext() != null) {
                            eTxtSearch.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.showSoftInput(eTxtSearch, InputMethodManager.SHOW_IMPLICIT);
                            }
                        }
                        break;

                    default :
                        if(getContext() != null) {
                            eTxtSearch.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.showSoftInput(eTxtSearch, InputMethodManager.SHOW_IMPLICIT);
                            }
                        }
                        break;
                }
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
        void onWeatherSearchSuccess(WeatherModelClass weatherDetails);
        void nextBtnClicked();
        MausamSharedPrefs getSharedPrefs();
        MaterialSnackBar getSnackBar();
    }
}
