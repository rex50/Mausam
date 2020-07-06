
package com.rex50.mausam.model_classes.unsplash.photos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DownloadLinks implements Parcelable {

    @SerializedName("self")
    @Expose
    private String self;
    @SerializedName("html")
    @Expose
    private String html;
    @SerializedName("download")
    @Expose
    private String download;
    @SerializedName("download_location")
    @Expose
    private String downloadLocation;

    protected DownloadLinks(Parcel in) {
        self = in.readString();
        html = in.readString();
        download = in.readString();
        downloadLocation = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(self);
        dest.writeString(html);
        dest.writeString(download);
        dest.writeString(downloadLocation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DownloadLinks> CREATOR = new Creator<DownloadLinks>() {
        @Override
        public DownloadLinks createFromParcel(Parcel in) {
            return new DownloadLinks(in);
        }

        @Override
        public DownloadLinks[] newArray(int size) {
            return new DownloadLinks[size];
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

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getDownloadLocation() {
        return downloadLocation;
    }

    public void setDownloadLocation(String downloadLocation) {
        this.downloadLocation = downloadLocation;
    }

}
