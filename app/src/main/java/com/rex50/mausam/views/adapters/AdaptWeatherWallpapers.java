package com.rex50.mausam.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.rex50.mausam.R;
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;

import java.util.List;

public class AdaptWeatherWallpapers extends RecyclerView.Adapter<AdaptWeatherWallpapers.WeatherWallpapersViewHolder> {

    private Context context;
    private List<UnsplashPhotos> photosList;

    public AdaptWeatherWallpapers(Context context, List<UnsplashPhotos> photosList){
        this.context = context;
        this.photosList = photosList;
    }

    class WeatherWallpapersViewHolder extends RecyclerView.ViewHolder{

        ImageView wallpaperImageView;

        WeatherWallpapersViewHolder(@NonNull View itemView) {
            super(itemView);
            wallpaperImageView = itemView.findViewById(R.id.wallpaper_img);
        }
    }

    @NonNull
    @Override
    public WeatherWallpapersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.general_type_cell, parent, false);
        return new WeatherWallpapersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherWallpapersViewHolder holder, int position) {
        Glide.with(context)
                .load(photosList.get(position).getUrls().getSmall())
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(holder.wallpaperImageView);
    }

    public void addItemsToList(List<UnsplashPhotos> photos){
        photosList.addAll(photos);
    }

    @Override
    public int getItemCount() {
        return photosList.size();
    }

}
