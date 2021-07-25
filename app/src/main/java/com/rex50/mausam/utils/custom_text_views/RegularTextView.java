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
        Typeface face = getTypeFace(context);
        this.setTypeface(face);
    }

    public static Typeface getTypeFace(Context context) {
        return Typeface.createFromAsset(context.getAssets(), getFontPath());
    }

    public static String getFontPath(){
//        return "fonts/Asap-Regular.ttf";
        return "fonts/Poppins-Regular.ttf";
//        return "fonts/Roboto-Regular.ttf";
    }

}