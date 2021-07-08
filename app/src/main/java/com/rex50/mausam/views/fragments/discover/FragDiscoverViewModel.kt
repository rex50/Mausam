package com.rex50.mausam.views.fragments.discover

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseAndroidViewModel
import com.rex50.mausam.enums.ContentAnimationState
import com.rex50.mausam.interfaces.GetUnsplashCollectionsAndTagsListener
import com.rex50.mausam.interfaces.GetUnsplashPhotosAndUsersListener
import com.rex50.mausam.interfaces.GetUnsplashPhotosListener
import com.rex50.mausam.interfaces.GetUnsplashSearchedPhotosListener
import com.rex50.mausam.model_classes.item_types.CategoryTypeModel
import com.rex50.mausam.model_classes.item_types.ColorTypeModel
import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.unsplash.collection.Tag
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.unsplash.searched_photos.SearchedPhotos
import com.rex50.mausam.model_classes.utils.AllContentModel
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.AnimatedMessage.AnimationByState
import com.rex50.mausam.utils.ConnectionChecker
import com.rex50.mausam.utils.Constants
import com.rex50.mausam.utils.ImageActionHelper
import com.rex50.mausam.utils.ImageActionHelper.DownloadStatus
import com.rex50.mausam.views.fragments.home.FragHome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils
import org.json.JSONArray

class FragDiscoverViewModel(
    application: Application,
    val unsplashHelper: UnsplashHelper,
    val connectionChecker: ConnectionChecker
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

    private val imageDownloadStatus: MutableLiveData<DownloadStatus> by lazy {
        MutableLiveData<DownloadStatus>()
    }

    var userFavConstants = arrayListOf(
        "Cat",
        "Dog",
        "Car",
        "Cartoon",
        "Anime",
        "Motorcycle",
        "Animal",
        "Bird",
        "Gadget",
    )

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

    fun getLiveDownloadStatus() = imageDownloadStatus

    fun getContentLoadingState() = allData.contentLoadingState

    suspend fun prepareContents() = withContext(Dispatchers.IO) {

        allData.clearList()

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

    private fun addFavouritePhotographerPhotos() {
        //TODO: create a array of photographer's list and pick randomly one photographer
        val user = "rpnickson"
        unsplashHelper.getUserPhotos(
            user,
            1,
            20,
            object : GetUnsplashPhotosListener {
                override fun onSuccess(photos: List<UnsplashPhotos>) {
                    viewModelScope.launch {
                        allData.addSequentially(
                            Constants.AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES,
                            GenericModelFactory.getFavouritePhotographerTypeObject(
                                Constants.AvailableLayouts.FAVOURITE_PHOTOGRAPHER_IMAGES,
                                Constants.Providers.POWERED_BY_UNSPLASH,
                                photos
                            )
                        )
                    }
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(FragHome.TAG, "onFailed: $errors")
                    allData.increaseResponseCount()
                }
            }
        )
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

    private fun addFeaturedCollectionsAndTags() {
        unsplashHelper.getCollectionsAndTags(
            1,
            10,
            object :
                GetUnsplashCollectionsAndTagsListener {
                override fun onSuccess(collection: List<Collections>, tagsList: List<Tag>) {
                    viewModelScope.launch {
                        allData.addSequentially(
                            Constants.AvailableLayouts.FEATURED_COLLECTIONS,
                            GenericModelFactory.getCollectionTypeObject(
                                Constants.AvailableLayouts.FEATURED_COLLECTIONS,
                                Constants.Providers.POWERED_BY_UNSPLASH,
                                true,
                                collection
                            )
                        )
                        if (sequenceOfLayouts.contains(Constants.AvailableLayouts.POPULAR_TAGS)) {
                            allData.addSequentially(
                                Constants.AvailableLayouts.POPULAR_TAGS,
                                GenericModelFactory.getTagTypeObject(
                                    Constants.AvailableLayouts.POPULAR_TAGS,
                                    Constants.Providers.POWERED_BY_UNSPLASH,
                                    false,
                                    tagsList,
                                    true
                                )
                            )
                        }
                    }
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(FragHome.TAG, "onFailed: $errors")
                    allData.apply {
                        increaseResponseCount()
                        if (sequenceOfLayouts.contains(Constants.AvailableLayouts.POPULAR_TAGS)) {
                            increaseResponseCount()
                        }
                    }
                }
            }
        )
    }

    private fun addPopularPhotosAndPhotographers() {
        unsplashHelper.getPhotosAndUsers(
            UnsplashHelper.ORDER_BY_POPULAR,
            object : GetUnsplashPhotosAndUsersListener {
                override fun onSuccess(photos: List<UnsplashPhotos>, userList: List<User>) {
                    viewModelScope.launch {
                        allData.addSequentially(
                            Constants.AvailableLayouts.POPULAR_PHOTOS,
                            GenericModelFactory.getGeneralTypeObject(
                                Constants.AvailableLayouts.POPULAR_PHOTOS,
                                Constants.Providers.POWERED_BY_UNSPLASH,
                                true,
                                photos
                            )
                        )

                        if (sequenceOfLayouts.contains(Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS)) {
                            allData.addSequentially(
                                Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS,
                                GenericModelFactory.getUserTypeObject(
                                    Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS,
                                    Constants.Providers.POWERED_BY_UNSPLASH,
                                    false,
                                    userList
                                )
                            )
                        }
                    }
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(FragHome.TAG, "onFailed: $errors")
                    allData.apply {
                        increaseResponseCount()
                        if (sequenceOfLayouts.contains(Constants.AvailableLayouts.POPULAR_PHOTOGRAPHERS)) {
                            increaseResponseCount()
                        }
                    }
                }
            }
        )
    }

    private fun addTimeBasedPhotos() {
        //TODO: based on current time use appropriate word
        val time = "Night"
        unsplashHelper.getSearchedPhotos(
            time,
            1,
            20,
            object : GetUnsplashSearchedPhotosListener {
                override fun onSuccess(photos: SearchedPhotos) {
                    viewModelScope.launch {
                        allData.addSequentially(
                            Constants.AvailableLayouts.TIME_BASED_PHOTOS,
                            GenericModelFactory.getGeneralTypeObject(
                                Constants.AvailableLayouts.TIME_BASED_PHOTOS,
                                Constants.Providers.POWERED_BY_UNSPLASH,
                                false,
                                photos.results
                            )
                        )
                    }
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(FragHome.TAG, "onFailed: $errors")
                    allData.increaseResponseCount()
                }
            }
        )
    }

    private fun addLocationBasedPhotos() {
        //TODO: Ask user to enter a location and based on that show images.
        val places = arrayListOf("Gujarat", "Orissa", "Bengaluru", "Hyderabad", "Kolkata",
            "Hong Kong", "London", "Malaysia", "Punjab", "Mumbai", "Chennai", "Paris", "USA", "Sri Lanka",
            "Russia", "Pakistan", "Bangladesh", "Tibet", "Berlin", "Bhutan", "Africa", "Australia", "England")
        val place = StringUtils.capitalize(places.random())
        unsplashHelper.getSearchedPhotos(
            place,
            1,
            20,
            object : GetUnsplashSearchedPhotosListener {
                override fun onSuccess(photos: SearchedPhotos) {
                    if (!photos.results.isNullOrEmpty()) {
                        viewModelScope.launch {
                            allData.addSequentially(
                                Constants.AvailableLayouts.LOCATION_BASED_PHOTOS,
                                GenericModelFactory.getGeneralTypeObject(
                                    Constants.AvailableLayouts.LOCATION_BASED_PHOTOS, /*Constants.Providers.POWERED_BY_UNSPLASH*/
                                    place,
                                    false,
                                    photos.results
                                )
                            )
                        }
                    } else
                        allData.increaseResponseCount()
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(FragHome.TAG, "onFailed: $errors")
                    allData.increaseResponseCount()
                }
            }
        )
    }

    private fun addWeatherBasedPhotos() {
        //TODO: based on current month pick one weather and use it.
        val seasons = arrayListOf("late winter", "spring", "monsoon", "autumn", "summer", "early winter")
        val season = StringUtils.capitalize(seasons.random())
        unsplashHelper.getSearchedPhotos(
            season,
            1,
            20,
            object :
                GetUnsplashSearchedPhotosListener {
                override fun onSuccess(photos: SearchedPhotos) {
                    viewModelScope.launch {
                        allData.addSequentially(
                            Constants.AvailableLayouts.WEATHER_BASED_PHOTOS,
                            GenericModelFactory.getGeneralTypeObject(
                                Constants.AvailableLayouts.WEATHER_BASED_PHOTOS, /*Constants.Providers.POWERED_BY_UNSPLASH*/
                                season,
                                false,
                                photos.results
                            )
                        )
                    }
                }

                override fun onFailed(errors: JSONArray) {
                    Log.e(FragHome.TAG, "onFailed: $errors")
                    allData.increaseResponseCount()
                }
            }
        )
    }

    /*fun setImageAsWallpaper(photoInfo: UnsplashPhotos, onDownloadSuccess: (UnsplashPhotos?) -> Unit) {

        var downloadObserver: Observer<ImageDownloadStatus>? = null

        fun removeObserver() {
            downloadObserver?.let { imageDownloadStatus.removeObserver(it) }
        }

        downloadObserver = Observer<ImageDownloadStatus> { status ->
            when (status) {
                ImageDownloadStatus.SUCCESS -> {
                    onDownloadSuccess(status.downloadedData)
                    removeObserver()
                }
                ImageDownloadStatus.ERROR -> {
                    removeObserver()
                }
                else -> {
                    //Do nothing
                }
            }
        }

        imageDownloadStatus.observeForever(downloadObserver)

        //downloadImage(photoInfo)

        ImageActionHelper.saveImage(this, photoInfo, false, object : ImageActionHelper.ImageSaveListener{
            override fun onDownloadStarted() {
                //bsDownload?.downloadStarted(childFragmentManager)
            }

            override fun onDownloadFailed() {
                //bsDownload?.downloadError()
            }

            override fun onDownloadProgress(progress: Int) {
                //bsDownload?.onProgress(progress)
            }

            override fun response(imageMeta: UnsplashPhotos?, msg: String) {
                //bsDownload?.downloaded()
                startActivity(Intent(context, ActImageEditor::class.java).also {
                    it.putExtra(Constants.IntentConstants.PHOTO_DATA, imageMeta)
                }
            }
        })
    }*/

    fun favouriteImage(photoInfo: UnsplashPhotos, onSuccess: ((UnsplashPhotos?, String) -> Unit)? = null) {
        saveImage(photoInfo, true, onSuccess)
    }

    fun downloadImage(photoInfo: UnsplashPhotos, onDownloadSuccess: ((UnsplashPhotos?, String) -> Unit)? = null) {
        saveImage(photoInfo, false, onDownloadSuccess)
    }

    private fun saveImage(
        photoInfo: UnsplashPhotos,
        forFav: Boolean,
        onSuccess: ((UnsplashPhotos?, String) -> Unit)?
    ) {
        ImageActionHelper.saveImage(getApplication(), photoInfo, forFav, object : ImageActionHelper.ImageSaveListener{
            override fun onDownloadStarted() {
                imageDownloadStatus.postValue (
                    DownloadStatus.Started(0)
                )
            }

            override fun onDownloadFailed(msg: String) {
                imageDownloadStatus.postValue(
                    DownloadStatus.Error(msg)
                )
            }

            override fun onDownloadProgress(progress: Int) {
                imageDownloadStatus.postValue(
                    DownloadStatus.Downloading(progress)
                )
            }

            override fun response(imageMeta: UnsplashPhotos?, msg: String) {
                imageDownloadStatus.postValue(
                    DownloadStatus.Success(imageMeta)
                )
                onSuccess?.invoke(imageMeta, msg)
            }
        })
    }

    fun reloadData() {
        viewModelScope.launch {
            prepareContents()
        }
    }

}