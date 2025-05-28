package com.sg.exception

import io.ktor.http.*

sealed class ApiException(
    val statusCode: HttpStatusCode,
    override val message: String
) : Exception(message)

class BadRequestException(message: String = "Bad Request") : ApiException(HttpStatusCode.BadRequest, message)
class NotFoundException(message: String = "Not Found") : ApiException(HttpStatusCode.NotFound, message)
class UnauthorizedException(message: String = "Unauthorized") : ApiException(HttpStatusCode.Unauthorized, message)
class InternalServerException(message: String = "Internal Server Error") : ApiException(HttpStatusCode.InternalServerError, message)
