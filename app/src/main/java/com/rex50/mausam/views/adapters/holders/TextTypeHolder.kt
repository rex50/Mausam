package com.rex50.mausam.views.adapters.holders

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.unsplash.collection.Tag
import com.thekhaeng.pushdownanim.PushDownAnim

class TextTypeHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var btnTag: Button? = itemView.findViewById(R.id.btnTag)
    fun bind(tag: Tag?) {
        btnTag?.text = tag?.title ?: ""
    }

    fun setClickListener(listener: View.OnClickListener?) {
        btnTag?.apply {
            PushDownAnim.setPushDownAnimTo(this)
                .setOnClickListener(listener)
        }
    }

}