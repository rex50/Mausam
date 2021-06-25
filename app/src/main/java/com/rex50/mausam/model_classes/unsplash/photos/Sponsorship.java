
package com.rex50.mausam.model_classes.unsplash.photos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Sponsorship implements Parcelable {

    @SerializedName("impression_urls")
    @Expose
    private List<String> impressionUrls = null;
    @SerializedName("tagline")
    @Expose
    private String tagline;
    @SerializedName("sponsor")
    @Expose
    private Sponsor sponsor;

    protected Sponsorship(Parcel in) {
        impressionUrls = in.createStringArrayList();
        tagline = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(impressionUrls);
        dest.writeString(tagline);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sponsorship> CREATOR = new Creator<Sponsorship>() {
        @Override
        public Sponsorship createFromParcel(Parcel in) {
            return new Sponsorship(in);
        }

        @Override
        public Sponsorship[] newArray(int size) {
            return new Sponsorship[size];
        }
    };

    public List<String> getImpressionUrls() {
        return impressionUrls;
    }

    public void setImpressionUrls(List<String> impressionUrls) {
        this.impressionUrls = impressionUrls;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public Sponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
    }

}
