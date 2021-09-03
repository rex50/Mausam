package com.rex50.mausam.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.interfaces.OnGroupItemClickListener
import com.rex50.mausam.model_classes.utils.AllContentModel
import com.rex50.mausam.utils.Constants.RecyclerItemLayouts
import com.rex50.mausam.utils.Constants.RecyclerItemTypes
import com.rex50.mausam.utils.GradientHelper
import com.rex50.mausam.views.adapters.holders.EndImageHolder
import com.rex50.mausam.views.adapters.holders.ItemCategoryHolder
import com.rex50.mausam.views.adapters.holders.ItemSectionHolder

class AdaptHome(private var gradientHelper: GradientHelper, private var allContentModel: AllContentModel?) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var itemClickListener: OnGroupItemClickListener? = null

    fun updateData(data: AllContentModel) {
        allContentModel = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, groupLayout: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(groupLayout, parent, false)
        return when (groupLayout) {

            RecyclerItemLayouts.GROUP_SECTION_LAYOUT -> {
                ItemSectionHolder(v)
            }

            //also when RecyclerItemLayouts.FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_LAYOUT
            RecyclerItemLayouts.GROUP_CATEGORY_LAYOUT -> {
                ItemCategoryHolder(v)
            }

            RecyclerItemTypes.END_IMAGE -> {
                EndImageHolder(v)
            }

            else -> throw IllegalArgumentException("Please add case for viewType:\"$groupLayout\"")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position < allContentModel?.size() ?: 0) {
            allContentModel?.getModel(position)?.apply {
                when (groupType) {
                    RecyclerItemTypes.GROUP_CATEGORY_TYPE -> {
                        val itemHolder = holder as ItemCategoryHolder
                        itemHolder.bind(
                            this,
                            gradientHelper,
                            itemClickListener,
                            1,
                            position
                        )
                    }

                    RecyclerItemTypes.GROUP_SECTION_TYPE -> {
                        val itemHolder = holder as ItemSectionHolder
                        itemHolder.bind(
                            this,
                            gradientHelper,
                            itemClickListener,
                            1,
                            position
                        )
                    }

                    RecyclerItemTypes.FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_TYPE -> {
                        val itemHolder = holder as ItemCategoryHolder
                        itemHolder.bind(
                            this,
                            gradientHelper,
                            itemClickListener,
                            2,
                            position
                        )
                    }

                    RecyclerItemTypes.END_IMAGE -> {
                        val itemHolder = holder as EndImageHolder
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return allContentModel?.getModel(position)?.groupLayout ?: RecyclerItemTypes.GROUP_CATEGORY_TYPE
    }

    override fun getItemCount(): Int {
        return allContentModel?.size() ?: 0
    }
}