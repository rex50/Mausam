package com.rex50.mausam.views.adapters.holders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.interfaces.OnChildItemClickListener
import com.rex50.mausam.interfaces.OnGroupItemClickListener
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.utils.ItemOffsetDecoration
import com.rex50.mausam.utils.hideView
import com.rex50.mausam.views.adapters.AdaptContent
import com.thekhaeng.pushdownanim.PushDownAnim
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import kotlinx.android.synthetic.main.header_custom_section.view.*
import kotlinx.android.synthetic.main.item_section.view.*

class ItemSectionHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

    fun bind(
        model: GenericModelFactory?,
        groupItemClickListener: OnGroupItemClickListener?,
        spanCount: Int,
        groupPosition: Int
    ) = with(itemView) {
        model?.apply {

            ivSectionIcon?.setImageResource(model.icon)

            tvSectionTitle?.text = model.title

            btnSeeAll?.apply {
                setText(R.string.see_all)
                PushDownAnim.setPushDownAnimTo(this).setOnClickListener {
                    //TODO: Start All downloads page
                    groupItemClickListener?.onMoreClicked(model, model.title, groupPosition)
                }
                hideView()
            }

            recSectionItems?.apply {

                layoutManager = GridLayoutManager(context, spanCount, scrollDirection, false)

                if(itemDecorationCount == 0){
                    addItemDecoration(ItemOffsetDecoration(context, R.dimen.recycler_item_offset_grid))
                }

                val secAdapter = AdaptContent(context, model)
                secAdapter.setChildClickListener(object : OnChildItemClickListener {
                    override fun onItemClick(o: Any?, childImgView: ImageView?, childPos: Int) {
                        groupItemClickListener?.onItemClick(o, childImgView, groupPosition, childPos)
                    }
                })
                adapter = SlideInBottomAnimationAdapter(secAdapter).apply {
                    setFirstOnly(false)
                }
            }

            /*gradient?.apply {
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
            }*/
        }
    }

}
