package com.rex50.mausam.views.adapters.holders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.interfaces.OnChildItemClickListener
import com.rex50.mausam.interfaces.OnGroupItemClickListener
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.utils.*
import com.rex50.mausam.views.adapters.AdaptContent
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.header_custom_category.view.*
import kotlinx.android.synthetic.main.item_category.view.*

class ItemCategoryHolder(itemView: View, private val sharedPool: RecyclerView.RecycledViewPool) : RecyclerView.ViewHolder(itemView) {

    fun bind(
        model: GenericModelFactory?,
        gradientHelper: GradientHelper,
        groupItemClickListener: OnGroupItemClickListener?,
        spanCount: Int,
        groupPosition: Int
    ) = with(itemView) {
        model?.apply {
            vGradientLineCategory?.background = gradientHelper.getGradient()
            tvCategoryTitle?.text = model.title
            tvCategoryDesc?.text = model.desc
            btnCategoryMore?.apply {
                if (model.isHasMore) showView() else hideView()
                PushDownAnim.setPushDownAnimTo(this).setOnClickListener {
                    groupItemClickListener?.onMoreClicked(model, model.title, groupPosition)
                }
            }

            recCategoryItems?.apply {

                layoutManager = GridLayoutManager(context, spanCount, scrollDirection, false)

                setRecycledViewPool(sharedPool)

                isNestedScrollingEnabled = false

                val adaptContent = AdaptContent(gradientHelper, model)
                adaptContent.setChildClickListener(object : OnChildItemClickListener {
                    override fun onItemClick(o: Any?, childImgView: ImageView?, childPos: Int) {
                        groupItemClickListener?.onItemClick(o, childImgView, groupPosition, childPos)
                    }
                })

                if(itemDecorationCount == 0){
                    recCategoryItems?.addItemDecoration(ItemOffsetDecoration(context, R.dimen.recycler_item_offset_grid))
                }

                adapter = adaptContent

                //For bounce effect
                edgeEffectFactory = RecyclerEdgeEffect()
            }
        }
    }
}