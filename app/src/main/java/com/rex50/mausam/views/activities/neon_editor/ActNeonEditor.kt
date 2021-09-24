package com.rex50.mausam.views.activities.neon_editor

import android.os.Bundle
import androidx.core.net.toUri
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivityWithBinding
import com.rex50.mausam.databinding.ActNeonEditorBinding
import com.rex50.mausam.network.Status
import com.rex50.mausam.utils.hideView
import com.rex50.mausam.utils.showView
import ja.burhanrashid52.photoeditor.PhotoEditor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class ActNeonEditor: BaseActivityWithBinding<ActNeonEditorBinding>() {

    private val viewModel by viewModel<ActNeonEditorViewModel>()

    private val editor: PhotoEditor by lazy {
        PhotoEditor.Builder(this, binding?.photoEditorView).build()
    }

    override fun bindView(): ActNeonEditorBinding = ActNeonEditorBinding.inflate(layoutInflater)

    override fun loadAct(savedInstanceState: Bundle?) {

        initClicks()

        setupObservers()

        fetchARandomPhoto()

    }

    private fun initClicks() {
        binding?.btnBrush?.setOnClickListener {
            editor.setBrushDrawingMode(true)
        }

        binding?.btnEraser?.setOnClickListener {
            editor.setBrushEraserSize(20f)
            editor.brushEraser()
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
                        binding?.photoEditorView?.source?.setImageURI(data?.relativePath?.toUri())
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