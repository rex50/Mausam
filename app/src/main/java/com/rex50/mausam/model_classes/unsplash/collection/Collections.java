
package com.rex50.mausam.model_classes.unsplash.collection;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rex50.mausam.model_classes.unsplash.photos.User;
import com.rex50.mausam.model_classes.utils.MoreListData;
import com.rex50.mausam.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class Collections implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("published_at")
    @Expose
    private String publishedAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("curated")
    @Expose
    private Boolean curated;
    @SerializedName("featured")
    @Expose
    private Boolean featured;
    @SerializedName("total_photos")
    @Expose
    private Integer totalPhotos;
    @SerializedName("private")
    @Expose
    private Boolean _private;
    @SerializedName("share_key")
    @Expose
    private String shareKey;
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;
    @SerializedName("links")
    @Expose
    private OtherLinks links;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("cover_photo")
    @Expose
    private CoverPhoto coverPhoto;
    @SerializedName("preview_photos")
    @Expose
    private List<PreviewPhoto> previewPhotos = null;

    protected Collections(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        publishedAt = in.readString();
        updatedAt = in.readString();
        byte tmpCurated = in.readByte();
        curated = tmpCurated == 0 ? null : tmpCurated == 1;
        byte tmpFeatured = in.readByte();
        featured = tmpFeatured == 0 ? null : tmpFeatured == 1;
        if (in.readByte() == 0) {
            totalPhotos = null;
        } else {
            totalPhotos = in.readInt();
        }
        byte tmp_private = in.readByte();
        _private = tmp_private == 0 ? null : tmp_private == 1;
        shareKey = in.readString();
        tags = in.createTypedArrayList(Tag.CREATOR);
        links = in.readParcelable(OtherLinks.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
        coverPhoto = in.readParcelable(CoverPhoto.class.getClassLoader());
        previewPhotos = in.createTypedArrayList(PreviewPhoto.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(publishedAt);
        dest.writeString(updatedAt);
        dest.writeByte((byte) (curated == null ? 0 : curated ? 1 : 2));
        dest.writeByte((byte) (featured == null ? 0 : featured ? 1 : 2));
        if (totalPhotos == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalPhotos);
        }
        dest.writeByte((byte) (_private == null ? 0 : _private ? 1 : 2));
        dest.writeString(shareKey);
        dest.writeTypedList(tags);
        dest.writeParcelable(links, flags);
        dest.writeParcelable(user, flags);
        dest.writeParcelable(coverPhoto, flags);
        dest.writeTypedList(previewPhotos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Collections> CREATOR = new Creator<Collections>() {
        @Override
        public Collections createFromParcel(Parcel in) {
            return new Collections(in);
        }

        @Override
        public Collections[] newArray(int size) {
            return new Collections[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getCurated() {
        return curated;
    }

    public void setCurated(Boolean curated) {
        this.curated = curated;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Integer getTotalPhotos() {
        return totalPhotos;
    }

    public void setTotalPhotos(Integer totalPhotos) {
        this.totalPhotos = totalPhotos;
    }

    public Boolean getPrivate() {
        return _private;
    }

    public void setPrivate(Boolean _private) {
        this._private = _private;
    }

    public String getShareKey() {
        return shareKey;
    }

    public void setShareKey(String shareKey) {
        this.shareKey = shareKey;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public OtherLinks getLinks() {
        return links;
    }

    public void setLinks(OtherLinks links) {
        this.links = links;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CoverPhoto getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(CoverPhoto coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public List<PreviewPhoto> getPreviewPhotos() {
        return previewPhotos;
    }

    public void setPreviewPhotos(List<PreviewPhoto> previewPhotos) {
        this.previewPhotos = previewPhotos;
    }

    @NotNull
    public MoreListData getMoreListData() {
        return new MoreListData(Constants.ListModes.LIST_MODE_COLLECTION_PHOTOS,
                null, this, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collections that = (Collections) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(publishedAt, that.publishedAt) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(curated, that.curated) &&
                Objects.equals(featured, that.featured) &&
                Objects.equals(totalPhotos, that.totalPhotos) &&
                Objects.equals(_private, that._private) &&
                Objects.equals(shareKey, that.shareKey) &&
                Objects.equals(tags, that.tags) &&
                Objects.equals(links, that.links) &&
                Objects.equals(user, that.user) &&
                Objects.equals(coverPhoto, that.coverPhoto) &&
                Objects.equals(previewPhotos, that.previewPhotos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
