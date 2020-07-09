package com.rex50.mausam.views.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.rex50.mausam.R
import com.rex50.mausam.interfaces.OnChildItemClickListener
import com.rex50.mausam.model_classes.unsplash.collection.Collections
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.utils.GenericModelFactory
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.RecyclerItemTypes
import com.rex50.mausam.views.MausamApplication
import com.thekhaeng.pushdownanim.PushDownAnim
import org.apache.commons.lang3.StringUtils

class AdaptContent(private var context: Context?, private var model: GenericModelFactory?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var childClickListener: OnChildItemClickListener? = null
    private var gradientHelper = GradientHelper.getInstance(context)
    private val isDataSaverMode = MausamApplication.getInstance()?.getSharedPrefs()?.isDataSaverMode ?: false

    fun setChildClickListener(childClickListener: OnChildItemClickListener?) {
        this.childClickListener = childClickListener
    }

    inner class GeneralTypeHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView? = itemView.findViewById(R.id.ivPhoto)
        private val cardView: CardView? = itemView.findViewById(R.id.cardView)

        fun bind(photo: UnsplashPhotos?) {
            imageView?.loadImage(photo?.urls?.getSmall(isDataSaverMode))
        }

        fun setClickListener(listener: View.OnClickListener?) {
            cardView?.apply {
                PushDownAnim.setPushDownAnimTo(this)
                        .setOnClickListener(listener)
            }
        }

    }

    inner class FavPhotographerPhotosTypeHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView? = itemView.findViewById(R.id.ivPhoto)
        private val cardView: CardView? = itemView.findViewById(R.id.cardView)
        fun bind(photo: UnsplashPhotos?) {
            imageView?.loadImage(photo?.urls?.getSmall(isDataSaverMode))
        }

        fun setClickListener(listener: View.OnClickListener?) {
            cardView?.apply {
                PushDownAnim.setPushDownAnimTo(this)
                        .setOnClickListener(listener)
            }
        }

    }

    inner class UserTypeHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
        private var userImg: ImageView? = v.findViewById(R.id.ivUser)
        private var txtUserName: TextView? = v.findViewById(R.id.tvUserName)
        private var userLayout: LinearLayout? = v.findViewById(R.id.lnlUser)

        fun bind(userModel: User?) {
            val name = userModel?.firstName.getTextOrEmpty() + " " + userModel?.lastName.getTextOrEmpty()

            txtUserName?.text = name

            userImg?.loadImage(userModel?.profileImage?.getLarge(isDataSaverMode))
        }

        fun setClickListener(listener: View.OnClickListener?) {
            userLayout?.apply {
                PushDownAnim.setPushDownAnimTo(this)
                        .setOnClickListener(listener)
            }
        }

    }

    inner class ColorTypeHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
        private var cardColor: View? = v.findViewById(R.id.vBgColor)
        private var txtColorName: TextView? = v.findViewById(R.id.tvColorName)
        private var cardView: CardView? = v.findViewById(R.id.cardView)
        fun bind(colorModel: GenericModelFactory.ColorModel?) {
            cardColor?.setBackgroundColor(Color.parseColor(colorModel?.colorCode))

            txtColorName?.text = colorModel?.colorName
        }

        fun setClickListener(listener: View.OnClickListener?) {
            cardView?.apply {
                PushDownAnim.setPushDownAnimTo(this)
                        .setOnClickListener(listener)
            }
        }

    }

    inner class CollectionTypeHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imgMain: ImageView? = itemView.findViewById(R.id.ivPhotoCover)
        private var imgPreview1: ImageView? = itemView.findViewById(R.id.ivPhotoPreview1)
        private var imgPreview2: ImageView? = itemView.findViewById(R.id.ivPhotoPreview2)
        private var imgPreview3: ImageView? = itemView.findViewById(R.id.ivPhotoPreview3)
        private var txtTitle: TextView? = itemView.findViewById(R.id.tvCollectionTitle)
        private var txtDesc: TextView? = itemView.findViewById(R.id.tvCollectionDesc)
        private var cardView: CardView? = itemView.findViewById(R.id.cardView)
        fun bind(collection: Collections?) {
            collection?.let {
                txtTitle?.text = StringUtils.capitalize(it.title?.trim())

                txtDesc?.text = it.description?.trim()

                imgMain?.takeIf { collection.coverPhoto.isNotNull() }?.loadImage(collection.coverPhoto?.urls?.getSmall(isDataSaverMode))

                imgPreview1?.takeIf { collection.previewPhotos?.size ?: 0 > 0 }?.loadImage(collection.previewPhotos[0]?.urls?.getSmall(isDataSaverMode))
                        ?: (imgPreview1?.parent as View?)?.hideView()

                imgPreview2.takeIf { collection.previewPhotos?.size ?: 0 > 1 }?.loadImage(collection.previewPhotos[1]?.urls?.getSmall(isDataSaverMode))
                        ?: (imgPreview2?.parent as View?)?.hideView()

                imgPreview3?.takeIf { collection.previewPhotos?.size ?: 0 > 2 }?.loadImage(collection.previewPhotos[2]?.urls?.getSmall(isDataSaverMode))
                        ?: (imgPreview3?.parent as View?)?.hideView()
            }
        }

        fun setClickListener(listener: View.OnClickListener?) {
            cardView?.apply {
                PushDownAnim.setPushDownAnimTo(this)
                        .setOnClickListener(listener)
            }
        }

    }

    inner class TextTypeHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var btnTag: Button? = itemView.findViewById(R.id.btnTag)
        fun bind(title: String?) {
            btnTag?.text = title.toString()
        }

        fun setClickListener(listener: View.OnClickListener?) {
            btnTag?.apply {
                PushDownAnim.setPushDownAnimTo(this)
                        .setOnClickListener(listener)
            }
        }

    }

    inner class CategoryTypeHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryName: TextView? = itemView.findViewById(R.id.tvCategoryName)
        private val cardView = itemView.findViewById<CardView>(R.id.cardView)
        private val ivCategory = itemView.findViewById<ImageView>(R.id.ivCategory)
        private val vBgOverlay = itemView.findViewById<View>(R.id.vBgOverlay)
        fun bind(model: GenericModelFactory.CategoryModel?) {
            categoryName?.text = model?.categoryName?.toString()

            vBgOverlay.background = gradientHelper?.getRandomLeftRightGradient()

            ivCategory?.loadImage(model?.categoryImg)
        }

        fun setClickListener(listener: View.OnClickListener?) {
            cardView?.apply {
                PushDownAnim.setPushDownAnimTo(this)
                        .setOnClickListener(listener)
            }
        }

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
                    generalHolder.bind(generalTypeModel?.photosList?.get(position))
                    generalHolder.setClickListener(View.OnClickListener { childClickListener?.onItemClick(model?.generalTypeModel, generalHolder.imageView, position) })
                }
                RecyclerItemTypes.USER_TYPE -> {
                    val userHolder = holder as UserTypeHolder
                    userHolder.bind(userTypeModel?.usersList?.get(position))
                    userHolder.setClickListener(View.OnClickListener { childClickListener?.onItemClick(model?.userTypeModel, null, position) })
                }
                RecyclerItemTypes.COLOR_TYPE -> {
                    val colorHolder = holder as ColorTypeHolder
                    colorHolder.bind(colorTypeModel?.colorsList?.get(position))
                    colorHolder.setClickListener(View.OnClickListener { childClickListener?.onItemClick(model?.colorTypeModel, null, position) })
                }
                RecyclerItemTypes.COLLECTION_TYPE, RecyclerItemTypes.COLLECTION_LIST_TYPE -> {
                    val collectionHolder = holder as CollectionTypeHolder
                    collectionHolder.bind(collectionTypeModel?.collections?.get(position))
                    collectionHolder.setClickListener(View.OnClickListener { childClickListener?.onItemClick(model?.collectionTypeModel, null, position) })
                }
                RecyclerItemTypes.TAG_TYPE -> {
                    val textHolder = holder as TextTypeHolder
                    textHolder.bind(tagTypeModel.tagsList?.get(position)?.title)
                    textHolder.setClickListener(View.OnClickListener { childClickListener?.onItemClick(model?.tagTypeModel, null, position) })
                }
                RecyclerItemTypes.CATEGORY_TYPE -> {
                    val categoryHolder = holder as CategoryTypeHolder
                    categoryHolder.bind(categoryTypeModel?.categories?.get(position))
                    categoryHolder.setClickListener(View.OnClickListener { childClickListener?.onItemClick(model?.categoryTypeModel, null, position) })
                }
                RecyclerItemTypes.FAV_PHOTOGRAPHER_PHOTOS_TYPE -> {
                    val favHolder = holder as FavPhotographerPhotosTypeHolder
                    favHolder.bind(favouritePhotographerTypeModel?.photosList?.get(position))
                    favHolder.setClickListener(View.OnClickListener { childClickListener?.onItemClick(model?.favouritePhotographerTypeModel, favHolder.imageView, position) })
                }
                else -> throw IllegalArgumentException("Code missing for $itemType")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return model?.itemLayout ?: R.layout.cell_type_general
    }

    override fun getItemCount(): Int {
        return totalItems()
    }

    private fun totalItems(): Int {
        return model?.let {
            when (it.itemType) {
                RecyclerItemTypes.GENERAL_TYPE -> it.generalTypeModel?.photosList?.size ?: 0
                RecyclerItemTypes.COLLECTION_TYPE, RecyclerItemTypes.COLLECTION_LIST_TYPE -> it.collectionTypeModel?.collections?.size ?: 0
                RecyclerItemTypes.COLOR_TYPE -> it.colorTypeModel?.colorsList?.size ?: 0
                RecyclerItemTypes.USER_TYPE -> it.userTypeModel?.usersList?.size ?: 0
                RecyclerItemTypes.TAG_TYPE -> it.tagTypeModel?.tagsList?.size ?: 0
                RecyclerItemTypes.CATEGORY_TYPE -> it.categoryTypeModel?.categories?.size ?: 0
                RecyclerItemTypes.FAV_PHOTOGRAPHER_PHOTOS_TYPE -> it.favouritePhotographerTypeModel?.photosList?.size ?: 0
                else -> throw IllegalArgumentException("Please add case for ItemType : \"" + model?.itemType + "\"")
            }
        }?: 0
    }
}