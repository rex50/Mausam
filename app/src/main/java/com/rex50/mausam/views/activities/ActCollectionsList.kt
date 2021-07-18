package com.rex50.mausam.views.activities

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.interfaces.OnChildItemClickListener
import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.IntentConstants.LIST_DATA
import com.rex50.mausam.utils.GradientHelper
import com.rex50.mausam.views.adapters.AdaptContent
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.android.synthetic.main.act_collections_list.*
import kotlinx.android.synthetic.main.header_custom_general.*
import com.rex50.mausam.model_classes.item_types.CollectionTypeModel
import com.rex50.mausam.network.Result
import com.rex50.mausam.views.activities.photoslist.ActPhotosList
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ActCollectionsList : BaseActivity() {

    companion object{
        const val INITIAL_PAGE = 1
    }

    override val layoutResource: Int
        get() = R.layout.act_collections_list

    private var collectionList = ArrayList<Collections>()
    private var collectionsModel: GenericModelFactory? = null
    private var adapter: AdaptContent? = null
    private var scrollToTopActive = false
    private var listData: MoreListData? = null

    private val unsplashHelper: UnsplashHelper by inject()

    override fun loadAct(savedInstanceState: Bundle?) {
        getArgs()
        init()
    }

    private fun init() {

        gradientLine?.background = GradientHelper.getInstance(this)?.getRandomLeftRightGradient()

        listData?.getBgImgUrl()?.takeIf { it.isNotEmpty() }?.apply {
            ivHeaderImg?.loadImageWithPreLoader(this, null)
            flHeaderBg?.showView()
        }

        tvPageTitle?.text = listData?.getTitle(this)

        tvPageDesc?.text = listData?.getDesc()

        fabCollectionBack?.setOnClickListener{onBackPressed()}
        fabSearchedCollectionBack?.setOnClickListener{ if(scrollToTopActive) scrollToTop() else onBackPressed() }

        initRecycler()

        ivLoader?.showView()

        getCollectionsOf(INITIAL_PAGE)
    }

    private fun getArgs() {
        intent?.getParcelableExtra<MoreListData>(LIST_DATA)?.apply {
            listData = this
        }
    }

    private fun initRecycler() {

        val layoutManager = GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false)

        listData?.let { data ->
            collectionsModel = GenericModelFactory.getCollectionListTypeObject(data.getTitle(this), data.getDesc(), false, collectionList)
            adapter = AdaptContent(this, collectionsModel)
            adapter?.setChildClickListener(object : OnChildItemClickListener {
                override fun onItemClick(o: Any?, childImgView: ImageView?, childPos: Int) {
                    object : GenericModelCastHelper(o) {
                        override fun onCollectionType(collectionTypeModel: CollectionTypeModel) {
                            startMorePhotosActivity(collectionTypeModel.collections[childPos].moreListData)
                        }
                    }
                }
            })
        }

        recSearchedCollection?.layoutManager = layoutManager
        recSearchedCollection?.addItemDecoration(ItemOffsetDecoration(this, R.dimen.recycler_item_offset_grid))
        recSearchedCollection?.adapter = ScaleInAnimationAdapter(adapter!!).apply {
            setFirstOnly(false)
        }

        val endlessScrollListener =  object: EndlessRecyclerOnScrollListener(layoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getCollectionsOf(page)
            }
        }

        endlessScrollListener.apply {
            setInitialPage(INITIAL_PAGE)
            setVisibleThreshold(4)
        }

        val layoutTrans = rlCollection?.layoutTransition
        layoutTrans?.setDuration(700)
        layoutTrans?.enableTransitionType(LayoutTransition.CHANGING)

        val scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                fabSearchedCollectionBack?.apply {
                    if(layoutManager.findFirstVisibleItemPosition() > 0){
                        scrollToTopActive = true
                        toRightAndRotate()
                    }else{
                        scrollToTopActive = false
                        toNormal()
                    }
                }
            }

        }

        recSearchedCollection.addOnScrollListener(endlessScrollListener)
        recSearchedCollection.addOnScrollListener(scrollListener)

    }

    private fun getCollectionsOf(page: Int) {
        if (page != INITIAL_PAGE)
            lvCenterss.showView()
        when(listData?.listMode){
            Constants.ListModes.LIST_MODE_COLLECTIONS -> {
                getFeaturedCollectionsOf(page)
            }

            Constants.ListModes.LIST_MODE_USER_COLLECTIONS -> {
                getUserCollectionsOf(page)
            }
        }

    }

    private fun getUserCollectionsOf(page: Int) = MainScope().launch {
        when (val result = unsplashHelper.getUserCollections(listData?.photographerInfo?.username ?: "", page, 20)) {
            is Result.Success -> {
                ivLoader?.hideView()
                lvCenterss.hideView()
                editList(page, result.data.collectionsList)
            }

            is Result.Failure -> {
                ivLoader?.hideView()
                lvCenterss.hideView()
                showErrorMsg()
            }
        }
    }

    private fun getFeaturedCollectionsOf(page: Int) = MainScope().launch {
        when(val result = unsplashHelper.getCollectionsAndTags(page, 20)) {
            is Result.Success -> {
                ivLoader?.hideView()
                lvCenterss.hideView()
                editList(page, result.data.collectionsList)
            }

            is Result.Failure -> {
                ivLoader?.hideView()
                lvCenterss.hideView()
                showErrorMsg()
            }
        }
    }

    private fun showErrorMsg() {
        materialSnackBar?.showActionSnackBar(
                R.string.failed_getting_collection_error_msg,
                R.string.ok_caps,
                MaterialSnackBar.LENGTH_INDEFINITE,
                object : MaterialSnackBar.SnackBarListener{
                    override fun onActionPressed() {
                        if(collectionList.isEmpty())
                            onBackPressed()
                        materialSnackBar?.dismiss()
                    }
                })
    }

    private fun editList(page: Int, collections: List<Collections>?){
        collections?.apply {
            when(page){
                INITIAL_PAGE -> {
                    collectionList.clear()
                    collectionList.addAll(collections)
                    adapter?.notifyItemRangeInserted(0, collections.size)
                }

                else -> {
                    val lastSize = collectionList.size
                    collectionList.addAll(collections)
                    adapter?.notifyItemRangeInserted(lastSize, collections.size)
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

    override fun internetStatus(internetType: Int) {
        //TODO("Not yet implemented")
    }
}