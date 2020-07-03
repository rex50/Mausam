package com.rex50.mausam.model_classes.utils

import android.util.Log
import com.rex50.mausam.interfaces.OnGroupItemClickListener
import com.rex50.mausam.views.adapters.AdaptHome

class AllContentModel {
    private val models: ArrayList<GenericModelFactory?>?
    private var sequenceOfLayout: List<String>
    private var adapter: AdaptHome? = null
    private val types: MutableList<String>
    private var responsesCount = 0
    private var insertedListener: ContentInsertedListener? = null
    @Volatile private var allContentLoaded = false


    companion object {
        private const val TAG = "AllContentModel"
    }

    init {
        models = ArrayList()
        types = ArrayList()
        sequenceOfLayout = ArrayList()
    }

    fun addModel(type: String, model: GenericModelFactory) {
        models?.add(model)
        types.add(type)
    }

    fun addModel(pos: Int, type: String, model: GenericModelFactory) {
        models?.add(pos, model)
        types.add(pos, type)
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
        increaseResponseCount()
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

    fun getModel(pos: Int): GenericModelFactory? {
        return models?.get(pos)
    }

    fun getType(pos: Int): String {
        return types[pos]
    }

    fun size(): Int {
        return models?.size ?: 0
    }

    interface ContentInsertedListener{
        fun onContentAdded(type: String, model: GenericModelFactory, insertedPos: Int){}

        fun onContentAddedCount(loadedCount: Int, totalCount: Int){}

        fun onAllContentLoaded()
    }

}