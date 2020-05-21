package com.rex50.mausam.model_classes.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rex50.mausam.R;
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;
import com.rex50.mausam.views.adapters.HomeAdapter;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.List;

public class AllContentModel{

    private static final String TAG = "AllContentModel";
    private final Context context;
    private List<GenericModelFactory> models;
    private List<String> sequenceOfLayout;
    private HomeAdapter adapter;

    private List<String> types;

    public AllContentModel(Context context){
        this.context = context;
        models = new ArrayList<>();
        types = new ArrayList<>();
        sequenceOfLayout = new ArrayList<>();
    }

    public void addModel(String type, GenericModelFactory model){
        this.models.add(model);
        this.types.add(type);
    }

    public void addModel(int pos, String type, GenericModelFactory model){
        this.models.add(pos, model);
        this.types.add(pos, type);
    }

    public void addAllModel(List<GenericModelFactory> list){
        models.addAll(list);
    }

    public void setSequenceOfLayouts(List<String> sequence){
        this.sequenceOfLayout = sequence;
    }

    public synchronized Integer addSequentially(String type, GenericModelFactory model){
        if(sequenceOfLayout.size() == 0){
            throw new IllegalStateException("set sequence before using addSequentially()");
        }
        try {
            if(size() == 0) {
                addModel(type, model);
                if(adapter != null)
                    adapter.notifyDataSetChanged();
                return 0;
            }
            int pos = sequenceOfLayout.indexOf(type);
            for (int i = 0; i < size(); i++) {
                int addedPos = sequenceOfLayout.indexOf(getType(i));
                if(addedPos > pos){
                    addModel(i, type, model);
                    if(adapter != null) {
                        //TODO: adapter.notifyItemInserted(i);
                        adapter.notifyDataSetChanged();
                    }
                    return i;
                }
            }
            addModel(type, model);
            if(adapter != null) {
                //TODO: adapter.notifyItemInserted(size()-1);
                adapter.notifyDataSetChanged();
            }
            return size() - 1;
        }catch (Exception e){
            Log.e(TAG, "addSequentially: ", e);
            return null;
        }
    }

    public void setAdapter(@Nullable HomeAdapter adapter){
        this.adapter = adapter;
    }

    public void setOnClickListener(){
        if(adapter != null){
            adapter.setGroupItemClickListener((object, childImgView, groupPos, childPos) -> {
                if(context != null) {
                    if (object instanceof GenericModelFactory.GeneralTypeModel) {

                        showImagesInFullScreen(((GenericModelFactory.GeneralTypeModel) object).getPhotosList(), childImgView, childPos);

                    } else if (object instanceof GenericModelFactory.UserTypeModel) {

                    } else if (object instanceof GenericModelFactory.ColorTypeModel) {

                    } else if (object instanceof GenericModelFactory.CollectionTypeModel) {

                    } else if (object instanceof GenericModelFactory.TagTypeModel) {

                    } else if (object instanceof GenericModelFactory.CategoryTypeModel) {

                    } else if (object instanceof GenericModelFactory.FavouritePhotographerTypeModel) {
                        showImagesInFullScreen(((GenericModelFactory.FavouritePhotographerTypeModel) object).getPhotosList(),
                                childImgView, childPos);


                    } else {
                        throw new IllegalArgumentException("Cannot determine given object type.\nHint: Add code for " + object.getClass().getName() + " class type");

                    }
                }
            });
        }
    }

    private void showImagesInFullScreen(List<UnsplashPhotos> photosList, ImageView childImgView, int childPos){
        StfalconImageViewer.Builder imageViewerBuilder = new StfalconImageViewer.Builder<>(context, photosList,
                (imageView, unsplashPhotos) ->
                        Glide.with(context)
                                .load(unsplashPhotos.getUrls().getFull())
                                /*.apply(new RequestOptions()
                                        .fitCenter()
                                        .format(DecodeFormat.PREFER_ARGB_8888)
                                        .override(Target.SIZE_ORIGINAL))*/
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .thumbnail(Glide.with(context).load(R.drawable.ic_loader))
                                .into(imageView))
                .withHiddenStatusBar(false)
                .withStartPosition(childPos);
        imageViewerBuilder.withBackgroundColorResource(R.color.white_to_black);
        View dialogLayout = View.inflate(context, R.layout.full_screen_image_overlay, null);

        PushDownAnim.setPushDownAnimTo(dialogLayout.findViewById(R.id.btn_set_wallpaper),
                dialogLayout.findViewById(R.id.btn_download_image),
                dialogLayout.findViewById(R.id.btn_fav_image),
                dialogLayout.findViewById(R.id.btn_share_image),
                dialogLayout.findViewById(R.id.btn_more_about_image))
                .setOnClickListener(v -> {
                    switch (v.getId()){
                        case R.id.btn_set_wallpaper : break;
                        case R.id.btn_download_image : break;
                        case R.id.btn_fav_image : break;
                        case R.id.btn_share_image : break;
                        case R.id.btn_more_about_image : break;
                    }
                });

        PushDownAnim.setPushDownAnimTo(dialogLayout.findViewById(R.id.btn_set_wallpaper))
                .setOnClickListener(v -> {
                    //TODO
                });

        imageViewerBuilder.withOverlayView(dialogLayout);
        if (childImgView != null) {
            imageViewerBuilder.withTransitionFrom(childImgView);
        }
        imageViewerBuilder.show();
    }

    public GenericModelFactory getModel(int pos){
        return models.get(pos);
    }

    public String getType(int pos){
        return types.get(pos);
    }

    public int size(){
        return models == null ? 0 : models.size();
    }

}
