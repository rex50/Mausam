package com.rex50.mausam.views.fragments.discover

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.rex50.mausam.utils.animations.AnimatedMessage
import com.rex50.mausam.utils.animations.configure2PlanetAnim
import com.rex50.mausam.utils.animations.configureAstronautAnim
import com.rex50.mausam.utils.animations.defaultConfig
import com.rex50.mausam.views.activities.ActImageEditor
import com.rex50.mausam.views.adapters.AdaptHome
import com.rex50.mausam.views.bottomsheets.BSDownload
import com.rex50.mausam.views.bottomsheets.BSDownloadQuality
import kotlinx.android.synthetic.main.anim_view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.RuntimeException
import java.util.*

class FragDiscover : BaseFragmentWithListener<FragDiscoverBinding, FragDiscover.OnFragmentInteractionListener>() {

    private val viewModel by viewModel<FragDiscoverViewModel>()

    private val gradientHelper: GradientHelper by inject()

    private val adaptHome: AdaptHome by lazy {
        AdaptHome(gradientHelper, AllContentModel())
    }

    private val bsDownload: BSDownload? by lazy {
        BSDownload(childFragmentManager).also { it.isCancelable = false }
    }

    private val animatedMessage: AnimatedMessage<ContentAnimationState> by lazy {
        AnimatedMessage(
            binding?.animLayout,
            arrayListOf(
                AnimatedMessage.AnimationByState(
                    ContentAnimationState.NO_INTERNET,
                    R.raw.l_anim_error_no_internet,
                    getString(R.string.error_no_internet),
                    getString(R.string.retry)
                ),
                AnimatedMessage.AnimationByState(
                    ContentAnimationState.EMPTY,
                    R.raw.l_anim_error_astronaout,
                    getString(
                        R.string.msg_empty_discover,
                        Constants.Util.userFavConstants.random()
                    ),
                    getString(R.string.action_start_search)
                ),
                AnimatedMessage.AnimationByState(
                    ContentAnimationState.LOADING,
                    R.raw.l_anim_2_planet,
                    getString(R.string.discovering_new_contents),
                    "",
                    false
                )
            )
        )
    }

    private var imageViewer: ImageViewerHelper? = null

    private var isNotInternetAvailable = false

    override fun bindView(inflater: LayoutInflater, container: ViewGroup?): FragDiscoverBinding {
        return FragDiscoverBinding.inflate(inflater,container,false)
    }


    override fun initView() {

        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        initHeader()

        initSearchBox()

        initRecycler()

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
                    lottieAnimationView.configureAstronautAnim()
                }

                ContentAnimationState.LOADING -> {
                    lottieAnimationView.configure2PlanetAnim()
                }

                else -> {
                    lottieAnimationView.defaultConfig()
                }
            }
        }
    }

    private fun initHeader() {
        binding?.headerLayout?.apply {
            gradientLine.background = gradientHelper.getRandomLeftRightGradient()

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
                    animatedMessage.setAnimationAndShow(ContentAnimationState.LOADING)
                    binding?.lvCenter?.root?.hideView()
                    binding?.recDiscoverContent?.hideView()
                }

                is ContentLoadingState.Ready<AllContentModel> -> {
                    fragScope.launch {
                        delay(300)
                        animatedMessage.hide()
                        binding?.recDiscoverContent?.showView()
                        adaptHome.updateData(state.data)
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
                    bsDownload?.downloadStarted()
                }

                is DownloadStatus.Downloading -> {
                    bsDownload?.onProgress(state.progress)
                }

                is DownloadStatus.Success -> {
                    bsDownload?.downloaded()
                    imageViewer?.updateButtons()
                }

                is DownloadStatus.Error ->  {
                    bsDownload?.downloadError()
                }
            }
        })

        bsDownload?.onCancel = {
            viewModel.cancelDownloadImage({
                bsDownload?.downloadError(getString(R.string.download_cancelled))
            }, {
                bsDownload?.downloadError(getString(R.string.error_failed_cancelling_photo_download))
            })
        }
    }

    private fun initRecycler() {
        binding?.recDiscoverContent?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = adaptHome
        }
    }

    private fun initItemClicks() {

        adaptHome.itemClickListener = object : OnGroupItemClickListener {

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
        imageViewer?.dismiss()
        imageViewer = ImageViewerHelper(requireContext(), get()).with(photosList,
            childImgView, childPos, object : ImageActionHelper.ImageActionListener() {

                override fun onSetWallpaper(photoInfo: UnsplashPhotos, name: String) {
                    BSDownloadQuality.showQualitySheet(childFragmentManager, imageViewer?.isDownloaded.isTrue()) {
                        viewModel.downloadImage(photoInfo) { photoData, _ ->
                            startActivity(Intent(context, ActImageEditor::class.java).also {
                                it.putExtra(Constants.IntentConstants.PHOTO_DATA, photoData)
                            })
                        }
                    }
                }

                override fun onDownload(photoInfo: UnsplashPhotos, name: String) {
                    BSDownloadQuality.showQualitySheet(childFragmentManager) {
                        viewModel.downloadImage(photoInfo) { _, msg ->
                            if(msg.isNotEmpty()){
                                showToast(msg)
                            }
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
                    ImageActionHelper.shareImage(requireContext(), "Share Photo", photoInfo.user.name, photoInfo.links.html)
                }

                override fun onUserPhotos(user: User) {
                    listener?.startMorePhotosActivity(user.moreListData)
                }
            })
            .setDataSaverMode(MausamApplication.getInstance()?.getSharedPrefs()?.isDataSaverMode ?: false)

        imageViewer?.show()
    }

    private fun onSearchClickAction() {
        searchPhotos()
    }

    private fun searchPhotos() {
        val searchTerm = binding?.etvSearch?.text.toString().trim().replace(" +".toRegex(), " ")
        listener?.startMorePhotosActivity(
                MoreListData(
                        Constants.ListModes.LIST_MODE_GENERAL_PHOTOS,
                        generalInfo = MoreData(
                                searchTerm,
                                Constants.Providers.POWERED_BY_UNSPLASH
                        )
                )
        )
        Handler(Looper.getMainLooper()).postDelayed({
            binding?.etvSearch?.setText("")
        }, 300)
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
        fun snackBar(): MaterialSnackBar?
    }

    companion object {

        private const val TAG = "FragDiscover"

        @JvmStatic
        fun newInstance(): FragDiscover {
            return FragDiscover()
        }
    }

}