
package com.rex50.mausam.model_classes.unsplash.photos;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rex50.mausam.model_classes.unsplash.collection.Category;
import com.rex50.mausam.utils.Constants;
import com.rex50.mausam.MausamApplication;

import java.util.List;

public class UnsplashPhotos implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("promoted_at")
    @Expose
    private String promotedAt;
    @SerializedName("width")
    @Expose
    private Integer width;
    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("alt_description")
    @Expose
    private String altDescription;
    @SerializedName("urls")
    @Expose
    private Urls urls;
    @SerializedName("links")
    @Expose
    private DownloadLinks links;
    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("liked_by_user")
    @Expose
    private Boolean likedByUser;
    @SerializedName("current_user_collections")
    @Expose
    private List<Object> currentUserCollections = null;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("sponsorship")
    @Expose
    private Sponsorship sponsorship;

    @SerializedName("relativePath")
    @Expose
    private String relativePath;
    
    @SerializedName("isFavourite")
    @Expose
    private boolean isFavorite;
    

    public UnsplashPhotos() { }

    protected UnsplashPhotos(Parcel in) {
        id = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        promotedAt = in.readString();
        if (in.readByte() == 0) {
            width = null;
        } else {
            width = in.readInt();
        }
        if (in.readByte() == 0) {
            height = null;
        } else {
            height = in.readInt();
        }
        color = in.readString();
        description = in.readString();
        altDescription = in.readString();
        urls = in.readParcelable(Urls.class.getClassLoader());
        links = in.readParcelable(DownloadLinks.class.getClassLoader());
        categories = in.createTypedArrayList(Category.CREATOR);
        if (in.readByte() == 0) {
            likes = null;
        } else {
            likes = in.readInt();
        }
        byte tmpLikedByUser = in.readByte();
        likedByUser = tmpLikedByUser == 0 ? null : tmpLikedByUser == 1;
        user = in.readParcelable(User.class.getClassLoader());
        relativePath = in.readString();
        isFavorite = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(promotedAt);
        if (width == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(width);
        }
        if (height == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(height);
        }
        dest.writeString(color);
        dest.writeString(description);
        dest.writeString(altDescription);
        dest.writeParcelable(urls, flags);
        dest.writeParcelable(links, flags);
        dest.writeTypedList(categories);
        if (likes == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(likes);
        }
        dest.writeByte((byte) (likedByUser == null ? 0 : likedByUser ? 1 : 2));
        dest.writeParcelable(user, flags);
        dest.writeString(relativePath);
        dest.writeInt(isFavorite ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UnsplashPhotos> CREATOR = new Creator<UnsplashPhotos>() {
        @Override
        public UnsplashPhotos createFromParcel(Parcel in) {
            return new UnsplashPhotos(in);
        }

        @Override
        public UnsplashPhotos[] newArray(int size) {
            return new UnsplashPhotos[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPromotedAt() {
        return promotedAt;
    }

    public void setPromotedAt(String promotedAt) {
        this.promotedAt = promotedAt;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        if(description == null) return "";
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAltDescription() {
        if(altDescription == null) return "";
        return altDescription;
    }

    public void setAltDescription(String altDescription) {
        this.altDescription = altDescription;
    }

    public Urls getUrls() {
        return urls;
    }

    public void setUrls(Urls urls) {
        this.urls = urls;
    }

    public DownloadLinks getLinks() {
        return links;
    }

    public void setLinks(DownloadLinks links) {
        this.links = links;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Boolean getLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(Boolean likedByUser) {
        this.likedByUser = likedByUser;
    }

    public List<Object> getCurrentUserCollections() {
        return currentUserCollections;
    }

    public void setCurrentUserCollections(List<Object> currentUserCollections) {
        this.currentUserCollections = currentUserCollections;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Sponsorship getSponsorship() {
        return sponsorship;
    }

    public void setSponsorship(Sponsorship sponsorship) {
        this.sponsorship = sponsorship;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String getJSON(){
        try {
            return new Gson().toJson(this);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            return "";
        }
    }

    public String getDBId() {
        return Constants.Image.DOWNLOADED_IMAGE +
                id + "_" +
                getDescription().replace(" ", "").trim() + "_" +
                getAltDescription().replace(" ", "").trim();
    }

    @Nullable
    public static UnsplashPhotos getModelFromJSON(String jsonString){
        try {
            return new Gson().fromJson(jsonString, UnsplashPhotos.class);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
    }

    public String createRelativePath(String filename) {
        return MausamApplication.getAppFolder() + filename + "_" + id + ".jpg";
    }

}
