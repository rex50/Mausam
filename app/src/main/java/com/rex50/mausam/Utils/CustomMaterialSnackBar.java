package com.rex50.mausam.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CustomMaterialSnackBar {
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

    public CustomMaterialSnackBar(Context context, View v) {
        this.context = context;
        this.layout = v;
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
        snackbar.dismiss();
        snackbar = null;
    }

    private void showSnackBarWithMargin(Snackbar snackbar, Drawable background) {
        int sideMargin = 20;
        int marginBottom = 20;
        final View snackBarView = snackbar.getView();
//        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setMaxLines(2);
        final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackBarView.getLayoutParams();

        params.setMargins(params.leftMargin + sideMargin,
                params.topMargin,
                params.rightMargin + sideMargin,
                params.bottomMargin + marginBottom);

        snackBarView.setLayoutParams(params);
        if (background == null) {
            int color = Color.rgb(43, 43, 43);
            int radius = 8;
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(color);
            drawable.setCornerRadius(radius);
            snackBarView.setBackground(drawable);
        } else
            snackBarView.setBackground(background);
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
