package com.rex50.mausam.views.activities.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rex50.mausam.base_classes.BaseAndroidViewModel
import com.rex50.mausam.utils.CacheFolderUtils
import kotlinx.coroutines.launch

class ActSettingsViewModel(app: Application): BaseAndroidViewModel(app) {

    init {
        calcAppCache()
    }

    private val mutableLiveCacheSize: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().also {
            it.value = 0
        }
    }

    val liveCacheSize: LiveData<Int> = mutableLiveCacheSize

    fun clearAppCache(onError: () -> Unit) = viewModelScope.launch {
        val cleared = CacheFolderUtils.deleteCache(getApplication())
        if (cleared)
            calcAppCache()
        else
            onError()
    }

    private fun calcAppCache() = viewModelScope.launch {
        val size = CacheFolderUtils.getCacheFolderSize(getApplication())
        Log.e("calcAppCache", "onCalculated: $size")
        val mb = (size/(1000*1000)).toInt()
        mutableLiveCacheSize.value = mb
    }


}