package com.rex50.mausam.interfaces;

import android.widget.ImageView;

public interface OnChildItemClickListener {
    //void onMoreButtonClick(int groupPos);
    void onItemClick(Object o, ImageView childImgView, int childPos);
}
