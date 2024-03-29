package com.rex50.mausam.utils

import com.rex50.mausam.R

class Constants {

    companion object {
        const val PHOTOS = "Photos"
        const val COLLECTIONS = "Collections"
        const val COLLECTIONS_PHOTOS = "Collections Photos"
    }

    object Configs {
        const val MAX_BITMAP_SIZE = 100 * 1024 * 1024
    }

    object AvailableLayouts {
        //Home
        const val WEATHER_BASED_PHOTOS = "Season based photos"
        const val LOCATION_BASED_PHOTOS = "Random location photos"
        const val TIME_BASED_PHOTOS = "Time based photos"
        const val POPULAR_PHOTOS = "Popular photos"
        const val POPULAR_PHOTOGRAPHERS = "Popular photographers"
        const val POPULAR_TAGS = "Popular tags"
        const val FEATURED_COLLECTIONS = "Featured collections"
        const val BROWSE_BY_CATEGORIES = "Browse by categories"
        const val BROWSE_BY_COLORS = "Browse by colors"
        const val FAVOURITE_PHOTOGRAPHER_IMAGES = "Favourite photographer’s images"

        //Recommendation
        const val DOWNLOADED_PHOTOS = "Downloaded photos"
        const val RECOMMENDED_PHOTOGRAPHERS = "You might me interested in"
        const val FAVOURITE_PHOTOS = "Your favourite photos"
    }

    object RecyclerItemTypes {
        const val AD_TYPE = 10010

        const val GROUP_CATEGORY_TYPE = 10001
        const val GROUP_SECTION_TYPE = 10002

        const val FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_TYPE = 10003
        const val GENERAL_TYPE = 10004
        const val FAV_PHOTOGRAPHER_PHOTOS_TYPE = 10005
        const val COLOR_TYPE = 10006
        const val CATEGORY_TYPE = 10007
        const val USER_TYPE = 10008
        const val COLLECTION_TYPE = 10009
        const val COLLECTION_LIST_TYPE = 10010
        const val TAG_TYPE = 10011
        const val END_IMAGE = 10012
    }

    object RecyclerItemLayouts {

        const val GROUP_CATEGORY_LAYOUT = R.layout.item_category
        const val GROUP_SECTION_LAYOUT = R.layout.item_section

        const val FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_LAYOUT = R.layout.item_category
        const val GENERAL_LAYOUT = R.layout.cell_type_general
        const val FAV_PHOTOGRAPHER_PHOTOS_LAYOUT = R.layout.cell_type_fav_photograher_photo
        const val COLOR_LAYOUT = R.layout.cell_type_color
        const val CATEGORY_LAYOUT = R.layout.cell_type_category
        const val USER_LAYOUT = R.layout.cell_type_user
        const val COLLECTION_LAYOUT = R.layout.cell_type_collection
        const val COLLECTION_LIST_LAYOUT = R.layout.cell_type_collection_list
        const val TAG_LAYOUT = R.layout.cell_type_tag

    }

    object Providers {
        const val POWERED_BY_UNSPLASH = "Powered by Unsplash"
    }

    object Units {
        const val PX = "px"
    }

    object Image {

        object Extensions {
            const val JPG = ".jpg"
        }

        const val JPEG_NAME_PATTERN = "%s.jpg"
        const val DOWNLOAD_RELATIVE_PATH = "Pictures/Mausam/Downloads/"
        const val FAV_RELATIVE_PATH = "Pictures/Mausam/Favourites/"
        const val SAVE_MIME_TYPE = "image/jpg"

        const val DOWNLOADED_IMAGE = "Downloaded_image_"
    }

    object Util {
        const val VALUE_DIVIDER = "~Mausam~"

        var userFavConstants = arrayListOf(
            "Pet",
            "Car",
            "Cartoon",
            "Anime",
            "Motorcycle",
            "Animal",
            "Bird",
            "Gadget",
        )
    }

    object File {
        const val READ_ONLY = "r"
        const val READ_AND_WRITE = "rw"
        const val TRUNCATE_OR_OVERWRITE = "rwt"
    }

    object ApiConstants {
        const val UNSPLASH_USERNAME = "unsplash_username"
        const val COLLECTION_ID = "collection_id"
        const val DOWNLOADING_PHOTO_URL = "downloadingPhotoUrl"
    }

    object Network {
        const val NO_INTERNET = "noInternet"
    }

    object IntentConstants {
        const val LIST_DATA = "photoListData"
        const val SEARCH_FEAT_COLLECTION = "searchFeaturedCollection"
        const val NAME = "name"
        const val IS_ADD_FAV = "isAddFav"
        const val PHOTO_DATA = "photoData"
    }

    object ListModes {
        const val LIST_MODE_POPULAR_PHOTOS = "popularPhotosListMode"
        const val LIST_MODE_USER_PHOTOS = "userPhotosListMode"
        const val LIST_MODE_GENERAL_PHOTOS = "generalPhotosListMode"
        const val LIST_MODE_COLLECTION_PHOTOS = "collectionPhotosListMode"
        const val LIST_MODE_COLLECTIONS = "collectionListMode"
        const val LIST_MODE_USER_COLLECTIONS = "userCollectionListMode"
    }
}