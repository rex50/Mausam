package com.rex50.mausam.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.interfaces.OnChildItemClickListener
import com.rex50.mausam.model_classes.unsplash.collection.Tag
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.RecyclerItemTypes
import com.rex50.mausam.views.MausamApplication
import com.rex50.mausam.views.adapters.holders.*

class AdaptContent(private var context: Context?, private var model: GenericModelFactory?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var childClickListener: OnChildItemClickListener? = null
    private var gradientHelper = GradientHelper.getInstance(context)
    private val isDataSaverMode = MausamApplication.getInstance()?.getSharedPrefs()?.isDataSaverMode ?: false

    fun setChildClickListener(childClickListener: OnChildItemClickListener?) {
        this.childClickListener = childClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, layout: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(context).inflate(layout, parent, false)
        return when (layout) {
            RecyclerItemTypes.USER_TYPE -> UserTypeHolder(v)
            RecyclerItemTypes.COLOR_TYPE -> ColorTypeHolder(v)
            RecyclerItemTypes.GENERAL_TYPE -> GeneralTypeHolder(v)
            RecyclerItemTypes.COLLECTION_TYPE, RecyclerItemTypes.COLLECTION_LIST_TYPE -> CollectionTypeHolder(v)
            RecyclerItemTypes.TAG_TYPE -> TextTypeHolder(v)
            RecyclerItemTypes.CATEGORY_TYPE -> CategoryTypeHolder(v)
            RecyclerItemTypes.FAV_PHOTOGRAPHER_PHOTOS_TYPE -> FavPhotographerPhotosTypeHolder(v)
            else -> throw IllegalArgumentException("Inflate code for viewType:\"$layout\" is not found")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        model?.apply {
            when (itemType) {

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

                else -> throw IllegalArgumentException("Code missing for $itemType")

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return model?.itemLayout ?: R.layout.cell_type_general
    }

    override fun getItemCount(): Int {
        return model?.getTotalItems() ?: 0
    }

}