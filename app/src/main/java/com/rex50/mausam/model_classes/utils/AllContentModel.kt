package com.rex50.mausam.model_classes.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rex50.mausam.interfaces.OnGroupItemClickListener
import com.rex50.mausam.views.adapters.AdaptHome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllContentModel {
    private val models: ArrayList<GenericModelFactory?> by lazy { arrayListOf() }

    private var sequenceOfLayout: List<String>
    private var adapter: AdaptHome? = null
    private val types: MutableList<String> by lazy { mutableListOf() }
    private var responsesCount = 0
    private var insertedListener: ContentInsertedListener? = null
    @Volatile private var allContentLoaded = false

    private val modelLiveList: MutableLiveData<AllContentModel> by lazy {
        MutableLiveData<AllContentModel>()
    }


    companion object {
        private const val TAG = "AllContentModel"
    }

    init {
        sequenceOfLayout = ArrayList()
    }

    fun addModel(type: String, model: GenericModelFactory) {
        models.add(model)
        types.add(type)
        updateLiveList()
    }

    @Synchronized
    fun addOrUpdateModel(type: String, model: GenericModelFactory){
        types.indexOf(type).takeIf { it != -1 }?.let { pos ->
            models.removeAt(pos)
            models.add(pos, model)
        } ?: addSequentially(type, model)
        updateLiveList()
    }

    fun addModel(pos: Int, type: String, model: GenericModelFactory) {
        models.add(pos, model)
        types.add(pos, type)
        updateLiveList()
    }

    private fun updateLiveList() {
        CoroutineScope(Dispatchers.Main).launch {
            modelLiveList.value = this@AllContentModel
        }
    }

    fun setSequenceOfLayouts(sequence: List<String>) {
        sequenceOfLayout = sequence
    }

    fun setContentInsertListener(insertedListener: ContentInsertedListener?){
        this.insertedListener = insertedListener
    }

    @Synchronized
    fun addSequentially(type: String, model: GenericModelFactory): Int? {
        check(sequenceOfLayout.isNotEmpty()) { "set sequence before using addSequentially()" }
        return try {
            if (size() == 0) {
                addModel(type, model)
                insertedListener?.onContentAdded(type, model, 0)
                return 0
            }
            val pos = sequenceOfLayout.indexOf(type)
            for (i in 0 until size()) {
                val addedPos = sequenceOfLayout.indexOf(getType(i))
                if (addedPos > pos) {
                    addModel(i, type, model)
                    insertedListener?.onContentAdded(type, model, i)
                    return i
                }
            }
            addModel(type, model)
            insertedListener?.onContentAdded(type, model, size()-1)
            size() - 1
        } catch (e: Exception) {
            Log.e(TAG, "addSequentially: ", e)
            null
        } finally {
            increaseResponseCount()
        }
    }

    fun increaseResponseCount(){
        responsesCount++
        insertedListener?.onContentAddedCount(responsesCount, sequenceOfLayout.size)
        if (sequenceOfLayout.size == responsesCount && !allContentLoaded) {
            allContentLoaded = true
            adapter?.notifyDataSetChanged()
            insertedListener?.onAllContentLoaded()
        }
    }

    fun setAdapter(adapter: AdaptHome?) {
        this.adapter = adapter
    }

    fun setOnClickListener(listener: OnGroupItemClickListener) {
        if (adapter != null) {
            adapter?.itemClickListener = listener
        }
    }

    fun getModel(type: String): GenericModelFactory? {
        return types.indexOf(type).takeIf { it != -1 }?.let { pos ->
            models[pos]
        }
    }

    fun getModel(pos: Int): GenericModelFactory? {
        return models[pos]
    }

    fun getType(pos: Int): String {
        return types[pos]
    }

    fun size(): Int {
        return models.size
    }

    fun getModelLiveList(): LiveData<AllContentModel> {
        return modelLiveList
    }

    fun remove(type: String) {
        types.indexOf(type).takeIf { it != -1 }?.let { pos ->
            models.removeAt(pos)
            types.removeAt(pos)
            updateLiveList()
        }
    }

    fun clearList() {
        models.clear()
        types.clear()
        updateLiveList()
    }

    interface ContentInsertedListener{
        fun onContentAdded(type: String, model: GenericModelFactory, insertedPos: Int){}

        fun onContentAddedCount(loadedCount: Int, totalCount: Int){}

        fun onAllContentLoaded()
    }

}