
package com.rex50.mausam.model_classes.unsplash.collection;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rex50.mausam.model_classes.utils.MoreData;
import com.rex50.mausam.model_classes.utils.MoreListData;
import com.rex50.mausam.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Tag implements Parcelable {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("source")
    @Expose
    private Source source;

    protected Tag(Parcel in) {
        type = in.readString();
        title = in.readString();
        source = in.readParcelable(Source.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(title);
        dest.writeParcelable(source, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    @NotNull
    public MoreListData getMoreListData() {
        return new MoreListData(Constants.ListModes.LIST_MODE_GENERAL_PHOTOS, null, null,
                new MoreData(title, Constants.Providers.POWERED_BY_UNSPLASH));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(type, tag.type) &&
                Objects.equals(title, tag.title) &&
                Objects.equals(source, tag.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
