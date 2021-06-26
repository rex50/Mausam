package com.rex50.mausam.views.adapters.holders

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.item_types.CategoryModel
import com.rex50.mausam.utils.loadImage
import com.thekhaeng.pushdownanim.PushDownAnim

class CategoryTypeHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val categoryName: TextView? = itemView.findViewById(R.id.tvCategoryName)
    private val cardView = itemView.findViewById<CardView>(R.id.cardView)
    private val ivCategory = itemView.findViewById<ImageView>(R.id.ivCategory)
    private val vBgOverlay = itemView.findViewById<View>(R.id.vBgOverlay)
    fun bind(model: CategoryModel?, overlayBg: Drawable?) {
        categoryName?.text = model?.categoryName

        vBgOverlay?.background = overlayBg

        ivCategory?.loadImage(model?.categoryImg)
    }

    fun setClickListener(listener: View.OnClickListener?) {
        cardView?.apply {
            PushDownAnim.setPushDownAnimTo(this)
                .setOnClickListener(listener)
        }
    }

}