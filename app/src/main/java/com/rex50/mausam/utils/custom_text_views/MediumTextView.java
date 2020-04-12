package com.rex50.mausam.utils.custom_text_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class MediumTextView extends AppCompatTextView {


    public MediumTextView(Context context) {
        super(context);
        setCustomTypeface(context);
    }

    public MediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomTypeface(context);
    }

    public MediumTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomTypeface(context);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

    private void setCustomTypeface(Context context){
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Asap-Medium.ttf");
        this.setTypeface(face);
    }

}