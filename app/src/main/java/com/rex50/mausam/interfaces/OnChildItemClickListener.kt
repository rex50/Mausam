package com.rex50.mausam.interfaces

import android.widget.ImageView

interface OnChildItemClickListener {
    fun onItemClick(o: Any?, childImgView: ImageView?, childPos: Int)
}