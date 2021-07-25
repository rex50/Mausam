package com.rex50.mausam.utils.custom_text_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class SemiBoldTextView extends AppCompatTextView {


    public SemiBoldTextView(Context context) {
        super(context);
        setCustomTypeface(context);
    }

    public SemiBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomTypeface(context);
    }

    public SemiBoldTextView(Context context, AttributeSet attrs, int defStyle) {
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

    private static String getFontPath(){
//        return "fonts/Asap-SemiBold.ttf";
        return "fonts/Poppins-SemiBold.ttf";
//        return "fonts/Roboto-Bold.ttf";
    }

}