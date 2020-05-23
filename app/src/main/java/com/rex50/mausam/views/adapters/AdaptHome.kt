package com.rex50.mausam.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.interfaces.OnChildItemClickListener
import com.rex50.mausam.interfaces.OnGroupItemClickListener
import com.rex50.mausam.model_classes.utils.AllContentModel
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.utils.Constants.RecyclerItemTypes

class AdaptHome(private var context: Context?, private var allContentModel: AllContentModel?) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var groupItemClickListener: OnGroupItemClickListener? = null

    class EndImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.wallpaper_img)
    }

    class ItemCategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var txtTitle: TextView = itemView.findViewById(R.id.txt_category_title)
        private var txtDesc: TextView = itemView.findViewById(R.id.txt_category_desc)
        private var btnMore: Button = itemView.findViewById(R.id.btn_category_more)
        private var contentRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_category_items)

        fun bind(context: Context?, model: GenericModelFactory?, groupItemClickListener: OnGroupItemClickListener?, spanCount: Int, groupPosition: Int) {
            model?.apply {
                txtTitle.text = model.title
                txtDesc.text = model.desc
                btnMore.visibility = if (model.isHasMore) View.VISIBLE else View.GONE
                //TODO : try not to create adapter here
                val adapter = AdaptContent(context, model)
                contentRecyclerView.layoutManager = GridLayoutManager(context, spanCount, scrollDirection, false)
                adapter.setChildClickListener(OnChildItemClickListener { o, childImgView, childPos ->
                    groupItemClickListener?.onItemClick(o, childImgView, groupPosition, childPos)
                })
                contentRecyclerView.adapter = adapter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(context).inflate(viewType, parent, false)
        return when (viewType) {
            RecyclerItemTypes.FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_TYPE, RecyclerItemTypes.ITEM_CATEGORY_TYPE -> ItemCategoryHolder(v)
            RecyclerItemTypes.END_IMAGE -> EndImageHolder(v)
            else -> throw IllegalArgumentException("Please add case for viewType:\"$viewType\"")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        allContentModel?.getModel(position)?.apply {
            when(viewType){
                RecyclerItemTypes.ITEM_CATEGORY_TYPE -> {
                    val itemHolder = holder as ItemCategoryHolder
                    itemHolder.bind(context, this, groupItemClickListener, 1, position)
                }

                RecyclerItemTypes.FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_TYPE -> {
                    val itemHolder = holder as ItemCategoryHolder
                    itemHolder.bind(context, this, groupItemClickListener, 2, position)
                }

                RecyclerItemTypes.END_IMAGE -> {
                    val itemHolder = holder as EndImageHolder
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return allContentModel?.getModel(position)?.viewLayout ?: RecyclerItemTypes.ITEM_CATEGORY_TYPE
    }

    override fun getItemCount(): Int {
        return allContentModel?.size() ?: 0
    }
}