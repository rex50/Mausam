
package com.rex50.mausam.model_classes.unsplash.collection;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtherLinks implements Parcelable {

    @SerializedName("self")
    @Expose
    private String self;
    @SerializedName("html")
    @Expose
    private String html;
    @SerializedName("photos")
    @Expose
    private String photos;
    @SerializedName("related")
    @Expose
    private String related;

    protected OtherLinks(Parcel in) {
        self = in.readString();
        html = in.readString();
        photos = in.readString();
        related = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(self);
        dest.writeString(html);
        dest.writeString(photos);
        dest.writeString(related);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OtherLinks> CREATOR = new Creator<OtherLinks>() {
        @Override
        public OtherLinks createFromParcel(Parcel in) {
            return new OtherLinks(in);
        }

        @Override
        public OtherLinks[] newArray(int size) {
            return new OtherLinks[size];
        }
    };

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getRelated() {
        return related;
    }

    public void setRelated(String related) {
        this.related = related;
    }

}
