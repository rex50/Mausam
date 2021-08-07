package com.rex50.mausam.utils.animations

import android.util.Log
import android.view.View
import androidx.annotation.RawRes
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.rex50.mausam.databinding.AnimViewBinding
import com.rex50.mausam.utils.hideView
import com.rex50.mausam.utils.showView

/**
 * Helper class for handling Lottie Animation
 * and showing it with a message and action button
 *
 * @param animViewBinding bind class of anim_view
 * @param animation data which will be used for showing animation
 */
class SingleAnimatedMessage(
    private val animViewBinding: AnimViewBinding?,
    val animation: Animation,
    private val cacheAnimation: Boolean = true
) {

    /**
     * Callback for handling click of Action button
     *
     * @param View of the button clicked
     */
    var onActionBtnClicked: ((View) -> Unit)? = null

    /**
     * Callback for additional configuration of the current Animation
     *
     * @param LottieAnimationView on which configurations will be applied
     */
    var onLottieAnimationConfig: ((LottieAnimationView) -> Unit)? = null


    /**
     * For knowing if animation is showing or not
     */
    var isShowing = false
        private set

    private var isFirstTime = true

    init {
        if(cacheAnimation)
            cacheAnimations()
    }

    /**
     * For caching Animations for fast changing animations
     * and better performance while changing animations
     */
    private fun cacheAnimations() {
        LottieCompositionFactory.fromRawRes(animViewBinding?.animError?.context, animation.animation)
    }

    /**
     * For showing animation
     */
    fun show(forceReload: Boolean = false) {
        if(isFirstTime || forceReload)
            configureAndShow()
        else {
            animViewBinding?.animError?.apply {
                playAnimation()
                showView()
            }
        }
    }

    /**
     * For showing current animation
     */
    private fun configureAndShow() {
        isShowing = true
        animation.let { anim ->

            Log.d(TAG, "Preparing animation")

            //Prepare Animation
            animViewBinding?.lnlError?.showView()
            animViewBinding?.animError?.apply {
                repeatCount = 0
                progress = 0F

                //request additional configuration for the view
                onLottieAnimationConfig?.invoke(this)

                setAnimation(anim.animation)
                playAnimation()
            }

            //Set message text
            animViewBinding?.tvError?.text = anim.messageText

            //Prepare action button
            if(anim.showActionButton) {
                animViewBinding?.btnAction?.apply {
                    text = anim.buttonText
                    setOnClickListener { view ->
                        onActionBtnClicked?.invoke(view)
                    }
                    showView()
                }
            }

        }
    }

    /**
     * For hiding current animation
     */
    fun hide() {
        isShowing = false
        animViewBinding?.animError?.pauseAnimation()
        animViewBinding?.lnlError?.hideView()
    }


    /**
     * Data class for animation details
     *
     * @param animation shown for the state
     * @param messageText which will shown below the animation
     * @param buttonText of the action button
     * @param showActionButton to show button or not. By default button will be always shown.
     */
    data class Animation(
        @RawRes val animation: Int,
        val messageText: String = "Something is wrong. Please try again",
        val buttonText: String = "Retry",
        val showActionButton: Boolean = true
    )

    companion object {
        const val TAG = "AnimatedMessage"
    }
}