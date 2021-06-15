package com.rex50.mausam.views.activities

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
import com.rex50.mausam.views.adapters.AdaptUsedLibrary
import kotlinx.android.synthetic.main.act_used_library.*
import kotlinx.android.synthetic.main.frag_search_result.*

class ActUsedLibrary : BaseActivity() {
    private var librariesList: ArrayList<Library> = arrayListOf()

    override fun loadAct(savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        initHeader()

        initBackButton()

        initRecycler()
    }

    private fun initRecycler() {
        val recyclerView = findViewById<RecyclerView>(R.id.recLibraries)

        val layout = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        recyclerView.layoutManager = layout

        val adapter = AdaptUsedLibrary(getData())

        adapter.listener = object: OnUsedLibrariesAdapterListener{
            override fun onClickLibrariesMaterial(url: String) {
                openUrl(url)
            }
        }
        recyclerView?.adapter = adapter
    }

    private fun initHeader() {
        findViewById<TextView>(R.id.tvPageTitle)?.text = "Libraries & resources"
        findViewById<TextView>(R.id.tvPageDesc)?.text = "open source Libraries and resources used making Mausam"
    }

    override val layoutResource: Int
        get() = R.layout.act_used_library

    fun getData(): ArrayList<Library> {
        val gradientHelper = GradientHelper.getInstance(this)

        librariesList?.clear()

        gradientHelper?.let {
            librariesList?.add(Library("Volley ","it help easy way to do api","https://github.com/google/volley", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("Glide","to load url image","https://github.com/bumptech/glide", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("Joda","use time amd date","https://github.com/JodaOrg/joda-time", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("Room","it help easy way to do api","https://developer.android.com/training/data-storage/room", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("Switcher","it help easy way to do api","https://github.com/bitvale/Switcher", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("PhotoView","it help easy way to do api","https://github.com/Baseflow/PhotoView", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("Stfalcon- sutdio","it help easy way to do api","https://github.com/stfalcon-studio/StfalconImageViewer", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("fetch","it help easy way to do api","https://github.com/tonyofrancis/Fetch", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("Recycleview-Animation","it help easy way to do api","https://github.com/wasabeef/recyclerview-animators", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("compressor","it help easy way to do api","https://github.com/zetbaitsu/Compressor", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("Android-Image-Cropper","it help easy way to do api","https://github.com/ArthurHub/Android-Image-Cropper", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("GpuImage","it help easy way to do api","https://github.com/BradLarson/GPUImage", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("BlurImage","it help easy way to do api","https://github.com/sparrow007/BlurImage", gradientHelper.getRandomLeftRightGradient()))
            librariesList?.add(Library("dotsindicator","it help easy way to do api","https://github.com/tommybuonomo/dotsindicator", gradientHelper.getRandomLeftRightGradient()))
        }
        return librariesList
    }

    fun initBackButton(){

        var btnBack = findViewById<FloatingActionButton>(R.id.TopLibrariesBak)

        btnBack?.setOnClickListener(){
            onBackPressed()
        }
    }

}