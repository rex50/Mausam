package com.rex50.mausam.Utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MaterialSnackBar {
    private View layout;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG})
    private @interface MaterialSnackBarDuration{}

    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_SHORT = -1;
    public static final int LENGTH_LONG = 0;

    public MaterialSnackBar(Activity activity){
        this.layout = activity.findViewById(android.R.id.content);
    }

    public void show(String msg, @MaterialSnackBarDuration int duration){
        show(msg, duration, null);
    }

    public void show(String msg, @MaterialSnackBarDuration int duration, @Nullable Drawable background){
        Snackbar snackbar = Snackbar.make(layout, msg, duration);
        showSnackBarWithMargin(snackbar, background);
    }

    public void showActionSnackBar(String msg, String action, @MaterialSnackBarDuration int duration, SnackBarListener listener){
        showActionSnackBar(msg, action,null, duration, listener);
    }

    public void showActionSnackBar(String msg, String action, @Nullable final Drawable background, @MaterialSnackBarDuration int duration, final SnackBarListener listener){
        Snackbar snackbar = Snackbar.make(layout, msg, duration)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onActionPressed();
                    }
                });
        showSnackBarWithMargin(snackbar, background);
    }

    private void showSnackBarWithMargin(Snackbar snackbar, Drawable background){
        int sideMargin = 20;
        int marginBottom = 20;
        final View snackBarView = snackbar.getView();
        final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackBarView.getLayoutParams();

        params.setMargins(params.leftMargin + sideMargin,
                params.topMargin,
                params.rightMargin + sideMargin,
                params.bottomMargin + marginBottom);

        snackBarView.setLayoutParams(params);
        if(background == null){
            int color = Color.rgb(43,43,43);
            int radius = 8;
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(color);
            drawable.setCornerRadius(radius);
            snackBarView.setBackground(drawable);
        }
        else
            snackBarView.setBackground(background);
        snackbar.show();
    }

    public interface SnackBarListener{
        void onActionPressed();
    }

}
