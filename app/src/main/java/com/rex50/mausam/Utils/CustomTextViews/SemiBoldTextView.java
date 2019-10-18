package com.rex50.mausam.Utils.CustomTextViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class SemiBoldTextView extends AppCompatTextView {


    public SemiBoldTextView(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Asap-SemiBold.ttf");
        this.setTypeface(face);
    }

    public SemiBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Asap-SemiBold.ttf");
        this.setTypeface(face);
    }

    public SemiBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Asap-SemiBold.ttf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

}