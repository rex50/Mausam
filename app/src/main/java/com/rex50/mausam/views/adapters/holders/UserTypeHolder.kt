package com.rex50.mausam.views.adapters.holders

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.utils.getTextOrEmpty
import com.rex50.mausam.utils.loadImage
import com.thekhaeng.pushdownanim.PushDownAnim

class UserTypeHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
    private var userImg: ImageView? = v.findViewById(R.id.ivUser)
    private var txtUserName: TextView? = v.findViewById(R.id.tvUserName)
    private var userLayout: LinearLayout? = v.findViewById(R.id.lnlUser)

    fun bind(userModel: User?, isDataSaverMode: Boolean) {
        val name = userModel?.firstName.getTextOrEmpty() + " " + userModel?.lastName.getTextOrEmpty()

        txtUserName?.text = name

        userImg?.loadImage(userModel?.profileImage?.getLarge(isDataSaverMode))
    }

    fun setClickListener(listener: View.OnClickListener?) {
        userLayout?.apply {
            PushDownAnim.setPushDownAnimTo(this)
                .setOnClickListener(listener)
        }
    }

}