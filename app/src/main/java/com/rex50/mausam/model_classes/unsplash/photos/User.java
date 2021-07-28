
package com.rex50.mausam.model_classes.unsplash.photos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rex50.mausam.model_classes.utils.MoreListData;
import com.rex50.mausam.utils.Constants;

import java.util.Objects;

public class User implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("twitter_username")
    @Expose
    private String twitterUsername;
    @SerializedName("portfolio_url")
    @Expose
    private String portfolioUrl;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("links")
    @Expose
    private UserLinks links;
    @SerializedName("profile_image")
    @Expose
    private ProfileImage profileImage;
    @SerializedName("instagram_username")
    @Expose
    private String instagramUsername;
    @SerializedName("total_collections")
    @Expose
    private Integer totalCollections;
    @SerializedName("total_likes")
    @Expose
    private Integer totalLikes;
    @SerializedName("total_photos")
    @Expose
    private Integer totalPhotos;
    @SerializedName("accepted_tos")
    @Expose
    private Boolean acceptedTos;

    protected User(Parcel in) {
        id = in.readString();
        updatedAt = in.readString();
        username = in.readString();
        name = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        twitterUsername = in.readString();
        portfolioUrl = in.readString();
        bio = in.readString();
        location = in.readString();
        links = in.readParcelable(UserLinks.class.getClassLoader());
        profileImage = in.readParcelable(ProfileImage.class.getClassLoader());
        instagramUsername = in.readString();
        if (in.readByte() == 0) {
            totalCollections = null;
        } else {
            totalCollections = in.readInt();
        }
        if (in.readByte() == 0) {
            totalLikes = null;
        } else {
            totalLikes = in.readInt();
        }
        if (in.readByte() == 0) {
            totalPhotos = null;
        } else {
            totalPhotos = in.readInt();
        }
        byte tmpAcceptedTos = in.readByte();
        acceptedTos = tmpAcceptedTos == 0 ? null : tmpAcceptedTos == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(updatedAt);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(twitterUsername);
        dest.writeString(portfolioUrl);
        dest.writeString(bio);
        dest.writeString(location);
        dest.writeParcelable(links, flags);
        dest.writeParcelable(profileImage, flags);
        dest.writeString(instagramUsername);
        if (totalCollections == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalCollections);
        }
        if (totalLikes == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalLikes);
        }
        if (totalPhotos == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalPhotos);
        }
        dest.writeByte((byte) (acceptedTos == null ? 0 : acceptedTos ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTwitterUsername() {
        return twitterUsername;
    }

    public void setTwitterUsername(String twitterUsername) {
        this.twitterUsername = twitterUsername;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserLinks getLinks() {
        return links;
    }

    public void setLinks(UserLinks links) {
        this.links = links;
    }

    public ProfileImage getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    public String getInstagramUsername() {
        return instagramUsername;
    }

    public void setInstagramUsername(String instagramUsername) {
        this.instagramUsername = instagramUsername;
    }

    public Integer getTotalCollections() {
        return totalCollections;
    }

    public void setTotalCollections(Integer totalCollections) {
        this.totalCollections = totalCollections;
    }

    public Integer getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Integer totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Integer getTotalPhotos() {
        return totalPhotos;
    }

    public void setTotalPhotos(Integer totalPhotos) {
        this.totalPhotos = totalPhotos;
    }

    public Boolean getAcceptedTos() {
        return acceptedTos;
    }

    public void setAcceptedTos(Boolean acceptedTos) {
        this.acceptedTos = acceptedTos;
    }

    public MoreListData getMoreListData() {
        return new MoreListData(Constants.ListModes.LIST_MODE_USER_PHOTOS, this, null, null);
    }

    public boolean hasCollections() {
        return totalCollections > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                Objects.equals(updatedAt, user.updatedAt) &&
                Objects.equals(username, user.username) &&
                Objects.equals(name, user.name) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(twitterUsername, user.twitterUsername) &&
                Objects.equals(portfolioUrl, user.portfolioUrl) &&
                Objects.equals(bio, user.bio) &&
                Objects.equals(location, user.location) &&
                Objects.equals(links, user.links) &&
                Objects.equals(profileImage, user.profileImage) &&
                Objects.equals(instagramUsername, user.instagramUsername) &&
                Objects.equals(totalCollections, user.totalCollections) &&
                Objects.equals(totalLikes, user.totalLikes) &&
                Objects.equals(totalPhotos, user.totalPhotos) &&
                Objects.equals(acceptedTos, user.acceptedTos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
