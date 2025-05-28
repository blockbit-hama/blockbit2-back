package com.sg.dto.common

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val success: Boolean = false,
    val error: String,
    val message: String? = null,
    val timestamp: String = java.time.LocalDateTime.now().toString()
)

@Serializable
data class SuccessResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val message: String? = null
)

@Serializable
data class SimpleSuccessResponse(
    val success: Boolean = true,
    val message: String? = null
)
