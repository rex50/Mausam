package com.rex50.mausam.views.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.utils.MaterialSnackBar
import com.rex50.mausam.views.fragments.discover.FragDiscover

class ActSearchCity : BaseActivity(), FragDiscover.OnFragmentInteractionListener {
    private var fragmentManager: FragmentManager? = supportFragmentManager
    private var fragDiscover: FragDiscover? = null

    override val layoutResource: Int
        get() = R.layout.act_search_city

    override fun loadAct(savedInstanceState: Bundle?) {
        //TODO: create a new fragment for searching place (city) for weather
        /*fragDiscover = newInstance(true)
        loadFragment(fragDiscover)*/
    }

    override fun internetStatus(internetType: Int) { //TODO : handle according to internet Status
    }

    private fun loadFragment(fragment: Fragment?) {
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragment?.apply {
            fragmentTransaction?.apply {
                add(R.id.search_city_fragment_placeholder, fragment, fragment.tag)
                commitAllowingStateLoss()
            }
        }
    }

    override fun onFragmentInteraction(uri: Uri?) {}
    //override fun onWeatherSearchSuccess(weatherDetails: WeatherModelClass?) {}
    override fun nextBtnClicked() {
        val intent = Intent(this, ActMain::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    //override val sharedPrefs: MausamSharedPrefs? = mausamSharedPrefs

    override val snackBar: MaterialSnackBar? = materialSnackBar

    override fun startMorePhotosActivity(data: MoreListData) {
        //Nothing
    }

    override fun startMoreFeaturedCollections(moreListData: MoreListData) {
        //Nothing
    }
}