package com.rex50.mausam.model_classes.utils

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.rex50.mausam.R
import com.rex50.mausam.model_classes.item_types.*
import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.unsplash.collection.Tag
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.utils.Constants.RecyclerItemLayouts.CATEGORY_LAYOUT
import com.rex50.mausam.utils.Constants.RecyclerItemLayouts.COLLECTION_LAYOUT
import com.rex50.mausam.utils.Constants.RecyclerItemLayouts.COLLECTION_LIST_LAYOUT
import com.rex50.mausam.utils.Constants.RecyclerItemLayouts.COLOR_LAYOUT
import com.rex50.mausam.utils.Constants.RecyclerItemLayouts.FAV_PHOTOGRAPHER_PHOTOS_LAYOUT
import com.rex50.mausam.utils.Constants.RecyclerItemLayouts.GENERAL_LAYOUT
import com.rex50.mausam.utils.Constants.RecyclerItemLayouts.GROUP_SECTION_LAYOUT
import com.rex50.mausam.utils.Constants.RecyclerItemLayouts.TAG_LAYOUT
import com.rex50.mausam.utils.Constants.RecyclerItemLayouts.USER_LAYOUT
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.CATEGORY_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.COLLECTION_LIST_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.COLLECTION_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.COLOR_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.FAV_PHOTOGRAPHER_PHOTOS_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.GENERAL_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.GROUP_CATEGORY_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.GROUP_SECTION_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.TAG_TYPE
import com.rex50.mausam.utils.Constants.RecyclerItemTypes.USER_TYPE

abstract class GenericModelFactory{

    //private int endImage = R.drawable.banner_bg_1;

    /**
     * Used for Section/Category UI
     *
     * This value will decide which holder to use in onBindViewHolder of AdaptHome
     */
    var groupType: Int = GROUP_CATEGORY_TYPE
        protected set

    /**
     * Used for Section/Category UI
     *
     * This value will decide which layout to inflate for the
     * parent UI in onCreateViewHolder of AdaptHome
     */
    var groupLayout = R.layout.item_category
        protected set

    /**
     * Used for Child UI items inside a Category/Section
     *
     * This value will decide which holder to use in onBindViewHolder of AdaptContent
     */
    var childType: Int = GENERAL_TYPE
        protected set

    /**
     * Used for Child UI items inside a Category/Section
     *
     * This value will decide which layout to inflate for the
     * child UI in onCreateViewHolder of AdaptContent
     */
    var childLayout = R.layout.cell_type_general
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

    abstract fun getList(): List<Any>

    abstract fun <Type> get(pos: Int): Type

    companion object {
        fun getGeneralTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            photosList: List<UnsplashPhotos>
        ): GeneralTypeModel {
            val generalTypeModel = GeneralTypeModel(photosList)
            generalTypeModel.title = title
            generalTypeModel.desc = desc
            generalTypeModel.isHasMore = hasMore
            generalTypeModel.childType = GENERAL_TYPE
            generalTypeModel.childLayout = GENERAL_LAYOUT
            return generalTypeModel
        }

        fun getUserTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            usersList: List<User>
        ): UserTypeModel {
            val generalTypeModel = UserTypeModel(usersList)
            generalTypeModel.title = title
            generalTypeModel.desc = desc
            generalTypeModel.isHasMore = hasMore
            generalTypeModel.childType = USER_TYPE
            generalTypeModel.childLayout = USER_LAYOUT
            return generalTypeModel
        }

        fun getCollectionTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            collectionsList: List<Collections>
        ): CollectionTypeModel {
            val generalTypeModel = CollectionTypeModel(collectionsList)
            generalTypeModel.title = title
            generalTypeModel.desc = desc
            generalTypeModel.isHasMore = hasMore
            generalTypeModel.childType = COLLECTION_TYPE
            generalTypeModel.childLayout = COLLECTION_LAYOUT
            return generalTypeModel
        }

        fun getCollectionListTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            collectionsList: List<Collections>
        ): CollectionTypeModel {
            val collectionTypeModel = CollectionTypeModel(collectionsList)
            collectionTypeModel.title = title
            collectionTypeModel.desc = desc
            collectionTypeModel.isHasMore = hasMore
            collectionTypeModel.childType = COLLECTION_LIST_TYPE
            collectionTypeModel.childLayout = COLLECTION_LIST_LAYOUT
            return collectionTypeModel
        }

        fun getTagTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            tagsList: List<Tag>,
            shuffleList: Boolean
        ): TagTypeModel {
            val textTypeModel = TagTypeModel(tagsList, shuffleList)
            textTypeModel.title = title
            textTypeModel.desc = desc
            textTypeModel.isHasMore = hasMore
            textTypeModel.childType = TAG_TYPE
            textTypeModel.childLayout = TAG_LAYOUT
            return textTypeModel
        }

        fun getColorTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            colorsList: List<ColorModel>,
            shuffleList: Boolean
        ): ColorTypeModel {
            val textTypeModel = ColorTypeModel(colorsList, shuffleList)
            textTypeModel.title = title
            textTypeModel.desc = desc
            textTypeModel.isHasMore = hasMore
            textTypeModel.childType = COLOR_TYPE
            textTypeModel.childLayout = COLOR_LAYOUT
            return textTypeModel
        }

        fun getCategoryTypeObject(
            title: String,
            desc: String,
            hasMore: Boolean,
            categories: List<CategoryModel>,
            shuffleList: Boolean
        ): CategoryTypeModel {
            val textTypeModel = CategoryTypeModel(categories, shuffleList)
            textTypeModel.title = title
            textTypeModel.desc = desc
            textTypeModel.isHasMore = hasMore
            textTypeModel.childType = CATEGORY_TYPE
            textTypeModel.childLayout = CATEGORY_LAYOUT
            return textTypeModel
        }

        fun getDownloadedSectionTypeObject(
            sectionTitle: String,
            @DrawableRes icon: Int,
            hasMore: Boolean,
            photosList: List<UnsplashPhotos>
        ): GeneralTypeModel {
            return GeneralTypeModel(photosList).apply {
                title = sectionTitle
                isHasMore = hasMore
                this.icon = icon
                groupType = GROUP_SECTION_TYPE
                groupLayout = GROUP_SECTION_LAYOUT
                childType = GENERAL_TYPE
                childLayout = GENERAL_LAYOUT
            }
        }

        fun getRecommendedUserSectionTypeObject(
            sectionTitle: String,
            @DrawableRes icon: Int,
            hasMore: Boolean,
            usersList: List<User>
        ): UserTypeModel {
            return UserTypeModel(usersList).apply {
                title = sectionTitle
                isHasMore = hasMore
                this.icon = icon
                groupType = GROUP_SECTION_TYPE
                groupLayout = GROUP_SECTION_LAYOUT
                childType = USER_TYPE
                childLayout = USER_LAYOUT
            }
        }

        fun getHorizontalSquarePhotosTypeObject(
            title: String,
            desc: String,
            photosList: List<UnsplashPhotos>
        ): HorizontalSquarePhotosTypeModel {
            val favouriteModel = HorizontalSquarePhotosTypeModel(photosList)
            favouriteModel.title = title
            favouriteModel.desc = desc
            favouriteModel.isHasMore = false
            favouriteModel.groupType = GROUP_CATEGORY_TYPE
            favouriteModel.childType = FAV_PHOTOGRAPHER_PHOTOS_TYPE
            favouriteModel.childLayout = FAV_PHOTOGRAPHER_PHOTOS_LAYOUT
            favouriteModel.scrollDirection = LinearLayoutManager.VERTICAL
            return favouriteModel
        }

    }
}