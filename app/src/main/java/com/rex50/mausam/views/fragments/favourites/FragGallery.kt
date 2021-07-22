package com.rex50.mausam.views.fragments.favourites

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseFragmentWithListener
import com.rex50.mausam.databinding.FragGalleryBinding
import com.rex50.mausam.enums.ContentLoadingState
import com.rex50.mausam.interfaces.OnGroupItemClickListener
import com.rex50.mausam.model_classes.item_types.GeneralTypeModel
import com.rex50.mausam.model_classes.item_types.UserTypeModel
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos
import com.rex50.mausam.model_classes.unsplash.photos.User
import com.rex50.mausam.model_classes.utils.AllContentModel
import com.rex50.mausam.model_classes.utils.MoreListData
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.utils.*
import com.rex50.mausam.utils.Constants.IntentConstants.PHOTO_DATA
import com.rex50.mausam.utils.GradientHelper
import com.rex50.mausam.views.activities.ActImageEditor
import com.rex50.mausam.views.adapters.AdaptHome
import com.rex50.mausam.views.bottomsheets.BSDeleteConfirmation
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel

class FragGallery : BaseFragmentWithListener<FragGalleryBinding, FragGallery.OnFragmentInteractionListener>() {

    private val viewModel by viewModel<FragGalleryViewModel>()

    private var imageViewer: ImageViewerHelper? = null

    private var adaptFav: AdaptHome? = null

    override fun bindView(inflater: LayoutInflater, container: ViewGroup?): FragGalleryBinding {
        return FragGalleryBinding.inflate(inflater, container, false)
    }

    override fun initView() {

        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        initHeader()

        initEmptyMessage(false)

        initRecycler()

        initClicks()
    }

    private fun initEmptyMessage(show: Boolean) {
        if(show) {
            binding?.animLayout?.apply {

                lnlError.showView()
                if(!animError.isAnimating) {
                    animError.apply {
                        setAnimation(R.raw.l_anim_error_astronaout)
                        scale = 0.2F
                        speed = 0.8F
                    }
                }
                tvError.text = getText(R.string.empty_download_section_msg)
                btnAction.apply {
                    text = getText(R.string.go_discover)
                    setOnClickListener {
                        listener?.navigateToDiscover()
                    }
                    showView()
                }
            }
        } else {
            binding?.animLayout?.lnlError?.hideView()
        }
    }

    private fun initHeader() {
        binding?.header?.apply {
            gradientLine.background = GradientHelper.getInstance(requireContext())?.getRandomLeftRightGradient()
            tvPageTitle.setText(R.string.favourites_title)
            tvPageDesc.setText(R.string.favourites_desc)
        }
    }

    override fun load() {
        setupContentObserver()
    }

    private fun initRecycler() {

        binding?.rvRecommendations?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AdaptHome(GradientHelper.getInstance(requireContext()), AllContentModel()).also {
                adaptFav = it
            }
        }

    }

    private fun setupContentObserver() {
        viewModel.loadingState.observe(this) { state ->
            Log.e("FragFav", "setupContentObserver: $state")
            when(state) {
                is ContentLoadingState.Preparing -> {
                    initEmptyMessage(false)
                    binding?.ivLoader?.showView()
                }

                is ContentLoadingState.Ready -> {
                    MainScope().launch {
                        delay(300)
                        binding?.rvRecommendations?.showView()
                        initEmptyMessage(false)
                        adaptFav?.updateData(state.data)
                        imageViewer?.let {
                            (state.data.getModel(Constants.AvailableLayouts.DOWNLOADED_PHOTOS)
                                    as GeneralTypeModel?)?.photosList?.let { list ->
                                if(list.isEmpty()) {
                                    it.dismiss()
                                    setEmptyState()
                                } else
                                    it.updateImages(list)
                            }
                        }
                        binding?.ivLoader?.hideView()
                    }
                }

                is ContentLoadingState.Empty -> {
                    setEmptyState()
                }

                is ContentLoadingState.NoInternet -> {
                    //Do nothing
                }
            }
        }
    }

    private fun setEmptyState() {
        binding?.ivLoader?.hideView()
        binding?.rvRecommendations?.hideView()
        imageViewer?.dismiss()
        initEmptyMessage(true)
    }

    private fun initClicks() {
        adaptFav?.itemClickListener = object: OnGroupItemClickListener {
            override fun onItemClick(
                o: Any?,
                childImgView: ImageView?,
                groupPos: Int,
                childPos: Int
            ) {

                object: GenericModelCastHelper(o) {

                    override fun onGeneralType(generalTypeModel: GeneralTypeModel) {

                        showImageViewer(generalTypeModel, childImgView, childPos)

                    }

                    override fun onUserType(userTypeModel: UserTypeModel) {

                        val errMsg = "Error while getting user's photos"

                        userTypeModel.usersList[childPos].let { user ->
                            listener?.startMorePhotosActivity(user.moreListData) ?: showToast(errMsg)
                        }
                    }
                }

            }

            override fun onMoreClicked(o: Any?, title: String?, groupPos: Int) {
                //TODO("Not yet implemented")
            }

        }
    }

    private fun showImageViewer(
        generalTypeModel: GeneralTypeModel,
        childImgView: ImageView?,
        childPos: Int
    ) {
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
                        BSDeleteConfirmation().show(childFragmentManager).onDelete = {
                            ImageActionHelper.deleteImage(
                                requireContext(),
                                photoInfo
                            ) { success ->
                                showToast(if (success) "Photo deleted successfully" else "Error while deleting photo")
                            }
                            it.dismissAllowingStateLoss()
                        }
                    }

                    override fun onUserPhotos(user: User) {
                        listener?.startMorePhotosActivity(user.moreListData)
                    }
                }
            )

        imageViewer?.show()
    }

    override fun onScrollToTop() {
        binding?.nsRecommendations?.smoothScrollTo(0, 0)
        binding?.ablRecommendations?.setExpanded(true, true)
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri?)
        val sharedPrefs: MausamSharedPrefs?
        fun snackBar(): MaterialSnackBar?
        fun startMorePhotosActivity(data: MoreListData)
        fun navigateToDiscover()
    }

}