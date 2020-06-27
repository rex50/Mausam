package com.rex50.mausam.views.activities

import android.animation.LayoutTransition
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.interfaces.GetUnsplashCollectionsAndTagsListener
import com.rex50.mausam.interfaces.OnChildItemClickListener
import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.unsplash.collection.Tag
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.IntentConstants.SEARCH_FEAT_COLLECTION
import com.rex50.mausam.views.adapters.AdaptContent
import kotlinx.android.synthetic.main.act_collections_list.*
import kotlinx.android.synthetic.main.act_wallpaper_list.*
import kotlinx.android.synthetic.main.act_wallpaper_list.ivLoader
import kotlinx.android.synthetic.main.header_custom_general.*
import org.apache.commons.lang3.StringUtils
import org.json.JSONArray

class ActCollectionsList : BaseActivity() {

    companion object{
        const val INITIAL_PAGE = 1
    }

    override fun getLayoutResource(): Int = R.layout.act_collections_list

    private var showFeatured = false

    private var pageTitle = ""
    private var pageDescription = ""
    private var collectionList = ArrayList<Collections>()
    private var collectionsModel: GenericModelFactory? = null
    private var adapter: AdaptContent? = null
    private var scrollToTopActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        init()
    }

    private fun init() {
        txtPageTitle?.text = pageTitle.takeIf { it.isNotEmpty() }?.let {
            StringUtils.capitalize(pageTitle)
        }?: getString(R.string.Images)

        pageDescription.takeIf { it.isNotEmpty() }?.apply {
            txtPageDesc?.text = this
        }?: txtPageDesc?.hideView()

        fabSearchedCollectionBack?.setOnClickListener{onBackPressed()}

        initRecycler()

        ivLoader?.showView()

        getCollectionsOf(INITIAL_PAGE)
    }

    private fun getArgs() {
        intent?.getBooleanExtra(SEARCH_FEAT_COLLECTION, false)?.apply {
            showFeatured = this
            pageTitle = if(this) "Featured Collections" else "Collections"
            pageDescription = Constants.Providers.POWERED_BY_UNSPLASH
        }
    }

    private fun initRecycler() {

        //val imageViewer: ImageViewerHelper? = ImageViewerHelper(this)

        val layoutManager = GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false)

        collectionsModel = GenericModelFactory.getCollectionListTypeObject(pageTitle, pageDescription, false, collectionList)
        adapter = AdaptContent(this, collectionsModel)
        adapter?.setChildClickListener(object : OnChildItemClickListener {
            override fun onItemClick(o: Any?, childImgView: ImageView?, childPos: Int) {
                /*imageViewer?.showImagesInFullScreen(wallpapersList, childImgView, childPos, object : ImageViewerHelper.ImageActionListener() {
                    override fun onDownload(photoInfo: UnsplashPhotos) {
                        super.onDownload(photoInfo)

                    }
                })*/
            }
        })

        recSearchedCollection?.layoutManager = layoutManager
        recSearchedWallpapers?.addItemDecoration(ItemOffsetDecoration(this, R.dimen.recycler_item_offset_grid))
        recSearchedCollection?.adapter = adapter

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

        UnsplashHelper(this).getCollectionsAndTags(page, 20, object: GetUnsplashCollectionsAndTagsListener {
            override fun onSuccess(collection: MutableList<Collections>?, tagsList: MutableList<Tag>?) {
                ivLoader?.hideView()
                collection?.apply {
                    when(page){
                        INITIAL_PAGE -> {
                            collectionList.clear()
                            collectionList.addAll(collection)
                            adapter?.notifyItemRangeInserted(0, 20)
                        }

                        else -> {
                            val lastSize = collectionList.size
                            collectionList.addAll(collection)
                            adapter?.notifyItemRangeInserted(lastSize, 20)
                        }
                    }
                }
            }

            override fun onFailed(errors: JSONArray?) {
                ivLoader?.hideView()
                materialSnackBar.showActionSnackBar(
                        R.string.failed_getting_collection_error_msg,
                        R.string.ok_caps,
                        MaterialSnackBar.LENGTH_INDEFINITE,
                        object : MaterialSnackBar.SnackBarListener{
                    override fun onActionPressed() {
                        if(collectionList.isEmpty())
                            onBackPressed()
                        materialSnackBar.dismiss()
                    }
                })
            }
        })
    }

    override fun internetStatus(internetType: Int) {
        //TODO("Not yet implemented")
    }
}