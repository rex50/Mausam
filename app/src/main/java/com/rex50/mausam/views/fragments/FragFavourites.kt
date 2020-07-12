package com.rex50.mausam.views.fragments

import android.content.Context
import android.net.Uri
import android.view.WindowManager
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseFragment
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.GradientHelper
import kotlinx.android.synthetic.main.header_custom_general.*
import kotlinx.android.synthetic.main.anim_view.*

class FragFavourites : BaseFragment() {

    private var mListener: OnFragmentInteractionListener? = null

    override fun getResourceLayout(): Int = R.layout.frag_fav

    override fun initView() {

        mContext?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        gradientLine?.background = GradientHelper.getInstance(mContext)?.getRandomLeftRightGradient()
        tvPageTitle?.setText(R.string.favourites_title)
        tvPageDesc?.setText(R.string.favourites_desc)

        lnlError?.showView()
        animError?.apply {
            setAnimation(R.raw.l_anim_error_astronaout)
            scale = 0.2F
            speed = 0.8F
        }
        tvError?.text = getText(R.string.coming_soon)

    }

    override fun load() {
        //TODO
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
        val sharedPrefs: MausamSharedPrefs?
        val snackBar: MaterialSnackBar?
    }

}