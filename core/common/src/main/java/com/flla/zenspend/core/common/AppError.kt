package com.flla.zenspend.core.common

sealed interface AppError {
    data object Unauthorized : AppError

    data object NetworkUnavailable : AppError

    data object Unknown : AppError

    data class Validation(val message: String) : AppError

    data class Remote(val code: Int, val message: String?) : AppError
}
