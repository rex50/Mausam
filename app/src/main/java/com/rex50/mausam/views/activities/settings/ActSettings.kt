package com.rex50.mausam.views.activities.settings

import android.content.Intent
import android.content.Intent.createChooser
import android.os.Bundle
import com.rex50.mausam.BuildConfig
import com.rex50.mausam.R
import com.rex50.mausam.utils.*
import com.rex50.mausam.MausamApplication
import com.rex50.mausam.base_classes.BaseActivityWithBinding
import com.rex50.mausam.databinding.ActSettingsBinding
import com.rex50.mausam.views.activities.ActUsedLibrary
import com.rex50.mausam.views.bottomsheets.BSDownloadQuality
import com.rex50.mausam.views.bottomsheets.BSFeedback
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel

class ActSettings: BaseActivityWithBinding<ActSettingsBinding>() {

    private val viewModel by viewModel<ActSettingsViewModel>()

    override fun bindView(): ActSettingsBinding {
        return ActSettingsBinding.inflate(layoutInflater)
    }

    override fun loadAct(savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {

        initHeader()

        initPrivacyPolicyUI()

        initDarkModeUI()

        initDataSaverUI()

        initDownloadQualityUI()

        initLocationUI()

        setupCacheObserver()

        initClicks()
    }

    private fun initPrivacyPolicyUI() {
        binding?.tvPrivacyDesc?.text = getString(R.string.privacy_desc, BuildConfig.VERSION_NAME)
    }

    private fun initLocationUI() {
        binding?.lnlCurrLoc?.hideView()
    }

    private fun initHeader() {
        binding?.headerSettings?.apply {
            tvPageTitle.text = getString(R.string.settings)

            tvPageDesc.hideView()

            gradientLine.background = GradientHelper.getInstance(this@ActSettings)?.getRandomLeftRightGradient()
        }
    }

    private fun initDarkModeUI() {
        val isDarkMode = mausamSharedPrefs?.isDarkModeEnabled ?: false
        binding?.sDarkMode?.isEnabled = true
        binding?.sDarkMode?.setChecked(isDarkMode)
        binding?.headerSettings?.apply {
            flHeaderBg.takeIf { isDarkMode }?.showView() ?: flHeaderBg.hideView()
            ivHeaderImg.setImageResource(R.drawable.ic_darth_vader)
        }
        binding?.tvDarkModeDesc?.text = if (isDarkMode) getString(R.string.dark_mode_on_desc) else getString(R.string.dark_mode_off_desc)
    }

    private fun initDataSaverUI() {
        val isDataSaverMode = mausamSharedPrefs?.isDataSaverMode ?: false
        binding?.sDataSaver?.setChecked(isDataSaverMode)
    }

    private fun initDownloadQualityUI() {
        binding?.btnDownloadQuality?.setOnClickListener {
            BSDownloadQuality().show(supportFragmentManager)
        }
    }

    private fun setupCacheObserver() {
        viewModel.liveCacheSize.observe(this) {
            binding?.tvCacheDesc?.text = getString(R.string.using_cache, it.toString())
        }
    }

    private fun initClicks() {

        binding?.sDarkMode?.setOnCheckedChangeListener {
            mausamSharedPrefs?.isDarkModeEnabled = it
            binding?.sDarkMode?.isEnabled = false
            MainScope().launch {
                delay(500)
                MausamApplication.getInstance()?.followSystemThemeIfRequired()
                initDarkModeUI()
            }
        }

        binding?.sDataSaver?.setOnCheckedChangeListener {
            mausamSharedPrefs?.isDataSaverMode = it
        }

        binding?.btnClearCache?.setOnClickListener{
            showToast("Clearing cache...")
            viewModel.clearAppCache {
                showToast(getString(R.string.error_problem_clearing_cache))
            }
        }

        binding?.btnCurrentLoc?.setOnClickListener{
            showToast("Work in progress")
        }

        PushDownAnim
                .setPushDownAnimTo(
                    binding?.lnlBigDonation,
                    binding?.lnlSmallDonation,
                    binding?.lnlMediumDonation,
                    binding?.btnShareApp,
                    binding?.btnGotoPlayStore,
                    binding?.btnReportBugs,
                    binding?.btnResUsed
                )
                .setOnClickListener {
                    when(it){
                        binding?.lnlSmallDonation -> {
                            showToast("Small Donation")
                        }

                        binding?.lnlMediumDonation -> {
                            showToast("Medium Donation")
                        }

                        binding?.lnlBigDonation -> {
                            showToast("Big Donation")
                        }

                        binding?.btnShareApp -> {
                            startActivity(
                                createChooser(Intent(Intent.ACTION_SEND)
                                    .setType("text/plain")
                                    .putExtra(Intent.EXTRA_SUBJECT, "Share app link")
                                    .putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_share_app_desc, getString(R.string.link_play_store))), "Select")
                            )
                        }

                        binding?.btnGotoPlayStore -> {
                            //TODO: Integrate in-app review
                            openUrl(getString(R.string.link_play_store))
                        }

                        binding?.btnReportBugs -> {
                            BSFeedback().show(supportFragmentManager)
                        }

                        binding?.btnResUsed -> {
                            val intent = Intent(this@ActSettings, ActUsedLibrary::class.java)
                            startActivity(intent)
                        }

                        else -> showToast(getString(R.string.something_wrong_msg))
                    }
                }
    }

}