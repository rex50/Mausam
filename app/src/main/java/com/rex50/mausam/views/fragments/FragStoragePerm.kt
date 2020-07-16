package com.rex50.mausam.views.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseFragment
import com.rex50.mausam.utils.MaterialSnackBar
import com.rex50.mausam.utils.isStoragePermissionGranted
import com.rex50.mausam.utils.showToast
import com.rex50.mausam.views.MausamApplication
import kotlinx.android.synthetic.main.frag_storage_perm.*

class FragStoragePerm : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = FragStoragePerm()
        const val STORAGE_REQUEST_CODE = 102
    }

    private var listener: OnFragmentInteractionListener? = null

    override fun getResourceLayout(): Int = R.layout.frag_storage_perm

    override fun initView() {
        sStoragePerm?.setOnClickListener {
            requestStoragePermission()
        }
    }

    override fun load() {

    }

    private fun requestStoragePermission() {
        MausamApplication.getInstance()?.getSharedPrefs()?.let {
            if (!it.isStoragePermanentlyDenied) {
                // request the storage permission
                mContext?.apply {
                    when {
                        isStoragePermissionGranted() -> {
                            sStoragePerm?.setChecked(true)
                            listener?.storagePermGranted()
                        }
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                                    STORAGE_REQUEST_CODE)
                        }
                        else -> {
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                                    STORAGE_REQUEST_CODE)
                        }
                    }
                }
            } else {
                listener?.materialSnackBar()?.showActionSnackBar(
                        getString(R.string.permission_denied_earlier_msg),
                        getString(R.string.ok_caps),
                        MaterialSnackBar.LENGTH_INDEFINITE,
                        object : MaterialSnackBar.SnackBarListener{
                            override fun onActionPressed() {
                                listener?.materialSnackBar()?.dismiss()
                            }
                        })
            }
        }?: showToast(getString(R.string.something_wrong_msg))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sStoragePerm?.setChecked(true)
                listener?.storagePermGranted()
            } else {
                mContext?.apply {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        listener?.materialSnackBar()?.showActionSnackBar(
                                getString(R.string.download_feature_not_possible_error_msg),
                                getString(R.string.ok_caps),
                                MaterialSnackBar.LENGTH_INDEFINITE
                                , object : MaterialSnackBar.SnackBarListener{
                            override fun onActionPressed() {
                                listener?.materialSnackBar()?.dismiss()
                            }
                        })
                        sStoragePerm?.setChecked(false)
                    } else {
                        sStoragePerm?.apply {
                            isEnabled = false
                            setChecked(false)
                        }
                        listener?.permStoragePermanentlyDenied()
                    }

                } ?: showToast(getString(R.string.something_wrong_msg))
            }
        }

    }

    interface OnFragmentInteractionListener{
        fun materialSnackBar() : MaterialSnackBar?
        fun storagePermGranted()
        fun permStoragePermanentlyDenied()
    }

}