package com.rex50.mausam.views.activities.auto_wallpaper

import android.os.Bundle
import androidx.core.view.setPadding
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivityWithBinding
import com.rex50.mausam.databinding.ActAutoWallpaperBinding
import com.rex50.mausam.enums.AutoWallpaperInterval
import com.rex50.mausam.enums.ContentAnimationState
import com.rex50.mausam.utils.*
import com.rex50.mausam.views.bottomsheets.BSAutoWallpaperInterval
import com.rex50.mausam.views.bottomsheets.BSBlurLevel
import com.rex50.mausam.views.bottomsheets.BSDoNotKillMyApp
import com.rex50.mausam.views.bottomsheets.BSDownload
import com.rex50.mausam.workers.ChangeWallpaperWorker
import com.rex50.mausam.workers.ChangeWallpaperWorker.Companion.CHANGE_NOW
import com.rex50.mausam.workers.ChangeWallpaperWorker.Companion.PROGRESS
import com.thekhaeng.pushdownanim.PushDownAnim
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class ActAutoWallpaper : BaseActivityWithBinding<ActAutoWallpaperBinding>() {

    private val viewModel by viewModel<ActAutoWallpaperViewModel>()

    private val animation: AnimatedMessage<ContentAnimationState> by lazy {
        AnimatedMessage(
            binding?.animLayout,
            arrayListOf(
                AnimatedMessage.AnimationByState(
                    ContentAnimationState.LOADING,
                    R.raw.l_anim_auto_wallpaper,
                    "",
                    "",
                    false
                )
            )
        )
    }

    override fun bindView(): ActAutoWallpaperBinding {
        return ActAutoWallpaperBinding.inflate(layoutInflater)
    }

    override fun loadAct(savedInstanceState: Bundle?) {
        initHeader()

        initAnimation()

        initAutoWallpaperBtn()

        initIntervalBtn()

        initBlurBtn()

        initCropBtn()

        initNotWorkingBtn()
    }

    private fun initNotWorkingBtn() {
        val connectionChecker = ConnectionChecker(this)
        binding?.btnSeeSolutions?.setOnClickListener {
            if(connectionChecker.isNetworkConnected()) {
                BSDoNotKillMyApp().show(supportFragmentManager)
            } else {
                materialSnackBar?.showActionSnackBar(
                    getString(R.string.error_no_internet),
                    getString(R.string.ok_caps),
                    object : MaterialSnackBar.SnackBarListener {
                        override fun onActionPressed() {
                            materialSnackBar?.dismiss()
                        }
                    })
            }
        }
    }

    private fun initCropBtn() {

        fun changeState(checked: Boolean) {

            mausamSharedPrefs?.isEnabledAutoWallpaperCrop = checked

            val msg = if(checked) {
                ChangeWallpaperWorker.scheduleAutoWallpaper(this)
                getString(R.string.desc_center_crop_on)
            } else {
                getString(R.string.desc_center_crop_off)
            }

            binding?.tvBlurCenterCropDesc?.text = msg
        }

        //Get state from shared prefs and update UI
        (mausamSharedPrefs?.isEnabledAutoWallpaperCrop == true).let {
            binding?.sCenterCrop?.setChecked(it)
            changeState(it)
        }

        binding?.sCenterCrop?.setOnCheckedChangeListener { changeState(it) }

    }

    private fun initBlurBtn() {

        fun updateDesc() {
            val percent = ((mausamSharedPrefs?.autoWallpaperBlurIntensity ?: 0f) * 4).roundToInt()
            binding?.tvBlurIntensityDesc?.text = getString(R.string.change_wallpaper_blur_desc, "$percent%")
        }

        binding?.btnChangeBlurIntensity?.setOnClickListener {
            val bs = BSBlurLevel()
            bs.onDismissed = {
                updateDesc()
            }
            bs.show(supportFragmentManager, "BlurLevel")
        }

        updateDesc()

    }

    private fun initIntervalBtn() {

        fun updateDesc() {
            val hours = mausamSharedPrefs?.autoWallpaperInterval ?: AutoWallpaperInterval.TWENTY_FOUR_HOURS
            binding?.tvIntervalDesc?.text = getString(R.string.change_wallpaper_interval_desc, "${hours.interval} hours")
        }

        binding?.btnChangeInterval?.setOnClickListener {
            val bs = BSAutoWallpaperInterval()
            bs.onSetSuccess = { isUpdated ->
                if(isUpdated) {
                    ChangeWallpaperWorker.scheduleAutoWallpaper(applicationContext, ExistingPeriodicWorkPolicy.REPLACE)
                    updateDesc()
                }
            }
            bs.show(supportFragmentManager)
        }

        updateDesc()
    }

    private fun initAutoWallpaperBtn() {

        fun changeState(checked: Boolean) {

            mausamSharedPrefs?.isEnabledAutoWallpaper = checked

            val msg = if(checked) {
                ChangeWallpaperWorker.scheduleAutoWallpaper(applicationContext)
                getString(R.string.auto_wallpaper_on)
            } else {
                ChangeWallpaperWorker.cancelAutoWallpaper(applicationContext)
                getString(R.string.auto_wallpaper_off)
            }

            binding?.tvAutoWall?.text = msg

        }

        //Get state from shared prefs and update UI
        (mausamSharedPrefs?.isEnabledAutoWallpaper == true).let {
            binding?.sAutoWallpaper?.setChecked(it)
            changeState(it)
        }

        //Handle clicks
        binding?.sAutoWallpaper?.setOnCheckedChangeListener { changeState(it) }

        PushDownAnim.setPushDownAnimTo(binding?.lnlAutoWallpaper).setOnClickListener {
            binding?.sAutoWallpaper?.setChecked(binding?.sAutoWallpaper?.isChecked != true)
        }
    }

    private fun initAnimation() {
        animation.cacheAnimations()
        animation.onLottieAnimationConfig = { lottieAnimationView, contentAnimationState ->
            when(contentAnimationState) {
                ContentAnimationState.LOADING -> {
                    lottieAnimationView.configureAutoWallpaperAnim()
                }

                else -> {
                    lottieAnimationView.defaultConfig()
                }
            }
        }
        animation.setAnimationAndShow(ContentAnimationState.LOADING)

        binding?.animLayout?.ivPlaceHolder?.apply {
            setImageResource(R.drawable.ic_refresh)
            setPadding(18)
            setBackgroundResource(R.drawable.tag_dot)
        }

        PushDownAnim.setPushDownAnimTo(binding?.animLayout?.root).setOnClickListener {

            val workManager = WorkManager.getInstance(this)

            val query = WorkQuery.Builder
                .fromTags(listOf(CHANGE_NOW))
                .addStates(listOf(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING))
                .build()

            //Check if any similar work is enqueued or running.
            // If running then no need work again
            if(workManager.getWorkInfos(query).get().isEmpty()) {

                val id = ChangeWallpaperWorker.changeNow(this)

                val downloadUI = BSDownload(supportFragmentManager)

                downloadUI.onCancel = {
                    downloadUI.dismiss()
                }

                downloadUI.downloadStarted()

                workManager.getWorkInfoByIdLiveData(id).observe(this, { work ->
                    when (work.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            downloadUI.downloaded()
                        }
                        WorkInfo.State.FAILED -> {
                            downloadUI.downloadError("Error while changing wallpaper.")
                        }
                        else -> {
                            downloadUI.onProgress(
                                work.progress.getString(PROGRESS)
                                    ?: BSDownload.getDownloadingWithQualityMsg(this)
                            )
                        }
                    }
                })

            }

        }

    }

    private fun initHeader() {
        binding?.headerAutoWall?.apply {
            tvPageTitle.text = getString(R.string.auto_wallpaper)

            tvPageDesc.text = getString(R.string.auto_wallpaper_desc)

            gradientLine.background = GradientHelper.getInstance(this@ActAutoWallpaper)?.getRandomLeftRightGradient()
        }
    }
}