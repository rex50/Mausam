package com.rex50.mausam.views.activities.neon_editor

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivityWithBinding
import com.rex50.mausam.databinding.ActNeonEditorBinding
import com.rex50.mausam.network.Status
import com.rex50.mausam.utils.hideView
import com.rex50.mausam.utils.showView
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class ActNeonEditor: BaseActivityWithBinding<ActNeonEditorBinding>() {

    private val viewModel by viewModel<ActNeonEditorViewModel>()

    /*private val editor: PhotoEditor by lazy {
        PhotoEditor.Builder(this, binding?.photoEditorView).build()
    }*/

    private lateinit var editorCallback: ActivityResultLauncher<Intent>

    override fun bindView(): ActNeonEditorBinding = ActNeonEditorBinding.inflate(layoutInflater)

    override fun loadAct(savedInstanceState: Bundle?) {

        initClicks()

        setupObservers()

        editorCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        }

        fetchARandomPhoto()

    }

    private fun initClicks() {
        binding?.btnBrush?.setOnClickListener {
            //editor.setBrushDrawingMode(true)
        }

        binding?.btnEraser?.setOnClickListener {
            /*editor.setBrushEraserSize(20f)
            editor.brushEraser()*/
        }
    }

    private fun setupObservers() = actScope.launch {
        viewModel.photoData.collect { photoStatus ->
            when(photoStatus) {
                is Status.Loading -> {
                    binding?.lv?.showView()
                }

                is Status.Success -> {
                    binding?.tvStatus?.text = "Starting download..."
                    viewModel.downloadImage(photoStatus.value) { data, msg ->
                        binding?.tvStatus?.text = ""
                        binding?.lv?.hideView()
                        ImageEditorIntentBuilder(this@ActNeonEditor, data?.relativePath, data?.relativePath+"-edited")
                            .withAddText()
                            .withPaintFeature()
                            .withFilterFeature()
                            .withRotateFeature()
                            .withCropFeature()
                            .withBrightnessFeature()
                            .withSaturationFeature()
                            .withBeautyFeature()
                            .withStickerFeature()
                            .forcePortrait(true)  // Add this to force portrait mode (It's set to false by default)
                            .setSupportActionBarVisibility(false) // To hide app's default action bar
                            .build().let { intent ->
                                //EditImageActivity.start, this, this@ActNeonEditor)
                                EditImageActivity.start(editorCallback, intent, this@ActNeonEditor)
                            }
                        //binding?.photoEditorView?.source?.setImageURI(data?.relativePath?.toUri())
                    }
                }

                is Status.Error -> {
                    binding?.lv?.hideView()
                    binding?.preview?.setImageResource(R.drawable.ic_refresh)
                }
            }
        }

    }

    private fun fetchARandomPhoto() {
        binding?.tvStatus?.text = "Getting photo details..."
        viewModel.getRandomPhoto()
    }

}