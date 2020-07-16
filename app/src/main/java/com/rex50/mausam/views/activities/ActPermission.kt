package com.rex50.mausam.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivity
import com.rex50.mausam.utils.MaterialSnackBar
import kotlinx.android.synthetic.main.act_permission.*

class ActPermission : BaseActivity(), View.OnClickListener {
    private val GPS_REQUEST = 99
    private val LOCATION_REQUEST_CODE = 101
    private val STORAGE_REQUEST_CODE = 102


    override val layoutResource: Int
        get() = R.layout.act_permission


    override fun loadAct(savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        if (isLocationPermissionGranted) switchLocPermission?.isChecked = true
        if (isStoragePermissionGranted) switchStoragePermission?.isChecked = true
        if (isBothPermissionGranted) {
            startActivity(Intent(this, ActMain::class.java))
        }
        switchLocPermission?.setOnClickListener(this)
        switchStoragePermission?.setOnClickListener(this)
        btnSkipPermission?.setOnClickListener(this)
    }

    private fun requestLocationPermission() {
        mausamSharedPrefs?.let {
            // request the location permission
            if (!it.isLocationPermanentlyDenied) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            LOCATION_REQUEST_CODE)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                            LOCATION_REQUEST_CODE)
                }
            } else {
                materialSnackBar?.showActionSnackBar(
                        "You have denied this permission. You can allow this permission from settings",
                        "OK",
                        MaterialSnackBar.LENGTH_INDEFINITE,
                        object : MaterialSnackBar.SnackBarListener{
                    override fun onActionPressed() {
                        materialSnackBar?.dismiss()
                    }
                })
            }
        }
    }

    private fun requestStoragePermission() {
        mausamSharedPrefs?.let {
            if (!it.isStoragePermanentlyDenied) {
                // request the storage permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                            STORAGE_REQUEST_CODE)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                            STORAGE_REQUEST_CODE)
                }
            } else {
                materialSnackBar?.showActionSnackBar(
                        "You have denied this permission. You can allow this permission from settings",
                        "OK",
                        MaterialSnackBar.LENGTH_INDEFINITE,
                        object : MaterialSnackBar.SnackBarListener{
                    override fun onActionPressed() {
                        materialSnackBar?.dismiss()
                    }
                })
            }
        }
    }



    override fun internetStatus(internetType: Int) {}
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switchLocPermission?.isChecked = true
                mausamSharedPrefs?.apply {
                    isLocationPermissionSkipped = false
                    if (isBothPermissionGranted || isStoragePermanentlyDenied) {
                        btnSkipPermission?.visibility = View.GONE
                        materialSnackBar?.show("Awesome! all permission are granted.", MaterialSnackBar.LENGTH_SHORT)
                        startActivity(Intent(this@ActPermission, ActMain::class.java))
                        finish()
                    } else {
                        materialSnackBar?.show("Good, one more permission to go", MaterialSnackBar.LENGTH_SHORT)
                    }
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                    materialSnackBar?.showActionSnackBar(
                            "Location can't be detected automatically without this permission. So please allow Mausam this permission.",
                            "OK",
                            MaterialSnackBar.LENGTH_INDEFINITE,
                            object : MaterialSnackBar.SnackBarListener{
                        override fun onActionPressed() {
                            materialSnackBar?.dismiss()
                        }
                    })
                    switchLocPermission?.isChecked = false
                } else {
                    switchLocPermission?.apply {
                        isChecked = false
                        isEnabled = false
                    }
                    mausamSharedPrefs?.isLocationPermanentlyDenied = true
                    materialSnackBar?.showActionSnackBar(
                            "You have denied this permission earlier but can allow it from settings",
                            "OK",
                            MaterialSnackBar.LENGTH_INDEFINITE
                            , object : MaterialSnackBar.SnackBarListener{
                        override fun onActionPressed() {
                            mausamSharedPrefs?.apply {
                                if (isStoragePermanentlyDenied) {
                                    //TODO : start SearchActivity
                                }
                            }
                            materialSnackBar?.dismiss()
                        }
                    })
                }
            }
        } else if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switchStoragePermission.isChecked = true
                mausamSharedPrefs?.apply {

                }
                mausamSharedPrefs?.apply {
                    isStoragePermissionSkipped = false
                    if (isBothPermissionGranted) {
                        btnSkipPermission.visibility = View.GONE
                        materialSnackBar?.show("Awesome! all permission are granted.", MaterialSnackBar.LENGTH_SHORT)
                        startActivity(Intent(this@ActPermission, ActMain::class.java))
                        finish()
                    } else if (isLocationPermanentlyDenied) {
                        //TODO : start search activity
                    } else {
                        materialSnackBar?.show("Good, one more permission to go", MaterialSnackBar.LENGTH_SHORT)
                    }
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    materialSnackBar?.showActionSnackBar(
                            getString(R.string.download_feature_not_possible_error_msg),
                            getString(R.string.ok_caps),
                            MaterialSnackBar.LENGTH_INDEFINITE
                            , object : MaterialSnackBar.SnackBarListener{
                        override fun onActionPressed() {
                            materialSnackBar?.dismiss()
                        }
                    })
                    switchStoragePermission?.isChecked = false
                } else {
                    switchStoragePermission?.apply {
                        isEnabled = false
                        isChecked = false
                    }
                    mausamSharedPrefs?.isStoragePermanentlyDenied = true
                    materialSnackBar?.showActionSnackBar(
                            "You have denied this permission earlier but can allow it from settings",
                            "OK",
                            MaterialSnackBar.LENGTH_INDEFINITE
                            , object : MaterialSnackBar.SnackBarListener{
                        override fun onActionPressed() {
                            materialSnackBar?.dismiss()
                            mausamSharedPrefs?.isLocationPermanentlyDenied?.apply {
                                if(this){
                                    //TODO : start SearchActivity
                                }
                            }
                        }
                    })
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private val isBothPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private val isLocationPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private val isStoragePermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    override fun onClick(view: View) {
        when (view.id) {
            R.id.switchLocPermission -> requestLocationPermission()
            R.id.switchStoragePermission -> requestStoragePermission()
            R.id.btnSkipPermission -> {
                mausamSharedPrefs?.apply {
                    if (!isStoragePermissionGranted && isStoragePermanentlyDenied)
                        isStoragePermissionSkipped = true
                    if (isLocationPermissionGranted) {

                    } else {
                        if (!isLocationPermanentlyDenied)
                            isLocationPermissionSkipped = true
                    }
                }
            }
        }
    }
}