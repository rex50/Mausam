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

class AdaptContent(private val gradientHelper: GradientHelper, private var model: GenericModelFactory?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var childClickListener: OnChildItemClickListener? = null
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

        model?.let { data ->

            when (holder) {

                is GeneralTypeHolder -> {
                    holder.bind(data.get(position), isDataSaverMode)
                    holder.setClickListener {
                        childClickListener?.onItemClick(
                            data,
                            holder.imageView,
                            position
                        )
                    }
                }

                is UserTypeHolder -> {
                    holder.bind(data.get(position), isDataSaverMode)
                    holder.setClickListener {
                        childClickListener?.onItemClick(
                            data,
                            null,
                            position
                        )
                    }
                }

                is ColorTypeHolder -> {
                    holder.bind(data.get(position))
                    holder.setClickListener {
                        childClickListener?.onItemClick(
                            data,
                            null,
                            position
                        )
                    }
                }

                is CollectionTypeHolder -> {
                    holder.bind(data.get(position), isDataSaverMode)
                    holder.setClickListener {
                        childClickListener?.onItemClick(
                            data,
                            null,
                            position
                        )
                    }
                }

                is TextTypeHolder -> {
                    holder.bind(data.get<Tag>(position))
                    holder.setClickListener {
                        childClickListener?.onItemClick(
                            data,
                            null,
                            position
                        )
                    }
                }

                is CategoryTypeHolder -> {
                    holder.bind(data.get(position), gradientHelper.getRandomLeftRightGradient())
                    holder.setClickListener {
                        childClickListener?.onItemClick(
                            data,
                            null,
                            position
                        )
                    }
                }

                is FavPhotographerPhotosTypeHolder -> {
                    holder.bind(data.get(position), isDataSaverMode)
                    holder.setClickListener {
                        childClickListener?.onItemClick(
                            data,
                            holder.imageView,
                            position
                        )
                    }
                }

                else -> throw IllegalArgumentException("Code missing for ${data.childType}")

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
        model = data
        val tempOldList = mutableListOf<Any>().also { it.addAll(oldList) }
        oldList.clear()
        oldList.addAll(data.getList())
        DiffUtil.calculateDiff(
            AdaptContentDiffUtil(
                tempOldList,
                data.getList()
            )
        ).dispatchUpdatesTo(this@AdaptContent)
    }

}