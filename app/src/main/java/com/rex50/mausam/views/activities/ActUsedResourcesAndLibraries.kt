package com.rex50.mausam.views.activities

import android.animation.LayoutTransition
import android.app.ActionBar
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.interfaces.OnUsedLibrariesAdapterListener
import com.rex50.mausam.model_classes.utils.Library
import com.rex50.mausam.utils.GradientHelper
import com.rex50.mausam.utils.openUrl
import com.rex50.mausam.utils.toNormal
import com.rex50.mausam.utils.toRightAndRotate
import com.rex50.mausam.views.adapters.AdaptUsedLibrary
import kotlinx.android.synthetic.main.act_photos_list.*
import kotlinx.android.synthetic.main.act_used_library.*
import kotlinx.android.synthetic.main.frag_search_result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class ActUsedResourcesAndLibraries : BaseActivity() {
    private var adapter: AdaptUsedLibrary? = null
    private var scrollToTopActive: Boolean = false
    var btnBackTop : FloatingActionButton? = null
    private val gradientHelper: GradientHelper by inject()

    override fun loadAct(savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        initHeader()

        initBackButton()

        initRecycler()

        loadData()
    }

    private fun loadData() = CoroutineScope(Dispatchers.Main).launch {
       adapter?.updateList(withContext(Dispatchers.IO) {
            getData()
       })
    }

    private fun initRecycler() {
        val recyclerView = findViewById<RecyclerView>(R.id.recLibraries)

        val layout = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        recyclerView.layoutManager = layout

        adapter = AdaptUsedLibrary(listOf())

        val layoutTrans = rlLibraries?.layoutTransition
        layoutTrans?.setDuration(700)
        layoutTrans?.enableTransitionType(LayoutTransition.CHANGING)

        adapter?.listener = object: OnUsedLibrariesAdapterListener{
            override fun onClickLibrariesMaterial(url: String) {
                openUrl(url)
            }
        }
        recyclerView?.adapter = adapter

        val scrollListener = object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                btnBackTop?.apply {
                    if(layout.findFirstVisibleItemPosition() > 0){
                        scrollToTopActive = true
                        toRightAndRotate()
                    }else{
                        scrollToTopActive = false
                        toNormal()
                    }
                }

            }

        }

        recyclerView?.apply {
            addOnScrollListener(scrollListener)
        }
    }

    private fun initHeader() {
        findViewById<TextView>(R.id.tvPageTitle)?.text = "Libraries & resources"
        findViewById<TextView>(R.id.tvPageDesc)?.text = "Open source Libraries and resources used in Mausam"
    }

    override val layoutResource: Int
        get() = R.layout.act_used_library

    private fun getData(): List<Library> {

        val librariesList: ArrayList<Library> = arrayListOf()

        librariesList.add(Library(
            "Volley",
            "Volley is an HTTP library that makes networking for Android apps easier and, most importantly, faster.",
            "https://github.com/google/volley",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "Glide",
            "An image loading and caching library for Android focused on smooth scrolling.",
            "https://github.com/bumptech/glide",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "Joda",
            "Joda-Time is the widely used replacement for the Java date and time classes prior to Java SE 8.",
            "https://github.com/JodaOrg/joda-time",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "Room",
            "The Room persistence library provides an abstraction layer over SQLite to allow fluent database access while harnessing the full power of SQLite.",
            "https://developer.android.com/training/data-storage/room",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "Switcher",
            "Android implementation of switch animation.",
            "https://github.com/bitvale/Switcher",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "PhotoView",
            "Implementation of ImageView for Android that supports zooming, by various touch gestures.",
            "https://github.com/Baseflow/PhotoView",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "Stfalcon Imageviewer",
            "A simple and customizable Android full-screen image viewer with shared image transition support, \"pinch to zoom\" and \"swipe to dismiss\" gestures.",
            "https://github.com/stfalcon-studio/StfalconImageViewer",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "Fetch",
            "The best file downloader library for Android.",
            "https://github.com/tonyofrancis/Fetch",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "RecyclerView Animators",
            "it help easy An Android Animation library which easily add itemanimator to RecyclerView items. to do api",
            "https://github.com/wasabeef/recyclerview-animators",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "Compressor",
            "An android image compression library.",
            "https://github.com/zetbaitsu/Compressor",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "Android Image Cropper",
            "Image Cropping Library for Android, optimized for Camera / Gallery.",
            "https://github.com/ArthurHub/Android-Image-Cropper",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "GpuImage",
            "Android filters based on OpenGL (idea from GPUImage for iOS).",
            "https://github.com/BradLarson/GPUImage",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "Dots Indicator",
            "Three material Dots Indicators for view pagers in Android ! ",
            "https://github.com/tommybuonomo/dotsindicator",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "Feather icons",
            "Simply beautiful open source icons.",
            "https://github.com/feathericons/feather",
            gradientHelper.getRandomLeftRightGradient()))

        librariesList.add(Library(
            "Push Anim Down",
            "A library for Android developers who want to create \"push down animation click\" for view like spotify application.",
            "https://github.com/TheKhaeng/pushdown-anim-click",
            gradientHelper.getRandomLeftRightGradient()));

        librariesList.add(Library(
            "Doki",
            "An Android library that displays information from dontkillmyapp.com to users in a clean and presentable manner.",
            "https://github.com/DoubleDotLabs/doki",
            gradientHelper.getRandomLeftRightGradient()));

        librariesList.add(Library(
            "Image Blur",
            "An efficient image blurring utility library for Android",
            "https://github.com/rex50/ImageBlur",
            gradientHelper.getRandomLeftRightGradient()));

        librariesList.add(Library(
            "Unsplash Photos",
            "Beautiful, free images and photos that you can download and use for any project. Better than any royalty free or stock photos.",
            "https://unsplash.com/",
            gradientHelper.getRandomLeftRightGradient()));

        librariesList.add(Library(
            "Lottie for Android",
            "Lottie is a mobile library for Android and iOS that parses Adobe After Effects animations exported as json with Bodymovin and renders them natively on mobile!",
            "https://github.com/airbnb/lottie-android",
            gradientHelper.getRandomLeftRightGradient()));

        librariesList.add(Library(
            "LottieFiles",
            "LottieFiles provides all the tools that you need to create, edit, test and display Lottie animations.",
            "https://lottiefiles.com/",
            gradientHelper.getRandomLeftRightGradient()));

        librariesList.add(Library(
            "PixelTrue free animations",
            "500+ Free Illustrations & Animations. Updated Weekly. Free to use for personal and commercial use without attribution",
            "https://www.pixeltrue.com/free-illustrations",
            gradientHelper.getRandomLeftRightGradient()));

        librariesList.shuffle()

        return librariesList
    }

    fun initBackButton(){

        var btnBack = findViewById<FloatingActionButton>(R.id.TopLibrariesBack)
         btnBackTop = findViewById<FloatingActionButton>(R.id.LibrariesBack)

        btnBack?.setOnClickListener(){
            onBackPressed()
        }
        btnBackTop?.setOnClickListener{ if(scrollToTopActive) scrollToTop() else onBackPressed() }
    }

    private fun scrollToTop(){
        recLibraries?.smoothScrollToPosition(0)
        ablLibrariesList?.setExpanded(true)
    }

}