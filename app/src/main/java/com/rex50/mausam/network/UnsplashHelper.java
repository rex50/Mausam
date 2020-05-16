package com.rex50.mausam.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.StringDef;

import com.rex50.mausam.interfaces.GetUnsplashCollectionsAndTagsListener;
import com.rex50.mausam.interfaces.GetUnsplashPhotosAndUsersListener;
import com.rex50.mausam.interfaces.GetUnsplashPhotosListener;
import com.rex50.mausam.interfaces.GetUnsplashSearchedPhotosListener;
import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos;
import com.rex50.mausam.model_classes.utils.CollectionsAndTags;
import com.rex50.mausam.model_classes.utils.PhotosAndUsers;
import com.rex50.mausam.storage.database.key_values.KeyValuesRepository;
import com.rex50.mausam.utils.DataParser;

import org.json.JSONArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

import static com.rex50.mausam.utils.Constants.ApiConstants.UNSPLASH_USERNAME;

public class UnsplashHelper {

    private final String TAG = "UnsplashHelper";
    private final String collectionKey = "popularCollectionAndTags";

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

    APIManager apiManager;

    private Context context;

    private UnsplashHelper(){
    }

    public UnsplashHelper(Context context){
        this.context = context;
        apiManager = APIManager.getInstance(context);
    }

    public void getPhotosAndUsers(@OrderPhotosByRestriction String orderBy, GetUnsplashPhotosAndUsersListener listener){
        HashMap<String, String> extras = new HashMap<>();
        extras.put("order_by", orderBy);
        String response = KeyValuesRepository.getValue(context, orderBy);
        if(response == null || response.isEmpty()){
            apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_PHOTOS, extras, new APIManager.UnsplashAPICallResponse() {
                @Override
                public void onSuccess(String response) {
                    //prefs.setPhotosResponseMap(orderBy, response);
                    PhotosAndUsers photosAndUsers = DataParser.parseUnsplashData(response, true);
                    listener.onSuccess(photosAndUsers.getPhotosList(), photosAndUsers.getUserList());
                    KeyValuesRepository.insert(context, orderBy, response);
                }

                @Override
                public void onFailed(JSONArray errors) {
                    listener.onFailed(errors);
                }
            });
        }else {
            PhotosAndUsers photosAndUsers = DataParser.parseUnsplashData(response, true);
            listener.onSuccess(photosAndUsers.getPhotosList(), photosAndUsers.getUserList());
        }
    }

    /*public void getPhotos(@OrderPhotosByRestriction String orderBy, GetUnsplashPhotosListener listener){
        APIManager apiManager = APIManager.getInstance(context);
        HashMap<String, String> extras = new HashMap<>();
        extras.put("order_by", orderBy);
        String response = KeyValuesRepository.getValue(context, orderBy);
        if(response == null || response.isEmpty()){
            apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_PHOTOS, extras, new APIManager.UnsplashAPICallResponse() {
                @Override
                public void onSuccess(String response) {
                    //prefs.setPhotosResponseMap(orderBy, response);
                    List<UnsplashPhotos> photosList = DataParser.parseUnsplashData(response, false).getPhotosList();
                    KeyValuesRepository.insert(context, orderBy, response);

                    Log.e(TAG, "getPhotos: getting from API");
                    listener.onSuccess(photosList);
                }

                @Override
                public void onFailed(JSONArray errors) {
                    listener.onFailed(errors);
                }
            });
        }else {
            Log.e(TAG, "getPhotos: getting from sharedPrefs");
            listener.onSuccess(DataParser.parseUnsplashData(response, false).getPhotosList());
        }
    }*/

    public void getSearchedPhotos(String searchTerm, int page, @perPageRestriction int perPage, GetUnsplashSearchedPhotosListener listener){
        HashMap<String, String> extras = new HashMap<>();
        extras.put("query", searchTerm);
        extras.put("page", page < 1 ? String.valueOf(page) : "1");
        extras.put("per_page", String.valueOf(perPage));
        String response = KeyValuesRepository.getValue(context, searchTerm);
        if(null == response || response.isEmpty()){
            apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_SEARCHED_PHOTOS, extras, new APIManager.UnsplashAPICallResponse() {
                @Override
                public void onSuccess(String response) {
                    SearchedPhotos photos = DataParser.parseSearchedPhotos(response);
                    listener.onSuccess(photos);
                    KeyValuesRepository.insert(context, searchTerm, response);
                }

                @Override
                public void onFailed(JSONArray errors) {
                    listener.onFailed(errors);
                }
            });
        }else {
            Log.e(TAG, "getPhotos: getting from sharedPrefs");
            listener.onSuccess(DataParser.parseSearchedPhotos(response));
        }
    }

    public void getCollectionsAndTags(int page, @perPageRestriction int perPage, GetUnsplashCollectionsAndTagsListener listener){
        HashMap<String, String> extras = new HashMap<>();
        extras.put("page", page < 1 ? String.valueOf(page) : "1");
        extras.put("per_page", String.valueOf(perPage));
        String response = KeyValuesRepository.getValue(context, collectionKey);
        if(null == response || response.isEmpty()){
            apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_COLLECTIONS, extras, new APIManager.UnsplashAPICallResponse() {
                @Override
                public void onSuccess(String response) {
                    CollectionsAndTags obj = DataParser.parseCollections(response, true);
                    listener.onSuccess(obj.getCollectionsList(), obj.getTagsList());
                    KeyValuesRepository.insert(context, collectionKey, response);
                }

                @Override
                public void onFailed(JSONArray errors) {
                    listener.onFailed(errors);
                }
            });
        }else {
            Log.e(TAG, "getCollectionsAndTags: getting from DB");
            CollectionsAndTags obj = DataParser.parseCollections(response, true);
            listener.onSuccess(obj.getCollectionsList(), obj.getTagsList());
        }
    }

    public void getUserPhotos(String unsplashUserName, GetUnsplashPhotosListener listener){
        HashMap<String, String> extras = new HashMap<>();
        extras.put(UNSPLASH_USERNAME, unsplashUserName);
        String response = KeyValuesRepository.getValue(context, unsplashUserName);
        if(response == null || response.isEmpty()) {
            apiManager.makeUnsplashRequest(APIManager.SERVICE_GET_PHOTOS_BY_USER, extras, new APIManager.UnsplashAPICallResponse() {
                @Override
                public void onSuccess(String response) {
                    PhotosAndUsers photosAndUsers = DataParser.parseUnsplashData(response, true);
                    listener.onSuccess(photosAndUsers.getPhotosList());
                    KeyValuesRepository.insert(context, unsplashUserName, response);
                }

                @Override
                public void onFailed(JSONArray errors) {
                    listener.onFailed(errors);
                }
            });
        } else {
            PhotosAndUsers photosAndUsers = DataParser.parseUnsplashData(response, true);
            listener.onSuccess(photosAndUsers.getPhotosList());
        }
    }


}
