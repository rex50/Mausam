package com.rex50.mausam.views.fragments.discover

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.rex50.mausam.R
import com.rex50.mausam.interfaces.*
import com.rex50.mausam.model_classes.item_types.*
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.utils.*
import com.rex50.mausam.utils.*
import com.rex50.mausam.MausamApplication
import com.rex50.mausam.base_classes.BaseFragmentWithListener
import com.rex50.mausam.databinding.FragDiscoverBinding
import com.rex50.mausam.enums.ContentAnimationState
import com.rex50.mausam.enums.ContentLoadingState
import com.rex50.mausam.utils.ImageActionHelper.DownloadStatus
import com.rex50.mausam.utils.ImageViewerHelper.Tools
import com.rex50.mausam.views.activities.ActImageEditor
import com.rex50.mausam.views.adapters.AdaptHome
import com.rex50.mausam.views.bottomsheets.BSDownload
import kotlinx.android.synthetic.main.anim_view.*
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.RuntimeException
import java.util.*

class FragDiscover : BaseFragmentWithListener<FragDiscoverBinding, FragDiscover.OnFragmentInteractionListener>() {

    private var adaptHome: AdaptHome? = null

    private val viewModel by viewModel<FragDiscoverViewModel>()

    private val bsDownload: BSDownload? by lazy {
        BSDownload().also { it.isCancelable = false }
    }

    private val animatedMessage: AnimatedMessage<ContentAnimationState> by lazy {
        AnimatedMessage(
            binding?.animLayout,
            viewModel.animations
        )
    }

    private var isNotInternetAvailable = false

    override fun bindView(inflater: LayoutInflater, container: ViewGroup?): FragDiscoverBinding {
        return FragDiscoverBinding.inflate(inflater,container,false)
    }


    override fun initView() {

        mContext?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        initHeader()

        initSearchBox()

        initAdapter()

        initItemClicks()


    }

    override fun load() {

        initAnimation()

        setupContentLoadingObserver()

        setupDownloadObserver()

    }

    private fun initAnimation() {
        animatedMessage.cacheAnimations()

        animatedMessage.onActionBtnClicked = { _, state ->
            when(state) {

                ContentAnimationState.EMPTY -> {
                    setFocusToSearchBox(true)
                }

                ContentAnimationState.NO_INTERNET -> {
                    viewModel.reloadData()
                }

                else -> {
                    Log.e(TAG, "initAnimation: ", RuntimeException("Action click not handled"))
                }

            }
        }

        animatedMessage.onLottieAnimationConfig = { lottieAnimationView, state ->
            when(state) {
                ContentAnimationState.EMPTY -> {
                    lottieAnimationView.scale = 0.2f
                    lottieAnimationView.repeatCount = LottieDrawable.INFINITE
                }
                else -> {
                    lottieAnimationView.scale = 1f
                    lottieAnimationView.repeatCount = 0
                }
            }
        }
    }

    private fun initHeader() {
        binding?.headerLayout?.apply {
            gradientLine.background = GradientHelper.getInstance(mContext)?.getRandomLeftRightGradient()

            tvPageTitle.setText(R.string.search_photo_title)
            tvPageDesc.setText(R.string.search_photo_desc)
        }

        binding?.etvSearch?.setHint(R.string.search_box_hint)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSearchBox() {
        if (resources.getBoolean(R.bool.hide_search_providers)) {
            binding?.containerSearchProviders?.hideView()
        }

        binding?.etvSearch?.apply {

            //Handle click event of search icon (inside edittext)
            setOnTouchListener(OnTouchListener { _: View?, event: MotionEvent ->
                /*DRAWABLE_LEFT = 0
                DRAWABLE_TOP = 1
                DRAWABLE_RIGHT = 2
                DRAWABLE_BOTTOM = 3*/
                val drawableRight = 2
                if (event.action == MotionEvent.ACTION_UP) {
                    binding?.etvSearch?.compoundDrawables?.get(drawableRight)?.bounds?.width()?.apply {
                        if (event.rawX >= binding?.etvSearch?.right!! - this) {
                            onSearchClickAction()
                            return@OnTouchListener true
                        }
                    }
                }
                false
            })

            //Handle click event of search button (inside keyboard)
            setOnEditorActionListener(OnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchClickAction()
                    return@OnEditorActionListener true
                }
                false
            })

            //Hide keyboard when focus is changed of edittext
            setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus) {
                    showKeyBoard()
                } else
                    hideKeyboard()
            }
        }
    }

    private fun setupContentLoadingObserver() {
        viewModel.getContentLoadingState().observe(requireActivity(), { state ->
            isNotInternetAvailable = false
            when(state) {
                is ContentLoadingState.Preparing -> {
                    animatedMessage.hide()
                    binding?.lvCenter?.root?.showView()
                    binding?.recDiscoverContent?.hideView()
                }

                is ContentLoadingState.Ready<AllContentModel> -> {
                    fragScope.launch {
                        animatedMessage.hide()
                        delay(300)
                        binding?.recDiscoverContent?.showView()
                        adaptHome?.updateData(state.data)
                        binding?.lvCenter?.root?.hideView()
                    }
                }

                is ContentLoadingState.Empty -> {
                    binding?.lvCenter?.root?.hideView()
                    binding?.recDiscoverContent?.hideView()
                    animatedMessage.setAnimationAndShow(ContentAnimationState.EMPTY)
                }

                is ContentLoadingState.NoInternet -> {
                    isNotInternetAvailable = true
                    binding?.lvCenter?.root?.hideView()
                    binding?.recDiscoverContent?.hideView()
                    animatedMessage.setAnimationAndShow(ContentAnimationState.NO_INTERNET)
                }
            }
        })
    }

    private fun setupDownloadObserver() {
        viewModel.getLiveDownloadStatus().observe(requireActivity(), { state ->
            when(state) {
                is DownloadStatus.Started -> {
                    bsDownload?.downloadStarted(childFragmentManager)
                }

                is DownloadStatus.Downloading -> {
                    bsDownload?.onProgress(state.progress)
                }

                is DownloadStatus.Success -> {
                    bsDownload?.downloaded()
                }

                is DownloadStatus.Error ->  {
                    bsDownload?.downloadError()
                }
            }
        })
    }

    private fun initAdapter() {
        adaptHome = AdaptHome(GradientHelper.getInstance(requireContext()), AllContentModel())
        binding?.recDiscoverContent?.apply {
            layoutManager = LinearLayoutManager(mContext)
            setHasFixedSize(true)
            adapter = adaptHome
        }
    }

    private fun initItemClicks() {

        adaptHome?.itemClickListener = object : OnGroupItemClickListener {

            override fun onItemClick(o: Any?, childImgView: ImageView?, groupPos: Int, childPos: Int) {

                //Cast the object
                object : GenericModelCastHelper(o) {

                    override fun onCollectionType(collectionTypeModel: CollectionTypeModel) {
                        listener?.startMorePhotosActivity(collectionTypeModel.collections[childPos].moreListData)
                    }

                    override fun onGeneralType(generalTypeModel: GeneralTypeModel) {
                        showImageViewer(generalTypeModel.photosList, childImgView, childPos)
                    }

                    override fun onHorizontalSquarePhotosTypeModel(horizontalSquarePhotosTypeModel: HorizontalSquarePhotosTypeModel) {
                        showImageViewer(horizontalSquarePhotosTypeModel.photosList, childImgView, childPos)
                    }

                    override fun onTagType(tagTypeModel: TagTypeModel) {
                        listener?.startMorePhotosActivity(tagTypeModel.tagsList[childPos].moreListData)
                    }

                    override fun onColorType(colorTypeModel: ColorTypeModel) {
                        listener?.startMorePhotosActivity(colorTypeModel.colorsList[childPos].getMoreListData())
                    }

                    override fun onCategoryType(categoryTypeModel: CategoryTypeModel) {
                        listener?.startMorePhotosActivity(categoryTypeModel.categories[childPos].getMoreListData())
                    }

                    override fun onUserType(userTypeModel: UserTypeModel) {
                        listener?.startMorePhotosActivity(userTypeModel.usersList[childPos].moreListData)
                    }
                }

            }

            override fun onMoreClicked(o: Any?, title: String?, groupPos: Int) {

                //Cast the object
                object : GenericModelCastHelper(o){

                    override fun onCollectionType(collectionTypeModel: CollectionTypeModel) {
                        listener?.startMoreFeaturedCollections(collectionTypeModel.getMoreListData())
                    }

                    override fun onGeneralType(generalTypeModel: GeneralTypeModel) {
                        listener?.startMorePhotosActivity(generalTypeModel.getMoreListData())
                    }

                }

            }
        }
    }

    private fun showImageViewer(photosList: List<UnsplashPhotos>, childImgView: ImageView?, childPos: Int) {
        ImageViewerHelper(mContext).with(photosList,
            childImgView, childPos, object : ImageActionHelper.ImageActionListener() {

                override fun onSetWallpaper(photoInfo: UnsplashPhotos, name: String) {
                    viewModel.downloadImage(photoInfo) { photoData, _ ->
                        startActivity(Intent(context, ActImageEditor::class.java).also {
                            it.putExtra(Constants.IntentConstants.PHOTO_DATA, photoData)
                        })
                    }
                }

                override fun onDownload(photoInfo: UnsplashPhotos, name: String) {
                    viewModel.downloadImage(photoInfo) { _, msg ->
                        if(msg.isNotEmpty()){
                            showToast(msg)
                        }
                    }
                }

                override fun onFavourite(photoInfo: UnsplashPhotos, name: String) {
                    viewModel.favouriteImage(photoInfo) { _, msg ->
                        if(msg.isNotEmpty())
                            showToast(msg)
                    }
                }

                override fun onShare(photoInfo: UnsplashPhotos, name: String) {
                    ImageActionHelper.shareImage(mContext, "Share", photoInfo.user.name, photoInfo.links.html)
                }

                override fun onUserPhotos(user: User) {
                    listener?.startMorePhotosActivity(user.moreListData)
                }
            })
            .setDataSaverMode(
                MausamApplication.getInstance()?.getSharedPrefs()?.isDataSaverMode ?: false
            )
            .setTools(arrayListOf(Tools.SET_WALLPAPER, Tools.DOWNLOAD_PHOTO, Tools.FAV_PHOTO, Tools.SHARE_PHOTO, Tools.MORE))
            .show()
    }

    private fun onSearchClickAction() {
        searchPhotos()
    }

    private fun searchPhotos() {
        val searchTerm = binding?.etvSearch?.text.toString().trim().replace(" +".toRegex(), " ")
        binding?.etvSearch?.setText(searchTerm)
        listener?.startMorePhotosActivity(
                MoreListData(
                        Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
                        generalInfo = MoreData(
                                searchTerm,
                                Constants.Providers.POWERED_BY_UNSPLASH
                        )
                )
        )
        hideKeyboard()
    }

    fun setFocusToSearchBox(state: Boolean){
        binding?.apply {
            if(state)
                etvSearch.requestFocus()
            else
                etvSearch.clearFocus()
        }
    }

    override fun whenResumed() {
        if(isNotInternetAvailable) {
            viewModel.reloadData()
        }
    }

    override fun onScrollToTop() {
        binding?.apply {
            nsDiscover.smoothScrollTo(0, 0)
            ablDiscover.setExpanded(true, true)
        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri?)
        fun nextBtnClicked()
        fun startMorePhotosActivity(data: MoreListData)
        fun startMoreFeaturedCollections(moreListData: MoreListData)
        val snackBar: MaterialSnackBar?
    }

    companion object {

        private const val TAG = "FragDiscover"

        @JvmStatic
        fun newInstance(): FragDiscover {
            return FragDiscover()
        }
    }

}