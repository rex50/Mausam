package com.rex50.mausam.enums

enum class ContentAnimationState {
    LOADING,
    SUCCESS,
    ERROR,
    EMPTY,
    NO_INTERNET;
}

sealed class ContentLoadingState<out T: Any> {
    object Preparing : ContentLoadingState<Nothing>()
    data class Ready<out T: Any>(val data: T): ContentLoadingState<T>()
    object Empty : ContentLoadingState<Nothing>()
    object NoInternet : ContentLoadingState<Nothing>()
}