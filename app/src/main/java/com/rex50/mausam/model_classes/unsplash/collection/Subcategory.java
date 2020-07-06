
package com.rex50.mausam.model_classes.unsplash.collection;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subcategory implements Parcelable {

    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("pretty_slug")
    @Expose
    private String prettySlug;

    protected Subcategory(Parcel in) {
        slug = in.readString();
        prettySlug = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(slug);
        dest.writeString(prettySlug);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Subcategory> CREATOR = new Creator<Subcategory>() {
        @Override
        public Subcategory createFromParcel(Parcel in) {
            return new Subcategory(in);
        }

        @Override
        public Subcategory[] newArray(int size) {
            return new Subcategory[size];
        }
    };

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPrettySlug() {
        return prettySlug;
    }

    public void setPrettySlug(String prettySlug) {
        this.prettySlug = prettySlug;
    }

}
