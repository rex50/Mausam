package com.rex50.mausam.utils.animations

import android.util.Log
import android.view.View
import androidx.annotation.RawRes
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.rex50.mausam.databinding.AnimViewBinding
import com.rex50.mausam.utils.hideView
import com.rex50.mausam.utils.showView
import java.lang.IllegalArgumentException

/**
 * Helper class for handling Lottie Animation based on different state
 *
 * <StateType> - an enum class which can help to maintain different state
 *
 * @param animViewBinding bind class of anim_view
 * @param listOfAnimations list of animations and it's data which will be used in future
 */
class AnimatedMessage<StateType>(
    private val animViewBinding: AnimViewBinding?,
    val listOfAnimations: ArrayList<AnimationByState<StateType>>
) {

    /**
     * Callback for handling click of Action button
     *
     * @param View of the button clicked
     * @param StateType - current state
     */
    var onActionBtnClicked: ((View, StateType) -> Unit)? = null

    /**
     * Callback for additional configuration of the current Animation
     *
     * @param LottieAnimationView on which configurations will be applied
     * @param StateType - current state
     */
    var onLottieAnimationConfig: ((LottieAnimationView, StateType) -> Unit)? = null

    /**
     * Current animation details
     */
    var currentAnim: AnimationByState<StateType>? = null
        private set

    /**
     * For knowing if animation is showing or not
     */
    var isShowing = false
        private set

    /**
     * First animation from the list is used as default animation
     */
    private var defaultAnimation: AnimationByState<StateType>

    private var isFirstTime = true

    init {
        check(listOfAnimations.isNotEmpty()) {
            "List of animations should not be empty."
        }

        //Init default animation
        defaultAnimation = listOfAnimations[0]

        //Set default animation as currentAnim
        if(currentAnim == null) {
            currentAnim = defaultAnimation
        }
    }

    /**
     * For changing current animation based on the given state
     *
     * This function will try to find an animation for the given state and store it in for next use.
     *
     * @throws IllegalArgumentException when animation is not found.
     */
    private fun setAnimation(state: StateType) {
        listOfAnimations.find { it.state == state }?.let {
            currentAnim = it
        } ?: throw IllegalArgumentException("Cannot find $state in the list of animations")
    }

    /**
     * For caching Animations for fast changing animations
     * and better performance while changing animations
     */
    fun cacheAnimations() {
        listOfAnimations.forEach {
            LottieCompositionFactory.fromRawRes(animViewBinding?.animError?.context, it.animation)
        }
    }

    /**
     * For changing and showing current animation based on given state
     */
    fun setAnimationAndShow(state: StateType) {
        if((currentAnim?.state != state) || isFirstTime) {
            isFirstTime = false
            setAnimation(state)
            show()
        } else {
            animViewBinding?.lnlError?.showView()
        }
    }

    /**
     * For showing current animation
     */
    private fun show() {
        isShowing = true
        currentAnim?.let { anim ->

            Log.d(TAG, "Preparing animation for ${anim.state} state")

            //Prepare Animation
            animViewBinding?.lnlError?.showView()
            animViewBinding?.animError?.apply {
                repeatCount = 0
                progress = 0F

                //request additional configuration for the view
                onLottieAnimationConfig?.invoke(this, anim.state)

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
                        onActionBtnClicked?.invoke(view, anim.state)
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
        animViewBinding?.lnlError?.hideView()
    }


    /**
     * Data class for storing animation details based on State
     *
     * @param state
     * @param animation shown for the state
     * @param messageText which will shown below the animation
     * @param buttonText of the action button
     * @param showActionButton to show button or not. By default button will be always shown.
     */
    data class AnimationByState<StateType>(
        val state: StateType,
        @RawRes val animation: Int,
        val messageText: String = "Something is wrong. Please try again",
        val buttonText: String = "Retry",
        val showActionButton: Boolean = true
    )

    companion object {
        const val TAG = "AnimatedMessage"
    }
}