package com.rex50.mausam.views.activities.auto_wallpaper

import android.os.Bundle
import androidx.work.ExistingPeriodicWorkPolicy
import com.rex50.mausam.R
import com.rex50.mausam.base_classes.BaseActivityWithBinding
import com.rex50.mausam.databinding.ActAutoWallpaperBinding
import com.rex50.mausam.enums.AutoWallpaperInterval
import com.rex50.mausam.enums.ContentAnimationState
import com.rex50.mausam.utils.*
import com.rex50.mausam.views.bottomsheets.BSAutoWallpaperInterval
import com.rex50.mausam.views.bottomsheets.BSBlurLevel
import com.rex50.mausam.workers.ChangeWallpaperWorker
import com.thekhaeng.pushdownanim.PushDownAnim
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit
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
                    ChangeWallpaperWorker.scheduleAutoWallpaper(applicationContext)
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
                ChangeWallpaperWorker.scheduleAutoWallpaper(applicationContext, ExistingPeriodicWorkPolicy.KEEP)
                getString(R.string.auto_wallpaper_on)
            } else {
                ChangeWallpaperWorker.cancelAutoWallpaper(applicationContext)
                getString(R.string.auto_wallpaper_off)
            }

            binding?.tvAutoWall?.text = msg

        }


        binding?.sAutoWallpaper?.setOnCheckedChangeListener { changeState(it) }

        //Get state from shared prefs and update UI
        (mausamSharedPrefs?.isEnabledAutoWallpaper == true).let {
            binding?.sAutoWallpaper?.setChecked(it)
            changeState(it)
        }

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
    }

    private fun initHeader() {
        binding?.headerAutoWall?.apply {
            tvPageTitle.text = getString(R.string.auto_wallpaper)

            tvPageDesc.text = getString(R.string.auto_wallpaper_desc)

            gradientLine.background = GradientHelper.getInstance(this@ActAutoWallpaper)?.getRandomLeftRightGradient()
        }
    }
}