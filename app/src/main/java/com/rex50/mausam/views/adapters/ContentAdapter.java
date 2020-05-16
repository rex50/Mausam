package com.rex50.mausam.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.rex50.mausam.R;
import com.rex50.mausam.interfaces.OnChildItemClickListener;
import com.rex50.mausam.model_classes.unsplash.collection.Collections;
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;
import com.rex50.mausam.model_classes.unsplash.photos.User;
import com.rex50.mausam.model_classes.utils.GenericModelFactory;
import com.rex50.mausam.utils.Utils;
import com.thekhaeng.pushdownanim.PushDownAnim;

import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.CATEGORY_TYPE;
import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.COLLECTION_TYPE;
import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.COLOR_TYPE;
import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.FAV_PHOTOGRAPHER_PHOTOS_TYPE;
import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.GENERAL_TYPE;
import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.TAG_TYPE;
import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.USER_TYPE;

public class ContentAdapter extends RecyclerView.Adapter{

    private Context context;
    private GenericModelFactory model;
    private OnChildItemClickListener childClickListener;

    private ContentAdapter(){}

    public ContentAdapter(Context context, GenericModelFactory model){
        this.context = context;
        this.model = model;
    }

    public void setChildClickListener(OnChildItemClickListener childClickListener) {
        this.childClickListener = childClickListener;
    }

    public class GeneralTypeHolder extends RecyclerView.ViewHolder{

        private ImageView wallpaperView;
        private CardView cardView;

        GeneralTypeHolder(@NonNull View itemView) {
            super(itemView);
            wallpaperView = itemView.findViewById(R.id.wallpaper_img);
            cardView = itemView.findViewById(R.id.cardView);
        }

        void bind(UnsplashPhotos photo) {
            if(context != null && wallpaperView != null) {
                Glide.with(context)
                        .load(photo.getUrls().getSmall())
                        //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(wallpaperView);
            }
        }

        void setClickListener(View.OnClickListener listener) {
            if(cardView != null && childClickListener != null){
                PushDownAnim.setPushDownAnimTo(cardView)
                        .setOnClickListener(listener);
            }
        }

        ImageView getImageView(){
            return wallpaperView;
        }
    }

    public class FavPhotographerPhotosTypeHolder extends RecyclerView.ViewHolder{

        private ImageView wallpaperView;
        private CardView cardView;

        FavPhotographerPhotosTypeHolder(@NonNull View itemView) {
            super(itemView);
            wallpaperView = itemView.findViewById(R.id.wallpaper_img);
            cardView = itemView.findViewById(R.id.cardView);
        }

        void bind(UnsplashPhotos photo) {
            if(context != null && wallpaperView != null) {
                Glide.with(context)
                        .load(photo.getUrls().getSmall())
                        //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(wallpaperView);
            }
        }

        void setClickListener(View.OnClickListener listener) {
            if(cardView != null && childClickListener != null){
                PushDownAnim.setPushDownAnimTo(cardView)
                        .setOnClickListener(listener);
            }
        }

        ImageView getImageView(){
            return wallpaperView;
        }
    }

    public class UserTypeHolder extends RecyclerView.ViewHolder {
        ImageView userImg;
        TextView txtUserName;
        LinearLayout userLayout;
        UserTypeHolder(View v) {
            super(v);
            userImg = v.findViewById(R.id.user_img);
            txtUserName = v.findViewById(R.id.txt_user_name);
            userLayout = v.findViewById(R.id.userLayout);
        }

        private void bind(User userModel) {
            if(context != null && userImg != null && txtUserName != null) {
                String name = Utils.getTextOrEmpty(userModel.getFirstName()) + " " + Utils.getTextOrEmpty(userModel.getLastName());
                txtUserName.setText(name);
                Glide.with(context)
                        .load(userModel.getProfileImage().getLarge())
                        //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(userImg);
            }
        }

        void setClickListener(View.OnClickListener listener) {
            if(userLayout != null && childClickListener != null){
                PushDownAnim.setPushDownAnimTo(userLayout)
                        .setOnClickListener(listener);
            }
        }
    }

    public class ColorTypeHolder extends RecyclerView.ViewHolder {
        View cardColor;
        TextView txtColorName;
        CardView cardView;

        ColorTypeHolder(View v) {
            super(v);
            cardColor = v.findViewById(R.id.viewColor);
            txtColorName = v.findViewById(R.id.txt_color_name);
            cardView = v.findViewById(R.id.cardView);
        }

        private void bind(GenericModelFactory.ColorModel colorModel) {
            if(cardColor != null && txtColorName != null) {
                cardColor.setBackgroundColor(Color.parseColor(colorModel.getColorCode()));
                txtColorName.setText(colorModel.getColorName());
            }
        }

        void setClickListener(View.OnClickListener listener) {
            if(cardView != null && childClickListener != null){
                PushDownAnim.setPushDownAnimTo(cardView)
                        .setOnClickListener(listener);
            }
        }
    }

    public class CollectionTypeHolder extends RecyclerView.ViewHolder {

        ImageView imgMain, imgPreview1, imgPreview2, imgPreview3;
        TextView txtTitle, txtDesc;
        CardView cardView;

        CollectionTypeHolder(@NonNull View itemView) {
            super(itemView);
            imgMain = itemView.findViewById(R.id.wallpaper_img_main);
            imgPreview1 = itemView.findViewById(R.id.wallpaper_img_1);
            imgPreview2 = itemView.findViewById(R.id.wallpaper_img_2);
            imgPreview3 = itemView.findViewById(R.id.wallpaper_img_3);

            txtTitle = itemView.findViewById(R.id.txt_collection_title);
            txtDesc = itemView.findViewById(R.id.txt_collection_desc);

            cardView = itemView.findViewById(R.id.cardView);
        }

        private void bind(Collections collection) {
            if(context != null && imgMain != null && imgPreview1 != null && imgPreview2 != null &&
                    imgPreview3 != null && txtTitle != null && txtDesc != null) {
                if(collection.getTitle() != null)
                    txtTitle.setText(collection.getTitle().trim());

                if(collection.getDescription() != null)
                    txtDesc.setText(collection.getDescription().trim());

                Glide.with(context)
                        .load(collection.getCoverPhoto().getUrls().getSmall())
                        //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(imgMain);

                Glide.with(context)
                        .load(collection.getPreviewPhotos().get(0).getUrls().getSmall())
                        //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(imgPreview1);

                Glide.with(context)
                        .load(collection.getPreviewPhotos().get(1).getUrls().getSmall())
                        //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(imgPreview2);

                Glide.with(context)
                        .load(collection.getPreviewPhotos().get(2).getUrls().getSmall())
                        //.load("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(imgPreview3);
            }
        }

        void setClickListener(View.OnClickListener listener) {
            if(cardView != null && childClickListener != null){
                PushDownAnim.setPushDownAnimTo(cardView)
                        .setOnClickListener(listener);
            }
        }
    }

    public class TextTypeHolder extends RecyclerView.ViewHolder{

        Button btnTag;

        TextTypeHolder(@NonNull View itemView) {
            super(itemView);
            btnTag = itemView.findViewById(R.id.btn_tag);
        }

        private void bind(String title) {
            if(btnTag != null)
                btnTag.setText(title);
        }

        void setClickListener(View.OnClickListener listener) {
            if(btnTag != null && childClickListener != null){
                PushDownAnim.setPushDownAnimTo(btnTag)
                        .setOnClickListener(listener);
            }
        }
    }

    public class CategoryTypeHolder extends RecyclerView.ViewHolder{

        TextView categoryName;
        CardView cardView;

        CategoryTypeHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.txt_category_name);
            cardView = itemView.findViewById(R.id.cardView);
        }

        private void bind(String name) {
            if(categoryName != null)
                categoryName.setText(name);
        }

        void setClickListener(View.OnClickListener listener) {
            if(cardView != null && childClickListener != null){
                PushDownAnim.setPushDownAnimTo(cardView)
                        .setOnClickListener(listener);
            }
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
            case TAG_TYPE:
                return new TextTypeHolder(v);
            case CATEGORY_TYPE:
                return new CategoryTypeHolder(v);
            case FAV_PHOTOGRAPHER_PHOTOS_TYPE:
                return new FavPhotographerPhotosTypeHolder(v);
            default: throw new IllegalArgumentException("Inflate code for viewType:\"" + layout +"\" is not found");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemType = model.getItemType();

        switch (itemType){
            case GENERAL_TYPE:
                GeneralTypeHolder generalHolder = ((GeneralTypeHolder) holder);
                generalHolder.bind(model.getGeneralTypeModel().getPhotosList().get(position));
                generalHolder.setClickListener(v ->
                        childClickListener.onItemClick(model.getGeneralTypeModel(), generalHolder.getImageView(), position));
                break;

            case USER_TYPE:
                UserTypeHolder userHolder = ((UserTypeHolder) holder);
                userHolder.bind(model.getUserTypeModel().getUsersList().get(position));
                userHolder.setClickListener(v ->
                        childClickListener.onItemClick(model.getUserTypeModel(), null, position));
                break;

            case COLOR_TYPE:
                ColorTypeHolder colorHolder = ((ColorTypeHolder) holder);
                colorHolder.bind(model.getColorTypeModel().getColorsList().get(position));
                colorHolder.setClickListener(v ->
                        childClickListener.onItemClick(model.getColorTypeModel(), null, position));
                break;

            case COLLECTION_TYPE:
                CollectionTypeHolder collectionHolder = ((CollectionTypeHolder) holder);
                collectionHolder.bind(model.getCollectionTypeModel().getCollections().get(position));
                collectionHolder.setClickListener(v ->
                        childClickListener.onItemClick(model.getCollectionTypeModel(), null, position));
                break;

            case TAG_TYPE:
                TextTypeHolder textHolder = ((TextTypeHolder) holder);
                textHolder.bind(model.getTagTypeModel().getTagsList().get(position).getTitle());
                textHolder.setClickListener(v ->
                        childClickListener.onItemClick(model.getTagTypeModel(), null, position));
                break;

            case CATEGORY_TYPE:
                CategoryTypeHolder categoryHolder = ((CategoryTypeHolder) holder);
                categoryHolder.bind(model.getCategoryTypeModel().getCategories().get(position));
                categoryHolder.setClickListener(v ->
                        childClickListener.onItemClick(model.getCategoryTypeModel(), null, position));
                break;

            case FAV_PHOTOGRAPHER_PHOTOS_TYPE:
                FavPhotographerPhotosTypeHolder favHolder = ((FavPhotographerPhotosTypeHolder) holder);
                favHolder.bind(model.getFavouritePhotographerTypeModel().getPhotosList().get(position));
                favHolder.setClickListener(v ->
                        childClickListener.onItemClick(model.getFavouritePhotographerTypeModel(), favHolder.getImageView(), position));
                break;

            default: throw new IllegalArgumentException("Code missing for " + itemType);

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
                return model.getCollectionTypeModel().getCollections().size();
            case COLOR_TYPE :
                return model.getColorTypeModel().getColorsList().size();
            case USER_TYPE :
                return model.getUserTypeModel().getUsersList().size();
            case TAG_TYPE:
                return model.getTagTypeModel().getTagsList().size();
            case CATEGORY_TYPE:
                return model.getCategoryTypeModel().getCategories().size();
            case FAV_PHOTOGRAPHER_PHOTOS_TYPE:
                return model.getFavouritePhotographerTypeModel().getPhotosList().size();
            default: throw new IllegalArgumentException("Please add case for ItemType : \""+ model.getItemType() +"\"");
        }
    }

}
