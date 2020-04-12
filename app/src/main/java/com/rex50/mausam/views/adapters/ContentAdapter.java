package com.rex50.mausam.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.rex50.mausam.R;
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;
import com.rex50.mausam.model_classes.unsplash.photos.User;
import com.rex50.mausam.model_classes.utils.GenericModelFactory;

import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.COLLECTION_TYPE;
import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.COLOR_TYPE;
import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.GENERAL_TYPE;
import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.TEXT_TYPE;
import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.USER_TYPE;

public class ContentAdapter extends RecyclerView.Adapter{

    private Context context;
    private GenericModelFactory model;

    private ContentAdapter(){}

    public ContentAdapter(Context context, GenericModelFactory model){
        this.context = context;
        this.model = model;
    }

    public static class GeneralTypeHolder extends RecyclerView.ViewHolder{

        ImageView wallpaper;

        GeneralTypeHolder(@NonNull View itemView) {
            super(itemView);
            wallpaper = itemView.findViewById(R.id.wallpaper_img);
        }
    }

    private static class UserTypeHolder extends RecyclerView.ViewHolder {
        ImageView userImg;
        TextView txtUserName;
        UserTypeHolder(View v) {
            super(v);
            userImg = v.findViewById(R.id.user_img);
            txtUserName = v.findViewById(R.id.txt_user_name);
        }
    }

    private static class ColorTypeHolder extends RecyclerView.ViewHolder {
        View cardColor;
        TextView txtColorName;

        ColorTypeHolder(View v) {
            super(v);
            cardColor = v.findViewById(R.id.viewColor);
            txtColorName = v.findViewById(R.id.txt_color_name);
        }
    }

    private static class CollectionTypeHolder extends RecyclerView.ViewHolder {

        ImageView imgMain, imgPreview1, imgPreview2, imgPreview3;
        TextView txtTitle, txtDesc;

        CollectionTypeHolder(@NonNull View itemView) {
            super(itemView);
            imgMain = itemView.findViewById(R.id.wallpaper_img_main);
            imgPreview1 = itemView.findViewById(R.id.wallpaper_img_1);
            imgPreview2 = itemView.findViewById(R.id.wallpaper_img_2);
            imgPreview3 = itemView.findViewById(R.id.wallpaper_img_3);

            txtTitle = itemView.findViewById(R.id.txt_collection_title);
            txtDesc = itemView.findViewById(R.id.txt_collection_desc);
        }
    }

    private static class TextTypeHolder extends RecyclerView.ViewHolder{

        Button btnTag;

        TextTypeHolder(@NonNull View itemView) {
            super(itemView);
            btnTag = itemView.findViewById(R.id.btn_tag);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int layout) {
        View v = LayoutInflater.from(context).inflate(layout, parent, false);
        switch (layout){
            case USER_TYPE :
                return new UserTypeHolder(v);
            case COLOR_TYPE :
                return new ColorTypeHolder(v);
            case GENERAL_TYPE :
                return new GeneralTypeHolder(v);
            case COLLECTION_TYPE:
                return new CollectionTypeHolder(v);
            case TEXT_TYPE :
                return new TextTypeHolder(v);
            default: throw new IllegalArgumentException("Inflate code for viewType:\"" + layout +"\" is not found");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemType = model.getItemType();

        if(itemType == GENERAL_TYPE){

            GeneralTypeHolder itemHolder = ((GeneralTypeHolder) holder);
            UnsplashPhotos photo = model.getGeneralTypeModel().getPhotosList().get(position);
            Glide.with(context)
                    .load(photo.getUrls().getSmall())
                    //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(itemHolder.wallpaper);

        } else if(itemType == USER_TYPE){

            UserTypeHolder itemHolder = ((UserTypeHolder) holder);
            User userModel = model.getUserTypeModel().getUsersList().get(position);
            itemHolder.txtUserName.setText(userModel.getFirstName() + " " + userModel.getLastName());
            Glide.with(context)
                    .load(userModel.getProfileImage())
                    //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(itemHolder.userImg);

        } else if(itemType == COLOR_TYPE){

            ColorTypeHolder itemHolder = ((ColorTypeHolder) holder);
            GenericModelFactory.ColorModel colorModel = model.getColorTypeModel().getColorsList().get(position);

            itemHolder.cardColor.setBackgroundColor(ContextCompat.getColor(context, colorModel.getColorCode()));
            itemHolder.txtColorName.setText(colorModel.getColorName());

        } else if(itemType == COLLECTION_TYPE){

            /*CollectionTypeHolder itemHolder = ((CollectionTypeHolder) holder);

            itemHolder.txtTitle.setText(model.getTitle());

            itemHolder.txtDesc.setText(model.getDesc());

            Glide.with(context)
                    .load(model.getPhotosList().get(position).getUrls().getSmall())
                    //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(itemHolder.imgMain);

            Glide.with(context)
                    .load(model.getPhotosList().get(position).getUrls().getSmall())
                    //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(itemHolder.imgPreview1);

            Glide.with(context)
                    .load(model.getPhotosList().get(position).getUrls().getSmall())
                    //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(itemHolder.imgPreview2);

            Glide.with(context)
                    .load(model.getPhotosList().get(position).getUrls().getSmall())
                    //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(itemHolder.imgPreview3);*/

        } else if(itemType == TEXT_TYPE){

            /*TextTypeHolder itemHolder = ((TextTypeHolder) holder);
            itemHolder.btnTag.setText(model.getTagsList().get(position));*/

        }else {
            throw new IllegalArgumentException("Code missing for " + itemType);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return model.getItemLayout();
    }

    @Override
    public int getItemCount() {
        return totalItems();
    }

    private int totalItems(){
        switch (model.getItemType()){
            case GENERAL_TYPE :
                return model.getGeneralTypeModel().getPhotosList().size();
            case COLLECTION_TYPE :
                return model.getCollectionTypeModel().getPhotosList().size();
            case COLOR_TYPE :
                return model.getColorTypeModel().getColorsList().size();
            case USER_TYPE :
                return model.getUserTypeModel().getUsersList().size();
            case TEXT_TYPE :
                return model.getTextTypeModel().getTagsList().size();
            default: throw new IllegalArgumentException("Please add case for ItemType : \""+ model.getItemType() +"\"");
        }
    }

}
