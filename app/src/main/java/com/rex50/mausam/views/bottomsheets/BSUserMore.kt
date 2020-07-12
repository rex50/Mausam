package com.rex50.mausam.views.bottomsheets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.MaterialBottomSheet
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.utils.*
import kotlinx.android.synthetic.main.bs_user_more_info.*

class BSUserMore : MaterialBottomSheet(){

    companion object{
        const val TAG = "BSUserMore"
    }

    private lateinit var user: User

    override fun layoutId(): Int = R.layout.bs_user_more_info

    fun initAndShow(fragmentManager: FragmentManager, user: User){
        show(fragmentManager, TAG)
        this.user = user
    }

    override fun onViewReady(view: View, savedInstanceState: Bundle?) {
        bindUserInfo()
    }

    private fun bindUserInfo(){
        user.apply {
            btnUserPortfolio?.apply {
                if (portfolioUrl.isNullOrEmpty()) hideView() else {
                    showView()
                    setOnClickListener {
                        context?.openUrl(portfolioUrl)
                    }
                }
            }
            btnUserInstagram?.apply {
                if (instagramUsername.isNullOrEmpty()) hideView() else {
                    showView()
                    setOnClickListener {
                        context?.openInstagramProfile(instagramUsername)
                    }
                }
            }
            btnUserTwitter?.apply {
                if (twitterUsername.isNullOrEmpty()) hideView() else {
                    showView()
                    setOnClickListener {
                        context?.openTwitterProfile(twitterUsername)
                    }
                }
            }
            btnUserVisitThisPage?.apply {
                if (links?.html.isNullOrEmpty()) hideView() else {
                    showView()
                    setOnClickListener {
                        context?.openUrl(links?.html.toString())
                    }
                }
            }
        }
    }

}