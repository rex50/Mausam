package com.rex50.mausam.utils

import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable

object AnimConfigs {

    fun configureAstronautAnim(lottieAnimationView: LottieAnimationView) {
        lottieAnimationView.scale = 0.2f
        lottieAnimationView.repeatCount = LottieDrawable.INFINITE
    }

    fun defaultConfig(lottieAnimationView: LottieAnimationView) {
        lottieAnimationView.scale = 1f
        lottieAnimationView.repeatCount = 0
    }

}