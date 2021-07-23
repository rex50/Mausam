package com.rex50.mausam.views.activities.photos

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivityWithBinding
import com.rex50.mausam.databinding.ActPhotosListBinding
import com.rex50.mausam.databinding.HeaderCustomGeneralBinding
import com.rex50.mausam.databinding.HeaderCustomPhotographerBinding
import com.rex50.mausam.enums.ContentAnimationState
import com.rex50.mausam.interfaces.OnChildItemClickListener
import com.rex50.mausam.model_classes.item_types.HorizontalSquarePhotosTypeModel
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.utils.*
import com.rex50.mausam.views.activities.collections.ActCollectionsList
import com.rex50.mausam.views.activities.ActImageEditor
import com.rex50.mausam.views.adapters.AdaptContent
import com.rex50.mausam.views.bottomsheets.BSDownload
import com.rex50.mausam.views.bottomsheets.BSDownloadQuality
import com.rex50.mausam.views.bottomsheets.BSUserMore
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class ActPhotosList : BaseActivityWithBinding<ActPhotosListBinding>() {

    companion object{
        private const val INITIAL_PAGE = 1
    }

    private var scrollToTopActive = false

    private val adaptPhotos: AdaptContent by lazy {
        AdaptContent(this, ActPhotosListViewModel.getEmptyData())
    }

    private val bsDownload: BSDownload? by lazy {
        BSDownload(supportFragmentManager).also { it.isCancelable = false }
    }

    private var imageViewer: ImageViewerHelper? = null

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
                    getString(R.string.msg_empty_photos, Constants.Util.userFavConstants.random()),
                    getString(R.string.action_go_back)
                )
            )
        )
    }

    private val viewModel: ActPhotosListViewModel by viewModel()

    override fun bindView(): ActPhotosListBinding {
        return ActPhotosListBinding.inflate(layoutInflater)
    }

    override fun loadAct(savedInstanceState: Bundle?) {

        getArgs()

        init()

    }

    private fun getArgs() {
        intent?.getParcelableExtra<MoreListData>(Constants.IntentConstants.LIST_DATA)?.apply {
            viewModel.listData = this
        }
    }

    private fun init() {

        initHeader()

        initFABs()

        initRecycler()

        initClicks()

        initAnimations()

        setupPhotosObserver()

        setupLoadingStateObserver()

        setupDownloadObserver()

        viewModel.getPhotosOf(INITIAL_PAGE)

    }

    private fun initFABs() {
        binding?.fabBack?.setOnClickListener{ onBackPressed() }
        binding?.fabScrollToTop?.setOnClickListener{ if(scrollToTopActive) scrollToTop() else onBackPressed() }

        //For Animating scroll-to-top button movement
        val layoutTrans = binding?.rlPhotos?.layoutTransition
        layoutTrans?.setDuration(700)
        layoutTrans?.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun initHeader() {
        when(viewModel.listData.listMode){
            Constants.ListModes.LIST_MODE_USER_PHOTOS -> {
                binding?.headerPhotographer?.let { header ->
                    bindUserHeader(
                        header,
                        viewModel.getPageTitle(),
                        viewModel.getPageDesc(),
                        viewModel.getPhotographerInfo()
                    )
                }
            }

            else -> {
                binding?.headerGeneral?.let { header ->
                    bindGeneralHeader(
                        header,
                        viewModel.getPageTitle(),
                        viewModel.getPageDesc(),
                        viewModel.listData.getBgImgUrl()
                    )
                }
            }
        }
    }

    private fun bindGeneralHeader(
        header: HeaderCustomGeneralBinding,
        title: String,
        desc: String,
        bgImgUrl: String
    ) = with(header) {

        root.showView()

        gradientLine.background =
            GradientHelper.getInstance(this@ActPhotosList)?.getRandomLeftRightGradient()

        tvPageTitle.text = title

        tvPageDesc.text = desc

        bgImgUrl.takeIf { it.isNotEmpty() }?.let { url ->
            ivHeaderImg.loadImageWithPreLoader(url, null)
            flHeaderBg.showView()
        }

    }

    private fun bindUserHeader(
        header: HeaderCustomPhotographerBinding,
        title: String,
        desc: String,
        photographerInfo: User?
    ) = with(header) {
        root.showView()

        gradientLine.background =
            GradientHelper.getInstance(this@ActPhotosList)?.getRandomLeftRightGradient()

        tvPageTitle.text = title

        tvPageDesc.text = desc

        photographerInfo?.let { user ->

            ivUser.loadImageWithPreLoader(user.profileImage.large)

            user.totalCollections?.takeIf { it > 0 }?.let { collectionCount ->
                val collectionText = getString(R.string.see_collections) + "($collectionCount)"
                btnBrowseUserCollections.let { btn ->
                    btn.text = collectionText
                    btn.showView()
                    btn.setOnClickListener {
                        showUserCollections(
                            MoreListData(
                                Constants.ListModes.LIST_MODE_USER_COLLECTIONS,
                                user
                            )
                        )
                    }
                }
            }

            btnAddFavUser.apply {
                hideView()
                setOnClickListener{
                    showToast("Coming soon...")
                    //TODO : add user as favourite
                }
            }


            btnMore.setOnClickListener {
                val bsUserMore = BSUserMore()
                bsUserMore.initAndShow(supportFragmentManager, user)
            }
        }
    }

    private fun initRecycler() {

        val layoutManager = GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false)

        binding?.recSearchedPhotos?.layoutManager = layoutManager

        binding?.recSearchedPhotos?.apply {

            //For proper spacing around the items
            if (itemDecorationCount == 0)
                binding?.recSearchedPhotos?.addItemDecoration(ItemOffsetDecoration(this@ActPhotosList, R.dimen.recycler_item_offset_grid))

            //For Item animation while scrolling
            adapter = ScaleInAnimationAdapter(adaptPhotos).apply {
                setFirstOnly(false)
            }

            clearOnScrollListeners()

            //For Pagination of photos list
            addOnScrollListener(object: EndlessRecyclerOnScrollListener(layoutManager){
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    viewModel.getPhotosOf(page)
                }
            }.also {
                it.setInitialPage(INITIAL_PAGE)
                it.setVisibleThreshold(4)
            })

            //For Showing scroll-to-top button
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    binding?.fabScrollToTop?.apply {
                        if(layoutManager.findFirstVisibleItemPosition() > 0){
                            scrollToTopActive = true
                            toRightAndRotate()
                        }else{
                            scrollToTopActive = false
                            toNormal()
                        }
                    }
                }

            })
        }

    }

    private fun initClicks(){
        adaptPhotos.setChildClickListener(object : OnChildItemClickListener {
            override fun onItemClick(o: Any?, childImgView: ImageView?, childPos: Int) {

                object: GenericModelCastHelper(o){

                    override fun onHorizontalSquarePhotosTypeModel(horizontalSquarePhotosTypeModel: HorizontalSquarePhotosTypeModel) {
                        showImageViewer(horizontalSquarePhotosTypeModel.photosList, childImgView, childPos)
                    }

                }

            }
        })
    }

    private fun initAnimations() {

        animatedMessage.cacheAnimations()

        animatedMessage.onActionBtnClicked = { view, state ->
            when(state) {
                ContentAnimationState.NO_INTERNET -> {
                    viewModel.retry()
                }

                ContentAnimationState.EMPTY -> {
                    finish()
                }

                else -> {}
            }
        }

        animatedMessage.onLottieAnimationConfig = { lottieAnimationView, state ->
            when(state) {
                ContentAnimationState.EMPTY -> {
                    AnimConfigs.configureAstronautAnim(lottieAnimationView)
                }

                else -> AnimConfigs.defaultConfig(lottieAnimationView)
            }
        }
    }

    private fun setupPhotosObserver() {
        viewModel.photosData.observe(this, {
            adaptPhotos.update(it)
            imageViewer?.updateImages(it.photosList)
        })
    }

    private fun setupLoadingStateObserver() {

        viewModel.loadingState.observe(this, { state ->
            when(state) {

                null -> {}

                ContentAnimationState.SUCCESS -> {
                    animatedMessage.hide()
                    binding?.apply {
                        lvTop.hideView()
                        lvBottom.hideView()
                    }
                }

                ContentAnimationState.LOADING -> {
                    animatedMessage.hide()
                    binding?.apply {
                        if(viewModel.isListEmpty())
                            lvTop.showView()
                        else
                            lvBottom.showView()
                    }
                }

                ContentAnimationState.EMPTY -> {
                    binding?.apply {
                        lvTop.hideView()
                        lvBottom.hideView()
                    }
                    animatedMessage.setAnimationAndShow(state)
                }

                ContentAnimationState.NO_INTERNET -> {
                    binding?.apply {
                        lvTop.hideView()
                        lvBottom.hideView()
                    }
                    if(viewModel.isListEmpty()) {
                        animatedMessage.setAnimationAndShow(state)
                    } else {
                        materialSnackBar?.showActionSnackBar(getString(R.string.error_no_internet), "RETRY", object: MaterialSnackBar.SnackBarListener {
                            override fun onActionPressed() {
                                viewModel.retry()
                            }
                        })
                    }
                }

                ContentAnimationState.ERROR -> {
                    binding?.apply {
                        lvTop.hideView()
                        lvBottom.hideView()
                    }
                    val errMsg = viewModel.getErrorMessage()
                    materialSnackBar?.showActionSnackBar(errMsg ?: getString(R.string.error_failed_getting_photos), "RETRY", object: MaterialSnackBar.SnackBarListener {
                        override fun onActionPressed() {
                            if(viewModel.isListEmpty())
                                finish()
                            else {
                                materialSnackBar?.dismiss()
                                viewModel.retry()
                            }
                        }
                    })
                }
            }
        })
    }

    private fun setupDownloadObserver() {
        viewModel.getLiveDownloadStatus().observe(this, { state ->
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

    private fun showImageViewer(photosList: List<UnsplashPhotos>, childImgView: ImageView?, childPos: Int) {
        imageViewer?.dismiss()
        imageViewer = ImageViewerHelper(this).with(photosList,
            childImgView, childPos, object : ImageActionHelper.ImageActionListener() {

                override fun onSetWallpaper(photoInfo: UnsplashPhotos, name: String) {
                    BSDownloadQuality.showQualitySheet(supportFragmentManager) {
                        viewModel.downloadImage(photoInfo) { photoData, _ ->
                            startActivity(Intent(this@ActPhotosList, ActImageEditor::class.java).also {
                                it.putExtra(Constants.IntentConstants.PHOTO_DATA, photoData)
                            })
                        }
                    }
                }

                override fun onDownload(photoInfo: UnsplashPhotos, name: String) {
                    BSDownloadQuality.showQualitySheet(supportFragmentManager) {
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
                    ImageActionHelper.shareImage(this@ActPhotosList, "Share", photoInfo.user.name, photoInfo.links.html)
                }

                override fun onUserPhotos(user: User) {
                    startMorePhotosActivity(user.moreListData)
                }
            })
            .setDataSaverMode(mausamSharedPrefs?.isDataSaverMode ?: false)
            .showPhotographer(viewModel.isShowPhotographer)

        imageViewer?.onDismissListener = {
            imageViewer = null
        }

        imageViewer?.onPageChangeListener = {
            actScope.launch {
                delay(300)
                binding?.recSearchedPhotos?.scrollToPosition(it)
            }
        }

        imageViewer?.show()
    }

    private fun startMorePhotosActivity(data: MoreListData) {
        startActivity(
            Intent(
                this,
                ActPhotosList::class.java
            ).putExtra(
                Constants.IntentConstants.LIST_DATA,
                data
            )
        )
    }

    private fun showUserCollections(data: MoreListData){
        startActivity(
            Intent(
                this,
                ActCollectionsList::class.java
            ).putExtra(
                Constants.IntentConstants.LIST_DATA,
                data
            )
        )
    }

    private fun scrollToTop(){
        binding?.recSearchedPhotos?.smoothScrollToPosition(0)
        binding?.ablPhotosList?.setExpanded(true)
    }
}