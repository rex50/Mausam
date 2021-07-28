package com.rex50.mausam.model_classes.utils

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.utils.Constants
import com.rex50.mausam.views.activities.ActMain
import org.apache.commons.lang3.StringUtils

data class MoreListData(
        @ActMain.photosListMode var listMode: String? = Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
        var photographerInfo: User? = null,
        var collectionInfo: Collections? = null,
        var generalInfo: MoreData? = null
): Parcelable {

    fun getTitle(context: Context?): String = when(listMode){
        Constants.ListModes.LIST_MODE_COLLECTION_PHOTOS -> {
            StringUtils.capitalize(collectionInfo?.title)?.takeIf { it.isNotEmpty() } ?: Constants.COLLECTIONS_PHOTOS
        }

        Constants.ListModes.LIST_MODE_USER_PHOTOS -> {
            photographerInfo?.name?.takeIf { it.isNotEmpty() }?.let {
                context?.getString(R.string.photo_by_photographer, StringUtils.capitalize(it))
            } ?: context?.getString(R.string.photographer_photos) ?: Constants.PHOTOS
        }

        Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
        Constants.ListModes.LIST_MODE_POPULAR_PHOTOS -> {
            StringUtils.capitalize(generalInfo?.term)?.takeIf { it.isNotEmpty() } ?: Constants.PHOTOS
        }

        Constants.ListModes.LIST_MODE_USER_COLLECTIONS -> {
            photographerInfo?.name?.takeIf { it.isNotEmpty() }?.let {
                context?.getString(R.string.collection_by_photographer, StringUtils.capitalize(it))
            } ?: context?.getString(R.string.photographer_collections) ?: Constants.COLLECTIONS
        }

        Constants.ListModes.LIST_MODE_COLLECTIONS -> {
            generalInfo?.term?.takeIf { it.isNotEmpty() } ?: Constants.COLLECTIONS
        }

        else -> throw IllegalArgumentException("No title found for $listMode")
    }

    fun getDesc(): String = when(listMode){
        Constants.ListModes.LIST_MODE_COLLECTION_PHOTOS -> {
            collectionInfo?.description?.takeIf { it.isNotEmpty() } ?: ""
        }

        Constants.ListModes.LIST_MODE_USER_PHOTOS,
        Constants.ListModes.LIST_MODE_USER_COLLECTIONS -> {
            photographerInfo?.bio?.takeIf { it.isNotEmpty() } ?: generalInfo?.desc ?: Constants.Providers.POWERED_BY_UNSPLASH
        }

        Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
        Constants.ListModes.LIST_MODE_POPULAR_PHOTOS,
        Constants.ListModes.LIST_MODE_COLLECTIONS -> {
            generalInfo?.desc?.takeIf { it.isNotEmpty() } ?: Constants.Providers.POWERED_BY_UNSPLASH
        }

        else -> throw IllegalArgumentException("No desc found for $listMode")
    }

    fun getPhotosCount(): String = when(listMode) {
        Constants.ListModes.LIST_MODE_COLLECTION_PHOTOS -> {
            "${collectionInfo?.totalPhotos} Photos"
        }

        else -> ""
    }

    fun getBgImgUrl(): String = when(listMode){
        Constants.ListModes.LIST_MODE_COLLECTION_PHOTOS -> {
            collectionInfo?.let { col ->
                col.user?.profileImage?.large?.takeIf { !it.contains("placeholder-avatars") }
                    ?: col.coverPhoto?.urls?.small
            } ?: ""
        }

        Constants.ListModes.LIST_MODE_USER_COLLECTIONS -> {
            photographerInfo?.profileImage?.large ?: ""
        }

        else -> ""
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(User::class.java.classLoader),
            parcel.readParcelable(Collections::class.java.classLoader),
            parcel.readParcelable(MoreData::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(listMode)
        parcel.writeParcelable(photographerInfo, flags)
        parcel.writeParcelable(collectionInfo, flags)
        parcel.writeParcelable(generalInfo, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MoreListData> {
        override fun createFromParcel(parcel: Parcel): MoreListData {
            return MoreListData(parcel)
        }

        override fun newArray(size: Int): Array<MoreListData?> {
            return arrayOfNulls(size)
        }
    }
}

data class MoreData(
        var term: String?,
        var desc: String? = Constants.Providers.POWERED_BY_UNSPLASH
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(term)
        parcel.writeString(desc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MoreData> {
        override fun createFromParcel(parcel: Parcel): MoreData {
            return MoreData(parcel)
        }

        override fun newArray(size: Int): Array<MoreData?> {
            return arrayOfNulls(size)
        }
    }
}