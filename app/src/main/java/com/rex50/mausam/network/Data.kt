package com.rex50.mausam.network

sealed class Status<out T: Any> {
    data class Success<out T: Any>(val value: T): Status<T>()
    data class Error(val msg: String, val cause: Exception? = null): Status<Nothing>()
    object Loading : Status<Nothing>()
}