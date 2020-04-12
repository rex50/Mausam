package com.rex50.mausam.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.StringDef;

import com.rex50.mausam.interfaces.GetUnsplashPhotosListener;
import com.rex50.mausam.interfaces.GetUnsplashSearchedPhotosListener;
import com.rex50.mausam.utils.DataParser;
import com.rex50.mausam.utils.MausamSharedPrefs;

import org.json.JSONArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

public class UnsplashHelper {

    private final String TAG = "UnsplashHelper";

    //Unsplash Constants
    public final static String ORDER_BY_DEFAULT = "latest";
    public final static String ORDER_BY_LATEST = "latest";
    public final static String ORDER_BY_OLDEST = "oldest";
    public final static String ORDER_BY_POPULAR = "popular";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ORDER_BY_DEFAULT, ORDER_BY_LATEST, ORDER_BY_OLDEST, ORDER_BY_POPULAR})
    private @interface OrderPhotosByRestriction {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntRange(from = 1, to = 20)
    private @interface perPageRestriction{}

    private Context context;

    private UnsplashHelper(){
    }

    public UnsplashHelper(Context context){
        this.context = context;
    }

    public void getPhotos(@OrderPhotosByRestriction String orderBy, GetUnsplashPhotosListener listener){
        APIManager apiManager = APIManager.getInstance(context);
        HashMap<String, String> extras = new HashMap<>();
        extras.put("order_by", orderBy);
        MausamSharedPrefs prefs = new MausamSharedPrefs(context);
        if(prefs.getFromPhotosResponseMap(orderBy).isEmpty()) {
            apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_PHOTOS, extras, new APIManager.UnsplashAPICallResponse() {
                @Override
                public void onSuccess(String response) {
                    prefs.setPhotosResponseMap(orderBy, response);
                    Log.e(TAG, "getPhotos: getting from API");
                    listener.onSuccess(new DataParser().parseUnsplashData(response));
                }

                @Override
                public void onFailed(JSONArray errors) {
                    listener.onFailed(errors);
                }
            });
        }else {
            Log.e(TAG, "getPhotos: getting from sharedPrefs");
            listener.onSuccess(new DataParser().parseUnsplashData(prefs.getFromPhotosResponseMap(orderBy)));
        }
    }

    public void getSearchedPhotos(String searchTerm, int page, @perPageRestriction int perPage, GetUnsplashSearchedPhotosListener listener){
        APIManager apiManager = APIManager.getInstance(context);
        HashMap<String, String> extras = new HashMap<>();
        extras.put("query", searchTerm);
        extras.put("page", page < 1 ? String.valueOf(page) : "1");
        extras.put("per_page", String.valueOf(perPage));
        MausamSharedPrefs prefs = new MausamSharedPrefs(context);
        if(prefs.getFromPhotosResponseMap(searchTerm).isEmpty()) {
            apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_SEARCHED_PHOTOS, extras, new APIManager.UnsplashAPICallResponse() {
                @Override
                public void onSuccess(String response) {
                    prefs.setPhotosResponseMap(searchTerm, response);
                    listener.onSuccess(new DataParser().parseSearchedPhotos(response));
                }

                @Override
                public void onFailed(JSONArray errors) {
                    listener.onFailed(errors);
                }
            });
        }else {
            Log.e(TAG, "getPhotos: getting from sharedPrefs");
            listener.onSuccess(new DataParser().parseSearchedPhotos(prefs.getFromPhotosResponseMap(searchTerm)));
        }
    }



}
