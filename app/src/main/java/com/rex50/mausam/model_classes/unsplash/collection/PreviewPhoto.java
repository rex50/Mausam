
package com.rex50.mausam.model_classes.unsplash.collection;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rex50.mausam.model_classes.unsplash.photos.Urls;

public class PreviewPhoto implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("urls")
    @Expose
    private Urls urls;

    protected PreviewPhoto(Parcel in) {
        id = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        urls = in.readParcelable(Urls.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeParcelable(urls, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PreviewPhoto> CREATOR = new Creator<PreviewPhoto>() {
        @Override
        public PreviewPhoto createFromParcel(Parcel in) {
            return new PreviewPhoto(in);
        }

        @Override
        public PreviewPhoto[] newArray(int size) {
            return new PreviewPhoto[size];
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

    public Urls getUrls() {
        return urls;
    }

    public void setUrls(Urls urls) {
        this.urls = urls;
    }

}
