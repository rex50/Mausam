package com.rex50.mausam.views.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.master.permissionhelper.PermissionHelper
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseFragment
import com.rex50.mausam.utils.MaterialSnackBar
import com.rex50.mausam.utils.isStoragePermissionGranted
import com.rex50.mausam.utils.showToast
import com.rex50.mausam.MausamApplication
import kotlinx.android.synthetic.main.frag_storage_perm.*

class FragStoragePerm : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = FragStoragePerm()
        const val STORAGE_REQUEST_CODE = 102
    }

    private var listener: OnFragmentInteractionListener? = null

    private val permissionHandler: PermissionHelper by lazy {
        PermissionHelper(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
            200
        )
    }

    override fun getResourceLayout(): Int = R.layout.frag_storage_perm

    override fun initView() {
        sStoragePerm?.setOnClickListener {
            permissionHandler.requestAll {
                requestStoragePermission()
            }
        }

        permissionHandler.denied { systemDenied ->
            if(systemDenied) {
                listener?.materialSnackBar()?.showActionSnackBar(
                    getString(R.string.storage_perm_denied_msg),
                    getString(R.string.settings),
                    MaterialSnackBar.LENGTH_INDEFINITE,
                    object : MaterialSnackBar.SnackBarListener{
                        override fun onActionPressed() {
                            permissionHandler.openAppDetailsActivity()
                        }
                    })
            } else {
                listener?.materialSnackBar()?.showActionSnackBar(
                        getString(R.string.download_feature_not_possible_error_msg),
                        getString(R.string.ok_caps),
                        MaterialSnackBar.LENGTH_INDEFINITE,
                        object : MaterialSnackBar.SnackBarListener{
                            override fun onActionPressed() {
                                listener?.materialSnackBar()?.dismiss()
                                requestStoragePermission()
                            }
                        })
            }
        }
    }

    override fun load() {

    }

    private fun requestStoragePermission() {

        permissionHandler.requestAll {
            sStoragePerm?.setChecked(true)
            listener?.storagePermGranted()
        }

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    interface OnFragmentInteractionListener{
        fun materialSnackBar() : MaterialSnackBar?
        fun storagePermGranted()
        fun permStoragePermanentlyDenied()
    }

}