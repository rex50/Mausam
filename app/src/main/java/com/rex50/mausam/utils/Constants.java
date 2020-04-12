package com.rex50.mausam.utils;

import com.rex50.mausam.R;

public class Constants {

    public static class AvailableLayouts {

        public static final String WEATHER_BASED_WALLPAPERS = "Weather based wallpapers";
        public static final String LOCATION_BASED_WALLPAPERS = "Location based wallpapers";
        public static final String TIME_BASED_WALLPAPERS = "Time based wallpapers";
        public static final String POPULAR_WALLPAPERS = "Popular wallpapers";
        public static final String POPULAR_PHOTOGRAPHERS = "Popular photographers";
        public static final String POPULAR_TAGS = "Popular tags";
        public static final String FEATURED_COLLECTIONS = "Featured collections";
        public static final String BROWSE_BY_CATEGORIES = "Browse by categories";
        public static final String BROWSE_BY_COLORS = "Browse by colors";
        public static final String FAVOURITE_PHOTOGRAPHER_IMAGES = "Favourite photographer’s images";

    }

    public static class RecyclerItemTypes {

        public static final int AD_TYPE = 10010;
        public static final int ITEM_CATEGORY_TYPE = R.layout.item_category;
        public static final int FAVOURITE_PHOTOGRAPHER_PHOTOS_TYPE = R.layout.item_category + 1;
        public static final int END_IMAGE = 10012;


        public static final int GENERAL_TYPE = R.layout.general_type_cell;
        public static final int COLOR_TYPE = R.layout.color_type_cell;
        public static final int USER_TYPE = R.layout.user_type_cell;
        public static final int COLLECTION_TYPE = R.layout.collection_type_cell;
        public static final int TEXT_TYPE = R.layout.text_type_cell;

    }

    public static class Providers {
        public static final String POWERED_BY_UNSPLASH = "Powered by Unsplash";
    }

}
