package com.rex50.mausam.views.adapters.holders

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.item_types.ColorModel
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.thekhaeng.pushdownanim.PushDownAnim

class ColorTypeHolder(v: View) : RecyclerView.ViewHolder(v) {
    private var cardColor: View? = v.findViewById(R.id.vBgColor)
    private var txtColorName: TextView? = v.findViewById(R.id.tvColorName)
    private var cardView: CardView? = v.findViewById(R.id.cardView)
    fun bind(colorModel: ColorModel?) {
        cardColor?.setBackgroundColor(Color.parseColor(colorModel?.colorCode))

        txtColorName?.text = colorModel?.colorName
    }

    fun setClickListener(listener: View.OnClickListener?) {
        cardView?.apply {
            PushDownAnim.setPushDownAnimTo(this)
                .setOnClickListener(listener)
        }
    }

}