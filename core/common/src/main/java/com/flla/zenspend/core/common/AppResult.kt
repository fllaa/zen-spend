package com.flla.zenspend.core.common

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>

    data class Failure(val error: AppError, val cause: Throwable? = null) : AppResult<Nothing>
}

inline fun <T> AppResult<T>.onSuccess(block: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) block(data)
    return this
}

inline fun <T> AppResult<T>.onFailure(block: (AppError) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) block(error)
    return this
}
