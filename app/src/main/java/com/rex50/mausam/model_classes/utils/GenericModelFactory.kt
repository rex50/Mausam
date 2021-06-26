package com.rex50.mausam.model_classes.utils

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.item_types.*
import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.unsplash.collection.Tag
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.CATEGORY_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.COLLECTION_LIST_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.COLLECTION_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.COLOR_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.FAV_PHOTOGRAPHER_PHOTOS_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.GENERAL_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.ITEM_CATEGORY_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.ITEM_SECTION_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.TAG_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.USER_TYPE

abstract class GenericModelFactory{

    //private int endImage = R.drawable.banner_bg_1;

    /**
     * Used for Section/Category UI
     *
     * This value will decide which holder to use in onBindViewHolder of AdaptHome
     */
    var viewType: Int = ITEM_CATEGORY_TYPE
        protected set

    /**
     * Used for Section/Category UI
     *
     * This value will decide which layout to inflate for the
     * parent UI in onCreateViewHolder of AdaptHome
     */
    var viewLayout = R.layout.item_category
        protected set

    /**
     * Used for Child UI items inside a Category/Section
     *
     * This value will decide which holder to use in onBindViewHolder of AdaptContent
     */
    var itemType: Int = GENERAL_TYPE
        protected set

    /**
     * Used for Child UI items inside a Category/Section
     *
     * This value will decide which layout to inflate for the
     * child UI in onCreateViewHolder of AdaptContent
     */
    var itemLayout = R.layout.cell_type_general
        protected set

    /**
     * Title for the Category/Section
     */
    var title = ""
        protected set

    /**
     * Description for the Category
     */
    var desc = ""
        protected set

    /**
     * If Category/Section has more items than what we are showing currently then pass {@code true}
     *
     * If true then a button will be shown at the end of Category/Section title
     */
    var isHasMore = true
        protected set

    /**
     * Icon for showing before Section title
     */
    @DrawableRes
    var icon: Int = -1
        protected set

    /**
     * Scroll direction of the child items
     */
    var scrollDirection = LinearLayoutManager.HORIZONTAL
        protected set


    abstract fun getTotalItems(): Int

    abstract fun <Type> get(pos: Int): Type

    companion object {
        fun getGeneralTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            photosList: List<UnsplashPhotos>
        ): GenericModelFactory {
            val generalTypeModel = GeneralTypeModel(photosList)
            generalTypeModel.title = title
            generalTypeModel.desc = desc
            generalTypeModel.isHasMore = hasMore
            generalTypeModel.itemType = GENERAL_TYPE
            generalTypeModel.itemLayout = R.layout.cell_type_general
            return generalTypeModel
        }

        fun getUserTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            usersList: List<User>
        ): GenericModelFactory {
            val generalTypeModel = UserTypeModel(usersList)
            generalTypeModel.title = title
            generalTypeModel.desc = desc
            generalTypeModel.isHasMore = hasMore
            generalTypeModel.itemType = USER_TYPE
            generalTypeModel.itemLayout = R.layout.cell_type_user
            return generalTypeModel
        }

        fun getCollectionTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            collectionsList: List<Collections>
        ): GenericModelFactory {
            val generalTypeModel = CollectionTypeModel(collectionsList)
            generalTypeModel.title = title
            generalTypeModel.desc = desc
            generalTypeModel.isHasMore = hasMore
            generalTypeModel.itemType = COLLECTION_TYPE
            generalTypeModel.itemLayout = R.layout.cell_type_collection
            return generalTypeModel
        }

        fun getCollectionListTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            collectionsList: List<Collections>
        ): GenericModelFactory {
            val generalTypeModel = CollectionTypeModel(collectionsList)
            generalTypeModel.title = title
            generalTypeModel.desc = desc
            generalTypeModel.isHasMore = hasMore
            generalTypeModel.itemType = COLLECTION_LIST_TYPE
            generalTypeModel.itemLayout = R.layout.cell_type_collection_list
            return generalTypeModel
        }

        fun getTagTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            tagsList: List<Tag?>,
            shuffleList: Boolean
        ): GenericModelFactory {
            val textTypeModel = TagTypeModel(tagsList, shuffleList)
            textTypeModel.title = title
            textTypeModel.desc = desc
            textTypeModel.isHasMore = hasMore
            textTypeModel.itemType = TAG_TYPE
            textTypeModel.itemLayout = R.layout.cell_type_tag
            return textTypeModel
        }

        fun getColorTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            colorsList: List<ColorModel>,
            shuffleList: Boolean
        ): GenericModelFactory {
            val textTypeModel = ColorTypeModel(colorsList, shuffleList)
            textTypeModel.title = title
            textTypeModel.desc = desc
            textTypeModel.isHasMore = hasMore
            textTypeModel.itemType = COLOR_TYPE
            textTypeModel.itemLayout = R.layout.cell_type_color
            return textTypeModel
        }

        fun getCategoryTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            categories: List<CategoryModel?>,
            shuffleList: Boolean
        ): GenericModelFactory {
            val textTypeModel = CategoryTypeModel(categories, shuffleList)
            textTypeModel.title = title
            textTypeModel.desc = desc
            textTypeModel.isHasMore = hasMore
            textTypeModel.itemType = CATEGORY_TYPE
            textTypeModel.itemLayout = R.layout.cell_type_category
            return textTypeModel
        }

        fun getDownloadedSectionTypeObject(
            sectionTitle: String,
            @DrawableRes icon: Int,
            hasMore: Boolean,
            photosList: List<UnsplashPhotos>
        ): GenericModelFactory {
            return GeneralTypeModel(photosList).apply {
                title = sectionTitle
                isHasMore = hasMore
                this.icon = icon
                viewType = ITEM_SECTION_TYPE
                viewLayout = ITEM_SECTION_TYPE
                itemType = GENERAL_TYPE
                itemLayout = R.layout.cell_type_general
            }
        }

        fun getRecommendedUserSectionTypeObject(
            sectionTitle: String,
            @DrawableRes icon: Int,
            hasMore: Boolean,
            usersList: List<User>
        ): GenericModelFactory {
            return UserTypeModel(usersList).apply {
                title = sectionTitle
                isHasMore = hasMore
                this.icon = icon
                viewType = ITEM_SECTION_TYPE
                viewLayout = ITEM_SECTION_TYPE
                itemType = USER_TYPE
                itemLayout = R.layout.cell_type_user
            }
        }

        fun getFavouritePhotographerTypeObject(
            title: String,
            desc: String,
            photosList: List<UnsplashPhotos>
        ): GenericModelFactory {
            val favouriteModel = FavouritePhotographerTypeModel(photosList)
            favouriteModel.title = title
            favouriteModel.desc = desc
            favouriteModel.isHasMore = false
            favouriteModel.itemType = FAV_PHOTOGRAPHER_PHOTOS_TYPE
            favouriteModel.viewType = FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_TYPE
            favouriteModel.itemLayout = R.layout.cell_type_fav_photograher_photo
            favouriteModel.scrollDirection = LinearLayoutManager.VERTICAL
            return favouriteModel
        }

    }
}