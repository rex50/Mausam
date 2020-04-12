package com.rex50.mausam.utils.custom_text_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class RegularTextView extends AppCompatTextView {


    public RegularTextView(Context context) {
        super(context);
        setCustomTypeface(context);
    }

    public RegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomTypeface(context);
    }

    public RegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomTypeface(context);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

    private void setCustomTypeface(Context context){
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Asap-Regular.ttf");
        this.setTypeface(face);
    }

}