package com.rex50.mausam.utils

import com.rex50.mausam.R

class Constants {

    object AvailableLayouts {
        const val WEATHER_BASED_WALLPAPERS = "Weather based wallpapers"
        const val LOCATION_BASED_WALLPAPERS = "Location based wallpapers"
        const val TIME_BASED_WALLPAPERS = "Time based wallpapers"
        const val POPULAR_WALLPAPERS = "Popular wallpapers"
        const val POPULAR_PHOTOGRAPHERS = "Popular photographers"
        const val POPULAR_TAGS = "Popular tags"
        const val FEATURED_COLLECTIONS = "Featured collections"
        const val BROWSE_BY_CATEGORIES = "Browse by categories"
        const val BROWSE_BY_COLORS = "Browse by colors"
        const val FAVOURITE_PHOTOGRAPHER_IMAGES = "Favourite photographer’s images"
    }

    object RecyclerItemTypes {
        const val AD_TYPE = 10010
        const val ITEM_CATEGORY_TYPE = R.layout.item_category
        const val FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_TYPE = R.layout.item_category + 1
        const val END_IMAGE = 10012
        const val GENERAL_TYPE = R.layout.cell_type_general
        const val FAV_PHOTOGRAPHER_PHOTOS_TYPE = R.layout.cell_type_fav_photograher_photo
        const val COLOR_TYPE = R.layout.cell_type_color
        const val CATEGORY_TYPE = R.layout.cell_type_category
        const val USER_TYPE = R.layout.cell_type_user
        const val COLLECTION_TYPE = R.layout.cell_type_collection
        const val COLLECTION_LIST_TYPE = R.layout.cell_type_collection_list
        const val TAG_TYPE = R.layout.cell_type_tag
    }

    object Providers {
        const val POWERED_BY_UNSPLASH = "Powered by Unsplash"
    }

    object Units {
        const val PX = "px"
    }

    object Image {
        const val JPEG_NAME_PATTERN = "%s.jpg"
        const val DOWNLOAD_RELATIVE_PATH = "Pictures/Mausam/Downloads/"
        const val FAV_RELATIVE_PATH = "Pictures/Mausam/Favourites/"
        const val SAVE_MIME_TYPE = "image/jpg"
    }

    object Util {
        const val VALUE_DIVIDER = "~Mausam~"
    }

    object File {
        const val READ_ONLY = "r"
        const val READ_AND_WRITE = "rw"
        const val TRUNCATE_OR_OVERWRITE = "rwt"
    }

    object ApiConstants {
        const val UNSPLASH_USERNAME = "unsplash_username"
    }

    object IntentConstants {
        const val WALLPAPER_LIST_DATA = "wallpaperListData"
        const val LIST_MODE_POPULAR_WALLPAPER = "popularWallpaperListMode"
        const val LIST_MODE_PHOTOGRAPHER_WALLPAPER = "photographerWallpaperListMode"
        const val LIST_MODE_GENERAL_WALLPAPER = "generalWallpaperListMode"
        const val SEARCH_TERM = "searchTerm"
        const val SEARCH_DESC = "searchDesc"
        const val SEARCH_FEAT_COLLECTION = "searchFeaturedCollection"
    }
}