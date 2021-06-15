package com.rex50.mausam.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rex50.mausam.R
import com.rex50.mausam.interfaces.OnUsedLibrariesAdapterListener
import com.rex50.mausam.model_classes.utils.Library
import com.rex50.mausam.utils.GradientHelper
import com.thekhaeng.pushdownanim.PushDownAnim

class AdaptUsedLibrary(private val libraries: ArrayList<Library>) : RecyclerView.Adapter<AdaptUsedLibrary.UsedLibraryViewHolder>() {


    var listener :OnUsedLibrariesAdapterListener? = null

    inner class UsedLibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val cardView: CardView? = itemView.findViewById(R.id.cvUsedLibrary)
        private val title : TextView? = itemView.findViewById(R.id.tvTitle)
        private val des : TextView? = itemView.findViewById(R.id.tvDec)
        private val overlay: View? = itemView.findViewById(R.id.vBgOverlay)

        fun bind(lib: Library) {
            title?.text = lib.name
            des?.text = lib.desc
            overlay?.background = lib.color
            //cardView?.background = lib.color
        }

        fun setClickListener(listener: View.OnClickListener?) {
            cardView?.apply {
                PushDownAnim.setPushDownAnimTo(this)
                    .setOnClickListener(listener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsedLibraryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_type_used_library,parent, false)
        return UsedLibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsedLibraryViewHolder, position: Int) {
        holder.bind(libraries[position])
        holder.setClickListener {
            listener?.onClickLibrariesMaterial(libraries[position].link)
        }
    }

    override fun getItemCount(): Int = libraries.size
}