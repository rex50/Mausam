package com.rex50.mausam.views.activities.collections

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivityWithBinding
import com.rex50.mausam.databinding.ActCollectionsListBinding
import com.rex50.mausam.enums.ContentAnimationState
import com.rex50.mausam.interfaces.OnChildItemClickListener
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.IntentConstants.LIST_DATA
import com.rex50.mausam.utils.GradientHelper
import com.rex50.mausam.views.adapters.AdaptContent
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.android.synthetic.main.act_collections_list.*
import kotlinx.android.synthetic.main.header_custom_general.*
import com.rex50.mausam.model_classes.item_types.CollectionTypeModel
import com.rex50.mausam.views.activities.photos.ActPhotosList
import org.koin.android.viewmodel.ext.android.viewModel

class ActCollectionsList : BaseActivityWithBinding<ActCollectionsListBinding>() {

    companion object{
        const val INITIAL_PAGE = 1
    }

    private val adaptCollections: AdaptContent by lazy {
        AdaptContent(this, ActCollectionsListViewModel.getEmptyData())
    }
    private var scrollToTopActive = false

    private val viewModel by viewModel<ActCollectionsListViewModel>()

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

    override fun bindView(): ActCollectionsListBinding {
        return ActCollectionsListBinding.inflate(layoutInflater)
    }

    override fun loadAct(savedInstanceState: Bundle?) {
        getArgs()
        init()
    }

    private fun init() {

        initHeader()

        initFABs()

        initRecycler()

        initClicks()

        initAnimations()

        setupCollectionsObserver()

        setupLoadingStateObserver()

        binding?.lvTop?.showView()

        viewModel.getCollectionsOf(INITIAL_PAGE)
    }

    private fun initFABs() {
        fabCollectionBack?.setOnClickListener{onBackPressed()}
        fabScrollToTop?.setOnClickListener{ if(scrollToTopActive) scrollToTop() else onBackPressed() }

        //For Animating scroll-to-top button movement
        val layoutTrans = rlCollection?.layoutTransition
        layoutTrans?.setDuration(700)
        layoutTrans?.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun initHeader() {
        binding?.header?.apply {
            gradientLine.background = GradientHelper.getInstance(this@ActCollectionsList)?.getRandomLeftRightGradient()

            viewModel.listData.getBgImgUrl().takeIf { it.isNotEmpty() }?.apply {
                ivHeaderImg.loadImageWithPreLoader(this, null)
                flHeaderBg.showView()
            }

            tvPageTitle.text = viewModel.listData.getTitle(this@ActCollectionsList)

            tvPageDesc.text = viewModel.listData.getDesc()
        }

    }

    private fun getArgs() {
        intent?.getParcelableExtra<MoreListData>(LIST_DATA)?.apply {
            viewModel.listData = this
        }
    }

    private fun initRecycler() {

        val layoutManager = GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false)

        binding?.recSearchedCollection?.layoutManager = layoutManager

        binding?.recSearchedCollection?.apply {

            //For proper spacing around the items
            if (itemDecorationCount == 0)
                addItemDecoration(ItemOffsetDecoration(this@ActCollectionsList, R.dimen.recycler_item_offset_grid))

            //For Item animation while scrolling
            binding?.recSearchedCollection?.adapter = ScaleInAnimationAdapter(adaptCollections).apply {
                setFirstOnly(false)
            }

            clearOnScrollListeners()

            //For Pagination of collections list
            addOnScrollListener(object: EndlessRecyclerOnScrollListener(layoutManager){
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    viewModel.getCollectionsOf(page)
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

    private fun initClicks() {
        adaptCollections.setChildClickListener(object : OnChildItemClickListener {
            override fun onItemClick(o: Any?, childImgView: ImageView?, childPos: Int) {
                object : GenericModelCastHelper(o) {
                    override fun onCollectionType(collectionTypeModel: CollectionTypeModel) {
                        startMorePhotosActivity(collectionTypeModel.collections[childPos].moreListData)
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

    private fun setupCollectionsObserver() {
        viewModel.collectionsData.observe(this) {
            adaptCollections.update(it)
        }
    }

    private fun setupLoadingStateObserver() {
        viewModel.loadingState.observe(this) { state ->
            when(state) {
                null -> {}

                ContentAnimationState.SUCCESS ->{
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
                    if(viewModel.isListEmpty())
                        animatedMessage.setAnimationAndShow(state)
                    else {
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
                    materialSnackBar?.showActionSnackBar(getString(R.string.error_failed_getting_collections), "RETRY", object: MaterialSnackBar.SnackBarListener {
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
        }
    }

    private fun startMorePhotosActivity(data: MoreListData) {
        startActivity(
            Intent(
                this,
                ActPhotosList::class.java
            ).putExtra(
                LIST_DATA,
                data
            )
        )
    }

    private fun scrollToTop(){
        recSearchedCollection?.smoothScrollToPosition(0)
        ablCollectionList?.setExpanded(true)
    }

}