package com.rex50.mausam.interfaces

import android.widget.ImageView

interface OnGroupItemClickListener {
    fun onItemClick(o: Any?, childImgView: ImageView?, groupPos: Int, childPos: Int)
    fun onMoreClicked(o: Any?, title: String?, groupPos: Int)
}