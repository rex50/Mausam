package com.rex50.mausam.utils

import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable


fun LottieAnimationView.configureAstronautAnim() {
    scale = 0.2f
    repeatCount = LottieDrawable.INFINITE
}

fun LottieAnimationView.configure2PlanetAnim() {
    scale = 0.5f
    repeatCount = LottieDrawable.INFINITE
}

fun LottieAnimationView.configureAutoWallpaperAnim() {
    scale = 2f
    speed = 0.5f
    setMinAndMaxProgress(0.23f, 0.6f)
    repeatCount = LottieDrawable.INFINITE
    repeatMode = LottieDrawable.REVERSE
}

fun LottieAnimationView.defaultConfig() {
    scale = 1f
    repeatCount = 0
}