package com.rex50.mausam.model_classes.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rex50.mausam.enums.ContentLoadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllContentModel {
    private val models: ArrayList<GenericModelFactory> by lazy { arrayListOf() }

    var sequenceOfLayout: ArrayList<String> = arrayListOf()
        private set
    private val types: MutableList<String> by lazy { mutableListOf() }
    private var responsesCount = 0
    private var insertedListener: ContentInsertedListener? = null
    @Volatile var allContentLoaded = false
        private set

    private val modelLiveList: MutableLiveData<AllContentModel> by lazy {
        MutableLiveData<AllContentModel>()
    }

    private val loadingState: MutableLiveData<ContentLoadingState<AllContentModel>> by lazy {
        MutableLiveData<ContentLoadingState<AllContentModel>>().also {
            it.postValue(ContentLoadingState.Preparing)
        }
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
    suspend fun addOrUpdateModel(type: String, model: GenericModelFactory){
        types.indexOf(type).takeIf { it != -1 }?.let { pos ->
            models.removeAt(pos)
            models.add(pos, model)
            updateLoadingState()
        } ?: addSequentially(type, model)
        updateLiveList()
    }

    private fun addModel(pos: Int, type: String, model: GenericModelFactory) {
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
        sequenceOfLayout.clear()
        sequenceOfLayout.addAll(sequence)
        updateLoadingState()
    }

    fun removeSection(section: String) {
        if(sequenceOfLayout.contains(section)) {
            sequenceOfLayout.remove(section)
            responsesCount--
            allContentLoaded = sequenceOfLayout.size == responsesCount
        }
    }

    fun addSection(pos: Int, section: String) {
        if(!sequenceOfLayout.contains(section)) {
            sequenceOfLayout.add(pos, section)
            allContentLoaded = sequenceOfLayout.size == responsesCount
        }
    }

    fun setContentInsertListener(insertedListener: ContentInsertedListener?){
        this.insertedListener = insertedListener
    }

    @Synchronized
    suspend fun addSequentially(type: String, model: GenericModelFactory): Int? = withContext(Dispatchers.IO) {
        check(sequenceOfLayout.isNotEmpty()) { "set sequence before using addSequentially()" }
        return@withContext try {
            if (size() == 0) {
                addModel(type, model)
                insertedListener?.onContentAdded(type, model, 0)
                return@withContext 0
            }
            val pos = sequenceOfLayout.indexOf(type)
            for (i in 0 until size()) {
                val addedPos = sequenceOfLayout.indexOf(getType(i))
                if (addedPos > pos) {
                    addModel(i, type, model)
                    insertedListener?.onContentAdded(type, model, i)
                    return@withContext i
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
        updateLoadingState()
    }

    private fun updateLoadingState() {
        when {
            sequenceOfLayout.size == responsesCount && models.isEmpty() -> {
                loadingState.postValue(ContentLoadingState.Empty)
            }

            sequenceOfLayout.size == responsesCount -> {
                allContentLoaded = true
                insertedListener?.onAllContentLoaded()
                loadingState.postValue(ContentLoadingState.Ready(getThis()))
            }

            else -> {
                allContentLoaded = false
                loadingState.postValue(ContentLoadingState.Preparing)
            }
        }
    }

    private fun getThis() = this

    fun getModel(type: String): GenericModelFactory? {
        return types.indexOf(type).takeIf { it != -1 }?.let { pos ->
            models[pos]
        }
    }

    fun getModel(pos: Int): GenericModelFactory {
        return models[pos]
    }

    fun getType(pos: Int): String {
        return types[pos]
    }

    fun size(): Int {
        return models.size
    }

    fun getModelLiveList(): LiveData<AllContentModel> = modelLiveList

    val contentLoadingState: LiveData<ContentLoadingState<AllContentModel>>
        get() {
            return loadingState
        }

    fun remove(type: String) {
        types.indexOf(type).takeIf { it != -1 }?.let { pos ->
            models.removeAt(pos)
            types.removeAt(pos)
            updateLiveList()
            updateLoadingState()
        }
    }

    fun clearList() {
        models.clear()
        types.clear()
        responsesCount = 0
        allContentLoaded = false
        sequenceOfLayout = arrayListOf()
        updateLiveList()
        updateLoadingState()
    }

    fun setOnNoInternet() {
        loadingState.postValue(ContentLoadingState.NoInternet)
    }

    interface ContentInsertedListener{
        fun onContentAdded(type: String, model: GenericModelFactory, insertedPos: Int){}

        fun onContentAddedCount(loadedCount: Int, totalCount: Int){}

        fun onAllContentLoaded()
    }

}