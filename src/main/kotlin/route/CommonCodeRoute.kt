package com.sg.route

import com.sg.config.factory.DatabaseFactory.dbQuery
import com.sg.dto.CommonCodeRequestDTO
import com.sg.dto.common.SimpleSuccessResponse
import com.sg.dto.common.SuccessResponse
import com.sg.exception.BadRequestException
import com.sg.exception.NotFoundException
import com.sg.service.CommonCodeService
import com.sg.utils.JwtUtil
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.commonCodeRoute(commonCodeService: CommonCodeService) {
    authenticate("jwt-auth") {
        route("/api/cod") {
            
            get("/list") {
                val offset = call.request.queryParameters["offset"]?.toIntOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()

                val result = dbQuery {
                    commonCodeService.selectCODList(offset, limit)
                }
                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            get("/{codNum}") {
                val codNum = call.parameters["codNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid cod_num is required")

                val result = dbQuery {
                    commonCodeService.selectCOD(codNum)
                } ?: throw NotFoundException("Common code not found")

                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<CommonCodeRequestDTO>()
                val codNum = dbQuery {
                    commonCodeService.insertCOD(request, userId)
                }

                call.respond(
                    HttpStatusCode.Created,
                    SuccessResponse(
                        data = mapOf("codNum" to codNum),
                        message = "Common code created successfully"
                    )
                )
            }
            
            put {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<CommonCodeRequestDTO>()
                val result = dbQuery {
                    commonCodeService.updateCOD(request, userId)
                }

                if (!result) throw NotFoundException("Common code not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Common code updated successfully")
                )
            }
            
            delete("/{codNum}") {
                val codNum = call.parameters["codNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid cod_num is required")
                
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")
                
                val result = dbQuery {
                    commonCodeService.deleteCOD(codNum, userId)
                }
                if (!result) throw NotFoundException("Common code not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Common code deleted successfully")
                )
            }
        }
    }
}
