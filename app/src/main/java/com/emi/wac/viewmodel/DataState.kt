package com.emi.wac.viewmodel

/**
 * Sealed class representing the state of data.
 * @param T The type of data.
 * @property Loading Represents the loading state.
 * @property Success Represents the success state with the data.
 * @property Error Represents the error state with the error message.
 */
sealed class DataState<out T> {
    object Loading : DataState<Nothing>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val message: String) : DataState<Nothing>()
}