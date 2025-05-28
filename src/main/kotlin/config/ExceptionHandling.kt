package com.sg.config

import com.sg.dto.common.ErrorResponse
import com.sg.exception.ApiException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<ApiException> { call, cause ->
            call.respond(
                cause.statusCode,
                ErrorResponse(
                    error = cause.statusCode.description,
                    message = cause.message
                )
            )
        }
        
        exception<Exception> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    error = "Internal Server Error",
                    message = cause.message ?: "Unknown error occurred"
                )
            )
        }
    }
}
