package com.rex50.mausam.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.interfaces.OnGroupItemClickListener
import com.rex50.mausam.model_classes.utils.AllContentModel
import com.rex50.mausam.utils.Constants.RecyclerItemTypes
import com.rex50.mausam.utils.GradientHelper
import com.rex50.mausam.views.adapters.holders.EndImageHolder
import com.rex50.mausam.views.adapters.holders.ItemCategoryHolder

class AdaptHome(private var gradientHelper: GradientHelper?, private var allContentModel: AllContentModel?) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var itemClickListener: OnGroupItemClickListener? = null

    class EndImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.ivPhoto)
    }

    class ItemCategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(context: Context?, model: GenericModelFactory?, gradient: GradientDrawable?, groupItemClickListener: OnGroupItemClickListener?, spanCount: Int, groupPosition: Int) = with(itemView) {
            model?.apply {
                gradient?.apply {
                    vGradientLineCategory?.background = this
                }
                tvCategoryTitle?.text = model.title
                tvCategoryDesc?.text = model.desc
                btnCategoryMore?.apply {
                    if (model.isHasMore) showView() else hideView()
                    setOnClickListener { groupItemClickListener?.onMoreClicked(model, model.title, groupPosition) }
                }

                //TODO : Check if any other optimal solution is available
                val adapter: AdaptContent? = AdaptContent(context, model)
                recCategoryItems?.layoutManager = GridLayoutManager(context, spanCount, scrollDirection, false)
                adapter?.setChildClickListener(object : OnChildItemClickListener {
                    override fun onItemClick(o: Any?, childImgView: ImageView?, childPos: Int) {
                        groupItemClickListener?.onItemClick(o, childImgView, groupPosition, childPos)
                    }
                })
                if(recCategoryItems?.itemDecorationCount == 0){
                    recCategoryItems?.addItemDecoration(ItemOffsetDecoration(context, R.dimen.recycler_item_offset_grid))
                }

                recCategoryItems?.adapter =  SlideInBottomAnimationAdapter(adapter!!).apply {
                    setFirstOnly(false)
                }
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
                    itemHolder.bind(this, gradientHelper?.getGradient(), itemClickListener, 1, position)
                }

                RecyclerItemTypes.FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_TYPE -> {
                    val itemHolder = holder as ItemCategoryHolder
                    itemHolder.bind(this, gradientHelper?.getGradient(), itemClickListener, 2, position)
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