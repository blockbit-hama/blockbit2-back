package com.sg.controller

import com.sg.dto.CommonCodeRequestDTO
import com.sg.dto.CommonCodeUpdateDTO
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

fun Route.commonCodeRoutes(commonCodeService: CommonCodeService) {
    authenticate("jwt-auth") {
        route("/api/cod") {
            
            get("/list") {
                val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 50

                val result = commonCodeService.selectCodList(offset, limit)
                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            get("/{codNum}") {
                val codNum = call.parameters["codNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid cod_num is required")

                val result = commonCodeService.selectCod(codNum)
                    ?: throw NotFoundException("Common code not found")

                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<CommonCodeRequestDTO>()
                val codNum = commonCodeService.insertCod(request, userId)

                call.respond(
                    HttpStatusCode.Created,
                    SuccessResponse(
                        data = mapOf("codNum" to codNum),
                        message = "Common code created successfully"
                    )
                )
            }
            
            put("/{codNum}") {
                val codNum = call.parameters["codNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid cod_num is required")
                
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")
                
                val request = call.receive<CommonCodeRequestDTO>()
                val updateDTO = CommonCodeUpdateDTO(
                    codNum = codNum,
                    codType = request.codType,
                    codKey = request.codKey,
                    codVal = request.codVal,
                    codDesc = request.codDesc
                )
                
                val result = commonCodeService.updateCod(updateDTO, userId)
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
                
                val result = commonCodeService.deleteCod(codNum, userId)
                if (!result) throw NotFoundException("Common code not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Common code deleted successfully")
                )
            }
        }
    }
}
