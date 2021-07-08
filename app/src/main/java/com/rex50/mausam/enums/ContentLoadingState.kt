package com.rex50.mausam.enums

import com.rex50.mausam.model_classes.utils.AllContentModel

enum class ContentAnimationState {
    PREPARING,
    EMPTY,
    NO_INTERNET;
}

sealed class ContentLoadingState {
    object Preparing : ContentLoadingState()
    data class Ready(val allContentModel: AllContentModel): ContentLoadingState()
    object Empty : ContentLoadingState()
    object NoInternet : ContentLoadingState()

}