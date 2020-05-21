package com.rex50.mausam.base_classes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(){

    private var isFragmentLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getResourceLayout(), container, false)
    }

    var activity: BaseActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity)
            activity = context
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        whenResumed()
        if(!isFragmentLoaded){
            load()
            isFragmentLoaded = true
        }
    }

    abstract fun getResourceLayout(): Int

    /**
     * use this function to init layouts only
     */
    abstract fun initView()

    /**
     * use this function to load
     */
    abstract fun load()


    open fun whenResumed(){}

    override fun onDestroy() {
        super.onDestroy()
    }


}