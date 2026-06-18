package com.flla.zenspend.feature.auth

import com.flla.zenspend.core.common.AppError

internal fun AppError.toMessage(): String =
    when (this) {
        AppError.NetworkUnavailable -> "We could not reach the server. Try again soon."
        AppError.Unauthorized -> "Your credentials were not accepted."
        AppError.Unknown -> "Something went wrong. Please try again."
        is AppError.Remote -> message ?: "Server error $code."
        is AppError.Validation -> message
    }
