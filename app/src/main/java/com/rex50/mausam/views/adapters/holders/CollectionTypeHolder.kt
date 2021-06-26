package com.rex50.mausam.views.adapters.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.utils.hideView
import com.rex50.mausam.utils.isNotNull
import com.rex50.mausam.utils.loadImage
import com.thekhaeng.pushdownanim.PushDownAnim
import org.apache.commons.lang3.StringUtils

class CollectionTypeHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var imgMain: ImageView? = itemView.findViewById(R.id.ivPhotoCover)
    private var imgPreview1: ImageView? = itemView.findViewById(R.id.ivPhotoPreview1)
    private var imgPreview2: ImageView? = itemView.findViewById(R.id.ivPhotoPreview2)
    private var imgPreview3: ImageView? = itemView.findViewById(R.id.ivPhotoPreview3)
    private var txtTitle: TextView? = itemView.findViewById(R.id.tvCollectionTitle)
    private var txtDesc: TextView? = itemView.findViewById(R.id.tvCollectionDesc)
    private var cardView: CardView? = itemView.findViewById(R.id.cardView)
    fun bind(collection: Collections?, isDataSaverMode: Boolean) {
        collection?.let {
            txtTitle?.text = StringUtils.capitalize(it.title?.trim())

            txtDesc?.text = it.description?.trim()

            imgMain?.takeIf { collection.coverPhoto.isNotNull() }?.loadImage(collection.coverPhoto?.urls?.getRegular(isDataSaverMode))

            imgPreview1?.takeIf { collection.previewPhotos?.size ?: 0 > 0 }?.loadImage(collection.previewPhotos[0]?.urls?.getSmall(isDataSaverMode))
                ?: (imgPreview1?.parent as View?)?.hideView()

            imgPreview2.takeIf { collection.previewPhotos?.size ?: 0 > 1 }?.loadImage(collection.previewPhotos[1]?.urls?.getSmall(isDataSaverMode))
                ?: (imgPreview2?.parent as View?)?.hideView()

            imgPreview3?.takeIf { collection.previewPhotos?.size ?: 0 > 2 }?.loadImage(collection.previewPhotos[2]?.urls?.getSmall(isDataSaverMode))
                ?: (imgPreview3?.parent as View?)?.hideView()
        }
    }

    fun setClickListener(listener: View.OnClickListener?) {
        cardView?.apply {
            PushDownAnim.setPushDownAnimTo(this)
                .setOnClickListener(listener)
        }
    }

}