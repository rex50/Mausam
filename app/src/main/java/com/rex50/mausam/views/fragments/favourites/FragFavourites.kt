package com.rex50.mausam.views.fragments.favourites

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.WindowManager
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rex50.mausam.BuildConfig
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseFragment
import com.rex50.mausam.interfaces.OnGroupItemClickListener
import com.rex50.mausam.model_classes.item_types.GeneralTypeModel
import com.rex50.mausam.model_classes.item_types.UserTypeModel
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.utils.AllContentModel
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.IntentConstants.PHOTO_DATA
import com.rex50.mausam.utils.GradientHelper
import com.rex50.mausam.views.activities.ActImageEditor
import com.rex50.mausam.views.adapters.AdaptHome
import kotlinx.android.synthetic.main.header_custom_general.*
import kotlinx.android.synthetic.main.anim_view.*
import kotlinx.android.synthetic.main.frag_fav.*
import org.koin.android.viewmodel.ext.android.viewModel

class FragFavourites : BaseFragment() {

    private var mListener: OnFragmentInteractionListener? = null

    private val viewModel by viewModel<FragFavouritesViewModel>()

    private var imageViewer: ImageViewerHelper? = null

    private var adaptFav: AdaptHome? = null

    override fun getResourceLayout(): Int = R.layout.frag_fav

    override fun initView() {

        mContext?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        initHeader()

        initEmptyMessage(true)

    }

    private fun initEmptyMessage(show: Boolean) {
        if(show) {
            lnlError?.showView()
            if(animError?.isAnimating == false) {
                animError?.apply {
                    setAnimation(R.raw.l_anim_error_astronaout)
                    scale = 0.2F
                    speed = 0.8F
                }
            }
            tvError?.text = getText(R.string.empty_download_section_msg)
            btnAction?.apply {
                text = getText(R.string.go_discover)
                setOnClickListener {
                    mListener?.navigateToDiscover()
                }
                showView()
            }
        } else {
            lnlError?.hideView()
        }
    }

    private fun initHeader() {
        gradientLine?.background = GradientHelper.getInstance(mContext)?.getRandomLeftRightGradient()
        tvPageTitle?.setText(R.string.favourites_title)
        tvPageDesc?.setText(R.string.favourites_desc)
    }

    override fun load() {
        initRecycler()
    }

    private fun initRecycler() {

        rvRecommendations?.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.getSectionsLiveData()?.observe(this, { data ->
            if(data.size() > 0) {
                rvRecommendations?.showView()
                initEmptyMessage(false)
                if(adaptFav == null) {
                    initAdapter(data)
                } else {
                    adaptFav?.updateData(data)
                    imageViewer?.let {
                        (data.getModel(Constants.AvailableLayouts.DOWNLOADED_PHOTOS)
                                as GeneralTypeModel?)?.photosList?.toArrayList()?.let { list ->
                            if(list.isEmpty())
                                it.dismiss()
                            else
                                it.updateImages(list)
                        }
                    }
                }
            } else {
                rvRecommendations?.hideView()
                imageViewer?.dismiss()
                initEmptyMessage(true)
            }
        })
    }

    private fun initAdapter(data: AllContentModel) {
        adaptFav = AdaptHome(GradientHelper.getInstance(requireContext()), data)
        adaptFav?.itemClickListener = object: OnGroupItemClickListener {
            override fun onItemClick(
                o: Any?,
                childImgView: ImageView?,
                groupPos: Int,
                childPos: Int
            ) {

                object: GenericModelCastHelper(o) {

                    override fun onGeneralType(generalTypeModel: GeneralTypeModel) {
                        imageViewer = ImageViewerHelper(requireContext())
                            .showPhotographer(true)
                            .setTools(
                                arrayListOf(ImageViewerHelper.Tools.SET_WALLPAPER, ImageViewerHelper.Tools.SHARE_PHOTO,
                                    ImageViewerHelper.Tools.DELETE, ImageViewerHelper.Tools.MORE)
                            )
                            .with(
                                generalTypeModel.photosList,
                                childImgView,
                                childPos,
                                object : ImageActionHelper.ImageActionListener() {

                                    override fun onSetWallpaper(
                                        photoInfo: UnsplashPhotos,
                                        name: String
                                    ) {
                                        startActivity(
                                            Intent(
                                                requireContext(),
                                                ActImageEditor::class.java
                                            ).also {
                                                it.putExtra(PHOTO_DATA, photoInfo)
                                            })
                                    }

                                    override fun onShare(photoInfo: UnsplashPhotos, name: String) {
                                        ImageActionHelper.shareImage(
                                            requireContext(),
                                            "Share",
                                            photoInfo.user.name,
                                            photoInfo.links.html
                                        )
                                    }

                                    override fun onDelete(photoInfo: UnsplashPhotos) {
                                        ImageActionHelper.deleteImage(
                                            requireContext(),
                                            photoInfo
                                        ) { success ->
                                            showToast(if (success) "Photo deleted successfully" else "Error while deleting photo")
                                        }
                                    }

                                }
                            )

                        imageViewer?.show()
                    }

                    override fun onUserType(userTypeModel: UserTypeModel) {

                        val errMsg = "Error while getting user's photos"

                        userTypeModel.usersList[childPos].let { user ->
                            mListener?.startMorePhotosActivity(
                                MoreListData(
                                    Constants.ListModes.LIST_MODE_USER_PHOTOS,
                                    user
                                )
                            ) ?: showToast(errMsg)
                        }
                    }
                }

            }

            override fun onMoreClicked(o: Any?, title: String?, groupPos: Int) {
                //TODO("Not yet implemented")
            }

        }

        rvRecommendations?.adapter = adaptFav
    }

    override fun onScrollToTop() {
        nsRecommendations?.smoothScrollTo(0, 0)
        ablRecommendations?.setExpanded(true, true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else if(BuildConfig.DEBUG) {
            throw RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri?)
        val sharedPrefs: MausamSharedPrefs?
        val snackBar: MaterialSnackBar?
        fun startMorePhotosActivity(data: MoreListData)
        fun navigateToDiscover()
    }

}