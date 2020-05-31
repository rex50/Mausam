package com.rex50.mausam.views.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseFragment
import com.rex50.mausam.model_classes.weather.WeatherModelClass
import com.rex50.mausam.network.APIManager
import com.rex50.mausam.network.APIManager.WeatherAPICallBackResponse
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Utils.TextValidationInterface
import kotlinx.android.synthetic.main.frag_search.*
import kotlinx.android.synthetic.main.header_custom_general.*
import org.apache.commons.lang3.StringUtils
import org.json.JSONObject
import java.util.*

class FragSearch : BaseFragment() {
    var searchOnlyCity = false

    private var mListener: OnFragmentInteractionListener? = null

    override fun getResourceLayout(): Int = R.layout.frag_search

    override fun initView() {

        arguments?.getBoolean(ARG_PARAM1)?.apply {
            searchOnlyCity = this
        }

        mContext?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        if (resources.getBoolean(R.bool.hide_search_providers)) {
            containerSearchProviders?.hideView()
            txtPageTitle?.setText(R.string.search_wallpaper_title)
            txtPageDesc?.setText(R.string.search_wallpaper_desc)
            etvSearch?.setHint(R.string.search_box_hint)
        }

        if (searchOnlyCity) {
            containerSearchProviders?.hideView()
            btnNextSearch?.apply {
                showView()
                setOnClickListener { mListener?.nextBtnClicked() }
            }
            txtPageTitle?.setText(R.string.search_loc_title)
            txtPageDesc?.setText(R.string.search_loc_desc)
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
        //TODO
        // can show some tags to search
    }

    private fun onSearchClickAction() {
        if (searchOnlyCity)
            validateCity(StringUtils.capitalize(etvSearch?.text.toString().trim()))
        else
            searchWallpapers()
    }

    private fun searchWallpapers() {
        val searchTerm = etvSearch?.text.toString()
        mListener?.startMorePhotosActivity(searchTerm)
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
        apiManager.searchWeather(APIManager.SERVICE_CURRENT_WEATHER, urlExtras, object : WeatherAPICallBackResponse {
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
                    Utils.PAGE_NOT_FOUND -> mListener?.snackBar?.show("Something is wrong. Please try again", MaterialSnackBar.LENGTH_LONG)
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
        fun startMorePhotosActivity(searchTerm: String?)
        val sharedPrefs: MausamSharedPrefs?
        val snackBar: MaterialSnackBar?
    }

    companion object {
        private const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(searchOnlyCity: Boolean): FragSearch {
            val fragment = FragSearch()
            val args = Bundle()
            args.putBoolean(ARG_PARAM1, searchOnlyCity)
            fragment.arguments = args
            return fragment
        }
    }
}