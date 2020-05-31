package com.rex50.mausam.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;
import com.rex50.mausam.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MaterialSnackBar {
    private Snackbar snackbar;
    private View layout;
    private Context context;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG})
    private @interface MaterialSnackBarDuration {
    }

    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_SHORT = -1;
    public static final int LENGTH_LONG = 0;

    public MaterialSnackBar(Context context, View v) {
        this.context = context;
        this.layout = v;
    }

    public void show(@StringRes int msg, @MaterialSnackBarDuration int duration) {
        show(context.getString(msg), duration, null);
    }

    public void show(String msg, @MaterialSnackBarDuration int duration) {
        show(msg, duration, null);
    }

    public void show(String msg, @MaterialSnackBarDuration int duration, @Nullable Drawable background) {
        if (isSupported()) {
            snackbar = Snackbar.make(layout, msg, duration);
            showSnackBarWithMargin(snackbar, background);
        } else {
            showToast(msg, duration);
        }
    }

    public void showActionSnackBar(@StringRes int msg, @StringRes int action, @MaterialSnackBarDuration int duration, SnackBarListener listener) {
        showActionSnackBar(context.getString(msg), context.getString(action), null, duration, listener);
    }

    public void showActionSnackBar(String msg, String action, @MaterialSnackBarDuration int duration, SnackBarListener listener) {
        showActionSnackBar(msg, action, null, duration, listener);
    }

    public void showActionSnackBar(String msg, String action, @Nullable final Drawable background, @MaterialSnackBarDuration int duration, final SnackBarListener listener) {
        if (isSupported()) {
            snackbar = Snackbar.make(layout, msg, duration)
                    .setAction(action, view -> listener.onActionPressed());
            showSnackBarWithMargin(snackbar, background);
        } else {
            showToast(msg, duration);
        }
    }

    public void dismiss() {
        if(snackbar != null){
            snackbar.dismiss();
            snackbar = null;
        }
    }

    private void showSnackBarWithMargin(Snackbar snackbar, Drawable background) {
        int sideMargin = 28;
        int marginBottom = 28;
        float fontSize = 14;
        Typeface textFont = Typeface.createFromAsset(context.getAssets(), "fonts/Asap-SemiBold.ttf"),
                actionFont = Typeface.createFromAsset(context.getAssets(), "fonts/Asap-Bold.ttf");
        int color = ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, null);
        int colorDark = ResourcesCompat.getColor(context.getResources(), R.color.white, null);
        final View snackBarView = snackbar.getView();
//        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setMaxLines(2);

        //setting custom margins
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackBarView.getLayoutParams();
        params.setMargins(params.leftMargin + sideMargin,
                params.topMargin,
                params.rightMargin + sideMargin,
                params.bottomMargin + marginBottom);

        snackBarView.setLayoutParams(params);
        if (background == null) {
            int radius = 10;
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(color);
            drawable.setCornerRadius(radius);
            snackBarView.setBackground(drawable);
        } else
            snackBarView.setBackground(background);


        // change snackbar text color
        /*int snackbarTextId = android.support.design.R.id.snackbar_text;*/  // for support library
        int snackbarTextId = com.google.android.material.R.id.snackbar_text; //for androidx
        int snackbarActionId = com.google.android.material.R.id.snackbar_action;
        TextView textView = snackBarView.findViewById(snackbarTextId);
        textView.setTypeface(textFont);
        textView.setTextColor(colorDark);
        textView.setTextSize(fontSize);
        textView.setMaxLines(5);

        //custom action text color
        textView = snackBarView.findViewById(snackbarActionId);
        textView.setTypeface(actionFont);
        textView.setTextColor(colorDark);
//        snackbar.setActionTextColor(colorDark);

        snackbar.show();
    }

    private boolean isSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private void showToast(String msg, @MaterialSnackBarDuration int duration) {
        Toast.makeText(context, msg, LENGTH_SHORT == duration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public interface SnackBarListener {
        void onActionPressed();
    }

}
