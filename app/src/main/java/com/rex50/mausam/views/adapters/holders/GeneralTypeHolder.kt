package com.rex50.mausam.views.adapters.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.utils.hideView
import com.rex50.mausam.utils.loadImage
import com.rex50.mausam.utils.showView
import com.thekhaeng.pushdownanim.PushDownAnim

class GeneralTypeHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView? = itemView.findViewById(R.id.ivPhoto)
    private val imageViewUser: ImageView? = itemView.findViewById(R.id.ivUser)
    private val cardView: CardView? = itemView.findViewById(R.id.cardView)
    private val tvSponsored: TextView? = itemView.findViewById(R.id.tvSponsored)

    fun bind(photo: UnsplashPhotos?, isDataSaverMode: Boolean) {
        imageView?.loadImage(
            if(photo?.relativePath.isNullOrEmpty())
                photo?.urls?.getRegular(isDataSaverMode)
            else
                photo?.relativePath
        )

        photo?.sponsorship?.let {
            tvSponsored?.showView()
        } ?: tvSponsored?.hideView()

        //imageViewUser?.loadImage(photo?.user?.profileImage?.medium)
    }

    fun setClickListener(listener: View.OnClickListener?) {
        cardView?.apply {
            PushDownAnim.setPushDownAnimTo(this)
                .setOnClickListener(listener)
        }
    }

}