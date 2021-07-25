package com.rex50.mausam.utils.custom_text_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class BoldTextView extends AppCompatTextView {


    public BoldTextView(Context context) {
        super(context);
        setCustomTypeface(context);
    }

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomTypeface(context);
    }

    public BoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomTypeface(context);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

    private void setCustomTypeface(Context context) {
        Typeface face= getTypeFace(context);
        this.setTypeface(face, Typeface.BOLD);
    }

    public static Typeface getTypeFace(Context context) {
        return Typeface.createFromAsset(context.getAssets(), getFontPath());
    }

    private static String getFontPath(){
//        return "fonts/Asap-Bold.ttf";
        return "fonts/Poppins-Bold.ttf";
//        return "fonts/Roboto-Black.ttf";
    }

}