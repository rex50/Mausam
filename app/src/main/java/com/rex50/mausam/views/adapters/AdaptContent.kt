package com.rex50.mausam.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.interfaces.OnChildItemClickListener
import com.rex50.mausam.model_classes.unsplash.collection.Tag
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.RecyclerItemTypes
import com.rex50.mausam.MausamApplication
import com.rex50.mausam.utils.Constants.RecyclerItemLayouts
import com.rex50.mausam.views.adapters.diffUtils.AdaptContentDiffUtil
import com.rex50.mausam.views.adapters.holders.*

//TODO: remove context from here and send required objects only instead.
class AdaptContent(context: Context?, private var model: GenericModelFactory?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var childClickListener: OnChildItemClickListener? = null
    private var gradientHelper = GradientHelper.getInstance(context)
    private val isDataSaverMode = MausamApplication.getInstance()?.getSharedPrefs()?.isDataSaverMode ?: false

    //For comparing with new list while updating
    private val oldList: ArrayList<Any> = arrayListOf()

    fun setChildClickListener(childClickListener: OnChildItemClickListener?) {
        this.childClickListener = childClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, layout: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return when (layout) {
            RecyclerItemLayouts.USER_LAYOUT -> UserTypeHolder(v)
            RecyclerItemLayouts.COLOR_LAYOUT -> ColorTypeHolder(v)
            RecyclerItemLayouts.GENERAL_LAYOUT -> GeneralTypeHolder(v)
            RecyclerItemLayouts.COLLECTION_LAYOUT, RecyclerItemLayouts.COLLECTION_LIST_LAYOUT -> CollectionTypeHolder(v)
            RecyclerItemLayouts.TAG_LAYOUT -> TextTypeHolder(v)
            RecyclerItemLayouts.CATEGORY_LAYOUT -> CategoryTypeHolder(v)
            RecyclerItemLayouts.FAV_PHOTOGRAPHER_PHOTOS_LAYOUT -> FavPhotographerPhotosTypeHolder(v)
            else -> throw IllegalArgumentException("Inflate code for viewType:\"$layout\" is not found")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        model?.apply {
            when (childType) {

                RecyclerItemTypes.GENERAL_TYPE -> {
                    val generalHolder = holder as GeneralTypeHolder

                    generalHolder.bind(model?.get(position), isDataSaverMode)
                    generalHolder.setClickListener {
                        childClickListener?.onItemClick(
                            model,
                            generalHolder.imageView,
                            position
                        )
                    }
                }

                RecyclerItemTypes.USER_TYPE -> {
                    val userHolder = holder as UserTypeHolder
                    userHolder.bind(model?.get(position), isDataSaverMode)
                    userHolder.setClickListener {
                        childClickListener?.onItemClick(
                            model,
                            null,
                            position
                        )
                    }
                }

                RecyclerItemTypes.COLOR_TYPE -> {
                    val colorHolder = holder as ColorTypeHolder
                    colorHolder.bind(model?.get(position))
                    colorHolder.setClickListener {
                        childClickListener?.onItemClick(
                            model,
                            null,
                            position
                        )
                    }
                }

                RecyclerItemTypes.COLLECTION_TYPE, RecyclerItemTypes.COLLECTION_LIST_TYPE -> {
                    val collectionHolder = holder as CollectionTypeHolder
                    collectionHolder.bind(model?.get(position), isDataSaverMode)
                    collectionHolder.setClickListener {
                        childClickListener?.onItemClick(
                            model,
                            null,
                            position
                        )
                    }
                }

                RecyclerItemTypes.TAG_TYPE -> {
                    val textHolder = holder as TextTypeHolder
                    textHolder.bind(model?.get<Tag>(position))
                    textHolder.setClickListener {
                        childClickListener?.onItemClick(
                            model,
                            null,
                            position
                        )
                    }
                }

                RecyclerItemTypes.CATEGORY_TYPE -> {
                    val categoryHolder = holder as CategoryTypeHolder
                    categoryHolder.bind(model?.get(position), gradientHelper?.getRandomLeftRightGradient())
                    categoryHolder.setClickListener {
                        childClickListener?.onItemClick(
                            model,
                            null,
                            position
                        )
                    }
                }

                RecyclerItemTypes.FAV_PHOTOGRAPHER_PHOTOS_TYPE -> {
                    val favHolder = holder as FavPhotographerPhotosTypeHolder
                    favHolder.bind(model?.get(position), isDataSaverMode)
                    favHolder.setClickListener {
                        childClickListener?.onItemClick(
                            model,
                            favHolder.imageView,
                            position
                        )
                    }
                }

                else -> throw IllegalArgumentException("Code missing for $childType")

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return model?.childLayout ?: R.layout.cell_type_general
    }

    override fun getItemCount(): Int {
        return model?.getTotalItems() ?: 0
    }

    fun update(data: GenericModelFactory) {
        DiffUtil.calculateDiff(AdaptContentDiffUtil(
            oldList,
            data.getList()
        )).dispatchUpdatesTo(this@AdaptContent)
        model = data
        oldList.clear()
        oldList.addAll(data.getList())
    }

}