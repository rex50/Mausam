package com.rex50.mausam.views.fragments.discover

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseAndroidViewModel
import com.rex50.mausam.enums.ContentAnimationState
import com.rex50.mausam.model_classes.item_types.CategoryTypeModel
import com.rex50.mausam.model_classes.item_types.ColorTypeModel
import com.rex50.mausam.model_classes.utils.AllContentModel
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.network.Result
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.AnimatedMessage.AnimationByState
import com.rex50.mausam.utils.Constants
import com.rex50.mausam.utils.Constants.Util.userFavConstants
import com.rex50.mausam.utils.ImageActionHelper.DownloadStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils

class FragDiscoverViewModel(
    application: Application,
    val unsplashHelper: UnsplashHelper
) : BaseAndroidViewModel(application) {

    private val sequenceOfLayouts by lazy {
        listOf(
            Constants.AvailableLayouts.POPULAR_TAGS,
            Constants.AvailableLayouts.BROWSE_BY_CATEGORIES,
            Constants.AvailableLayouts.FEATURED_COLLECTIONS,
            Constants.AvailableLayouts.POPULAR_PHOTOS,
            Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS,
            Constants.AvailableLayouts.BROWSE_BY_COLORS,
            Constants.AvailableLayouts.LOCATION_BASED_PHOTOS,
            Constants.AvailableLayouts.WEATHER_BASED_PHOTOS,
            Constants.AvailableLayouts.TIME_BASED_PHOTOS
        )
    }

    private val allData: AllContentModel by lazy {
        AllContentModel().also {
            it.setSequenceOfLayouts(sequenceOfLayouts)
        }
    }

    val animations: ArrayList<AnimationByState<ContentAnimationState>> by lazy {
        val app = getApplication<Application>()
        arrayListOf(
            AnimationByState(
                ContentAnimationState.NO_INTERNET,
                R.raw.l_anim_error_no_internet,
                app.getString(R.string.error_no_internet),
                app.getString(R.string.retry)
            ),
            AnimationByState(
                ContentAnimationState.EMPTY,
                R.raw.l_anim_error_astronaout,
                app.getString(R.string.msg_empty_discover, userFavConstants.random()),
                app.getString(R.string.action_start_search)
            )
        )
    }

    init {
        viewModelScope.launch {
            prepareContents()
        }
    }

    fun getSequenceOfLayout() = sequenceOfLayouts

    fun isAllContentLoaded() = allData.allContentLoaded

    fun getLiveDownloadStatus(): LiveData<DownloadStatus> = imageDownloadStatus

    fun getContentLoadingState() = allData.contentLoadingState

    suspend fun prepareContents() = withContext(Dispatchers.IO) {

        allData.clearList()
        allData.setSequenceOfLayouts(sequenceOfLayouts)

        if(connectionChecker.isNetworkConnected()) {

            if (sequenceOfLayouts.contains(Constants.AvailableLayouts.WEATHER_BASED_PHOTOS))
                addWeatherBasedPhotos()

            if (sequenceOfLayouts.contains(Constants.AvailableLayouts.LOCATION_BASED_PHOTOS))
                addLocationBasedPhotos()

            if (sequenceOfLayouts.contains(Constants.AvailableLayouts.TIME_BASED_PHOTOS))
                addTimeBasedPhotos()

            if (sequenceOfLayouts.contains(Constants.AvailableLayouts.POPULAR_PHOTOS)
                || sequenceOfLayouts.contains(Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS))
                addPopularPhotosAndPhotographers()

            if (sequenceOfLayouts.contains(Constants.AvailableLayouts.FEATURED_COLLECTIONS)
                || sequenceOfLayouts.contains(Constants.AvailableLayouts.POPULAR_TAGS))
                addFeaturedCollectionsAndTags()

            if (sequenceOfLayouts.contains(Constants.AvailableLayouts.BROWSE_BY_CATEGORIES))
                addCategories()

            if (sequenceOfLayouts.contains(Constants.AvailableLayouts.BROWSE_BY_COLORS))
                addColors()

            if (sequenceOfLayouts.contains(Constants.AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES))
                addFavouritePhotographerPhotos()

        } else {

            delay(500)

            allData.setOnNoInternet()
        }

    }

    private suspend fun addFavouritePhotographerPhotos() {
        //TODO: create a array of photographer's list and pick randomly one photographer
        val user = "rpnickson"

        when (val result = unsplashHelper.getUserPhotos(user, 1, 20)) {
            is Result.Success -> {
                allData.addSequentially(
                    Constants.AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES,
                    GenericModelFactory.getHorizontalSquarePhotosTypeObject(
                        Constants.AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES,
                        Constants.Providers.POWERED_BY_UNSPLASH,
                        result.data
                    )
                )
            }

            is Result.Failure -> {
                allData.increaseResponseCount()
            }
        }
    }

    private fun addColors() {
        viewModelScope.launch {
            val array = getApplication<Application>().resources.getStringArray(R.array.array_colors_type)
            val colorsList = array.toList()
            allData.addSequentially(
                Constants.AvailableLayouts.BROWSE_BY_COLORS,
                GenericModelFactory.getColorTypeObject(
                    Constants.AvailableLayouts.BROWSE_BY_COLORS,
                    Constants.Providers.POWERED_BY_UNSPLASH,
                    false,
                    ColorTypeModel.createModelFromStringList(colorsList),
                    true
                )
            )
        }
    }

    private fun addCategories() {
        viewModelScope.launch {
            val array = getApplication<Application>().resources.getStringArray(R.array.array_categories_type)
            val categories = array.toList()
            allData.addSequentially(
                Constants.AvailableLayouts.BROWSE_BY_CATEGORIES,
                GenericModelFactory.getCategoryTypeObject(
                    Constants.AvailableLayouts.BROWSE_BY_CATEGORIES,
                    Constants.Providers.POWERED_BY_UNSPLASH,
                    false,
                    CategoryTypeModel.createModelFromStringList(categories),
                    true
                )
            )
        }
    }

    private suspend fun addFeaturedCollectionsAndTags() {

        when(val result = unsplashHelper.getCollectionsAndTags(1, 10)) {
            is Result.Success -> {
                allData.addSequentially(
                    Constants.AvailableLayouts.FEATURED_COLLECTIONS,
                    GenericModelFactory.getCollectionTypeObject(
                        Constants.AvailableLayouts.FEATURED_COLLECTIONS,
                        Constants.Providers.POWERED_BY_UNSPLASH,
                        true,
                        result.data.collectionsList
                    )
                )
                if (sequenceOfLayouts.contains(Constants.AvailableLayouts.POPULAR_TAGS)) {
                    allData.addSequentially(
                        Constants.AvailableLayouts.POPULAR_TAGS,
                        GenericModelFactory.getTagTypeObject(
                            Constants.AvailableLayouts.POPULAR_TAGS,
                            Constants.Providers.POWERED_BY_UNSPLASH,
                            false,
                            result.data.tagsList,
                            true
                        )
                    )
                }
            }

            is Result.Failure -> {
                Log.e(TAG, "onFailed: ", result.exception)
                allData.apply {
                    increaseResponseCount()
                    if (sequenceOfLayouts.contains(Constants.AvailableLayouts.POPULAR_TAGS)) {
                        increaseResponseCount()
                    }
                }
            }
        }
    }

    private suspend fun addPopularPhotosAndPhotographers() {
        when(val result = unsplashHelper.getPhotosAndUsers(UnsplashHelper.ORDER_BY_POPULAR)) {
            is Result.Success -> {
                allData.addSequentially(
                    Constants.AvailableLayouts.POPULAR_PHOTOS,
                    GenericModelFactory.getGeneralTypeObject(
                        Constants.AvailableLayouts.POPULAR_PHOTOS,
                        Constants.Providers.POWERED_BY_UNSPLASH,
                        true,
                        result.data.photosList
                    )
                )

                if (sequenceOfLayouts.contains(Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS)) {
                    allData.addSequentially(
                        Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS,
                        GenericModelFactory.getUserTypeObject(
                            Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS,
                            Constants.Providers.POWERED_BY_UNSPLASH,
                            false,
                            result.data.userList
                        )
                    )
                }
            }

            is Result.Failure -> {
                Log.e(TAG, "addPopularPhotosAndPhotographers: ", result.exception)
                allData.apply {
                    increaseResponseCount()
                    if (sequenceOfLayouts.contains(Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS)) {
                        increaseResponseCount()
                    }
                }
            }
        }
    }

    private suspend fun addTimeBasedPhotos() {
        //TODO: based on current time use appropriate word
        val time = "Night"
        when(val result = unsplashHelper.getSearchedPhotos(time, 1, 20)) {
            is Result.Success -> {
                allData.addSequentially(
                    Constants.AvailableLayouts.TIME_BASED_PHOTOS,
                    GenericModelFactory.getGeneralTypeObject(
                        Constants.AvailableLayouts.TIME_BASED_PHOTOS,
                        Constants.Providers.POWERED_BY_UNSPLASH,
                        false,
                        result.data.results
                    )
                )
            }

            is Result.Failure -> {
                Log.e(TAG, "addTimeBasedPhotos: ", result.exception)
                allData.increaseResponseCount()
            }
        }
    }

    private suspend fun addLocationBasedPhotos() {
        //TODO: Ask user to enter a location and based on that show images.
        val places = arrayListOf("Gujarat", "Orissa", "Bengaluru", "Hyderabad", "Kolkata",
            "Hong Kong", "London", "Malaysia", "Punjab", "Mumbai", "Chennai", "Paris", "USA", "Sri Lanka",
            "Russia", "Pakistan", "Bangladesh", "Tibet", "Berlin", "Bhutan", "Africa", "Australia", "England")

        val place = StringUtils.capitalize(places.random())

        when(val result = unsplashHelper.getSearchedPhotos(place, 1, 20)) {
            is Result.Success -> {
                val photos = result.data
                if (!photos.results.isNullOrEmpty()) {
                    allData.addSequentially(
                        Constants.AvailableLayouts.LOCATION_BASED_PHOTOS,
                        GenericModelFactory.getGeneralTypeObject(
                            Constants.AvailableLayouts.LOCATION_BASED_PHOTOS,
                            place,
                            false,
                            photos.results
                        )
                    )
                } else
                    allData.increaseResponseCount()
            }

            is Result.Failure -> {
                Log.e(TAG, "addLocationBasedPhotos: ", result.exception)
                allData.increaseResponseCount()
            }
        }
    }

    private suspend fun addWeatherBasedPhotos() {
        //TODO: based on current month pick one weather and use it.
        val seasons = arrayListOf("late winter", "spring", "monsoon", "autumn", "summer", "early winter")

        val season = StringUtils.capitalize(seasons.random())

        when(val result = unsplashHelper.getSearchedPhotos(season, 1, 20)) {
            is Result.Success -> {
                allData.addSequentially(
                    Constants.AvailableLayouts.WEATHER_BASED_PHOTOS,
                    GenericModelFactory.getGeneralTypeObject(
                        Constants.AvailableLayouts.WEATHER_BASED_PHOTOS,
                        season,
                        false,
                        result.data.results
                    )
                )
            }

            is Result.Failure -> {
                Log.e(TAG, "addWeatherBasedPhotos: ", result.exception)
                allData.increaseResponseCount()
            }
        }
    }

    fun reloadData() {
        viewModelScope.launch {
            prepareContents()
        }
    }

    companion object {
        const val TAG = "FragDiscoverViewModel"
    }

}