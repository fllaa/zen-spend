package com.flla.example.feature.auth

import com.flla.example.core.common.AppError

internal fun AppError.toMessage(): String =
    when (this) {
        AppError.NetworkUnavailable -> "We could not reach the server. Try again soon."
        AppError.Unauthorized -> "Your credentials were not accepted."
        AppError.Unknown -> "Something went wrong. Please try again."
        is AppError.Remote -> message ?: "Server error $code."
        is AppError.Validation -> message
    }
