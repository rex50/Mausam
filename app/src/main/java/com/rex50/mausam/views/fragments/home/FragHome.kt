package com.rex50.mausam.views.fragments.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.MausamApplication
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseFragmentWithListener
import com.rex50.mausam.databinding.FragHomeBinding
import com.rex50.mausam.enums.ContentAnimationState
import com.rex50.mausam.interfaces.*
import com.rex50.mausam.model_classes.item_types.HorizontalSquarePhotosTypeModel
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.IntentConstants.PHOTO_DATA
import com.rex50.mausam.views.activities.ActImageEditor
import com.rex50.mausam.views.adapters.AdaptContent
import com.rex50.mausam.views.bottomsheets.BSDownload
import com.thekhaeng.pushdownanim.PushDownAnim
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.RuntimeException

class FragHome : BaseFragmentWithListener<FragHomeBinding, FragHome.OnFragmentInteractionListener>() {

    private val viewModel: FragHomeViewModel by viewModel()

    private val adapter: AdaptContent by lazy {
        AdaptContent(context, FragHomeViewModel.getEmptyData())
    }

    private val bsDownload: BSDownload? by lazy {
        BSDownload(childFragmentManager).also { it.isCancelable = false }
    }

    private var imageViewer: ImageViewerHelper? = null

    private val animatedMessage: AnimatedMessage<ContentAnimationState> by lazy {
        AnimatedMessage(
            binding?.animLayout,
            viewModel.animations
        )
    }

    private val connectionChecker: ConnectionChecker by inject()

    override fun bindView(inflater: LayoutInflater, container: ViewGroup?): FragHomeBinding {
        return FragHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {

        initHeader()

        initRecycler()

        initItemClicks()

    }

    override fun load() {

        initAnimation()

        setupContentObserver()

        setupStateObserver()

        setupDownloadObserver()

    }

    private fun initHeader() {
        PushDownAnim.setPushDownAnimTo(binding?.header?.btnSettings)
            .setOnClickListener { listener?.startSettings() }
    }

    private fun initAnimation() {

        animatedMessage.cacheAnimations()

        animatedMessage.onActionBtnClicked = { _, state ->
            when(state) {
                ContentAnimationState.EMPTY -> {
                    listener?.navigateToDiscover()
                }

                ContentAnimationState.NO_INTERNET -> {
                    viewModel.reload()
                }

                else -> {
                    Log.e(TAG, "initAnimation: ", RuntimeException("Action click not handled"))
                }
            }
        }

        animatedMessage.onLottieAnimationConfig = { lottieAnimationView, state ->
            when(state) {
                ContentAnimationState.EMPTY -> {
                    AnimConfigs.configureAstronautAnim(lottieAnimationView)
                }
                else -> {
                    AnimConfigs.defaultConfig(lottieAnimationView)
                }
            }
        }
    }

    private fun initRecycler() {

        val layoutManager = GridLayoutManager(context , 1,  LinearLayoutManager.VERTICAL, false)

        binding?.recHomeContent?.layoutManager = layoutManager

        //For proper spacing around the items
        if (binding?.recHomeContent?.itemDecorationCount ?: 0 > 0)
            binding?.recHomeContent?.addItemDecoration(ItemOffsetDecoration(context, R.dimen.recycler_item_offset_grid))

        //For Item animation while scrolling
        binding?.recHomeContent?.adapter = ScaleInAnimationAdapter(adapter).apply {
            setFirstOnly(false)
        }

        //For pagination (loading data when reached at the end of the list)
        binding?.recHomeContent?.apply {
            clearOnScrollListeners()
            addOnScrollListener(object: EndlessRecyclerOnScrollListener(layoutManager){
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    binding?.lvBottom?.root?.showView()
                    viewModel.getLatestPhotosOf(page)
                }
            }.also {
                it.setInitialPage(INITIAL_PAGE)
                it.setVisibleThreshold(PAGINATION_THRESHOLD)
            })
        }

    }

    private fun initItemClicks() {

        adapter.setChildClickListener(object : OnChildItemClickListener {
            override fun onItemClick(o: Any?, childImgView: ImageView?, childPos: Int) {

                object: GenericModelCastHelper(o){

                    override fun onHorizontalSquarePhotosTypeModel(horizontalSquarePhotosTypeModel: HorizontalSquarePhotosTypeModel) {
                        showImageViewer(horizontalSquarePhotosTypeModel.photosList, childImgView, childPos)
                    }

                }

            }
        })
    }

    private fun setupStateObserver() {
        viewModel.loadingState.observe(requireActivity(), { state ->

            binding?.lvCenter?.root?.hideView()
            binding?.lvBottom?.root?.hideView()
            animatedMessage.hide()

            when(state) {

                null -> {}

                ContentAnimationState.SUCCESS -> {
                    binding?.recHomeContent?.showView()
                }

                ContentAnimationState.LOADING -> {
                    if(adapter.itemCount == 0) {
                        binding?.lvCenter?.root?.showView()
                    } else {
                        binding?.lvBottom?.root?.showView()
                    }
                }

                ContentAnimationState.ERROR -> {
                    showErrorMsg(
                        if(connectionChecker.isNetworkConnected())
                            R.string.error_failed_getting_photos
                        else
                            R.string.error_no_internet
                    )
                }

                ContentAnimationState.EMPTY, ContentAnimationState.NO_INTERNET  -> {
                    binding?.recHomeContent?.hideView()
                    animatedMessage.setAnimationAndShow(state)
                }
            }
        })
    }

    private fun setupContentObserver() {
        viewModel.homeContent.observe(requireActivity(), {
            adapter.update(it)
            imageViewer?.updateImages(it.photosList)
        })
    }

    private fun setupDownloadObserver() {
        viewModel.getLiveDownloadStatus().observe(requireActivity(), { state ->
            when(state) {
                is ImageActionHelper.DownloadStatus.Started -> {
                    bsDownload?.downloadStarted()
                }

                is ImageActionHelper.DownloadStatus.Downloading -> {
                    bsDownload?.onProgress(state.progress)
                }

                is ImageActionHelper.DownloadStatus.Success -> {
                    bsDownload?.downloaded()
                }

                is ImageActionHelper.DownloadStatus.Error ->  {
                    bsDownload?.downloadError(state.msg)
                }
            }
        })
    }

    private fun showImageViewer(photosList: List<UnsplashPhotos>, childImgView: ImageView?, childPos: Int) {
        imageViewer?.dismiss()
        imageViewer = ImageViewerHelper(requireContext()).with(photosList,
            childImgView, childPos, object : ImageActionHelper.ImageActionListener() {

                override fun onSetWallpaper(photoInfo: UnsplashPhotos, name: String) {
                    viewModel.downloadImage(photoInfo) { photoData, _ ->
                        startActivity(Intent(context, ActImageEditor::class.java).also {
                            it.putExtra(PHOTO_DATA, photoData)
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
                    ImageActionHelper.shareImage(requireContext(), "Share", photoInfo.user.name, photoInfo.links.html)
                }

                override fun onUserPhotos(user: User) {
                    listener?.startMorePhotosActivity(user.moreListData)
                }
            })
            .setDataSaverMode(MausamApplication.getInstance()?.getSharedPrefs()?.isDataSaverMode ?: false)

        imageViewer?.onDismissListener = {
            imageViewer = null
        }

        imageViewer?.onPageChangeListener = {
            fragScope.launch {
                delay(300)
                binding?.recHomeContent?.scrollToPosition(it)
            }
        }

        imageViewer?.show()
    }

    private fun showErrorMsg(@StringRes msgId: Int){
        listener?.getMaterialSnackBar()?.showActionSnackBar(
            msgId,
            R.string.retry,
            MaterialSnackBar.LENGTH_INDEFINITE,
            object : MaterialSnackBar.SnackBarListener{
                override fun onActionPressed() {
                    viewModel.reload()
                    listener?.getMaterialSnackBar()?.dismiss()
                }
            }
        )
    }

    override fun onScrollToTop() {
        binding?.recHomeContent?.smoothScrollToPosition(0)
        binding?.ablHomeList?.setExpanded(true, true)
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri?)
        fun startMorePhotosActivity(data: MoreListData)
        fun getMaterialSnackBar(): MaterialSnackBar?
        fun startSettings()
        fun navigateToDiscover()
    }

    companion object {

        const val INITIAL_PAGE = 1

        const val PAGINATION_THRESHOLD = 4

        const val TAG = "HomeFragment"

        fun newInstance() = FragHome()

    }
}