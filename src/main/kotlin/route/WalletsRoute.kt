package com.sg.route

import com.sg.dto.WalletsRequestDTO
import com.sg.dto.common.SimpleSuccessResponse
import com.sg.dto.common.SuccessResponse
import com.sg.exception.BadRequestException
import com.sg.exception.NotFoundException
import com.sg.service.WalletsService
import com.sg.utils.JwtUtil
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.walletsRoute(walletsService: WalletsService) {
    authenticate("jwt-auth") {
        route("/api/wal") {
            
            get("/list") {
                val offset = call.request.queryParameters["offset"]?.toIntOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()

                val result = walletsService.selectWALList(offset, limit)
                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            get("/{walNum}") {
                val walNum = call.parameters["walNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid wal_num is required")

                val result = walletsService.selectWAL(walNum)
                    ?: throw NotFoundException("Wallet not found")

                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<WalletsRequestDTO>()
                val walNum = walletsService.insertWAL(request, userId)

                call.respond(
                    HttpStatusCode.Created,
                    SuccessResponse(
                        data = mapOf("walNum" to walNum),
                        message = "Wallet created successfully"
                    )
                )
            }
            
            put {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<WalletsRequestDTO>()
                val result = walletsService.updateWAL(request, userId)

                if (!result) throw NotFoundException("Wallet not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Wallet updated successfully")
                )
            }
            
            delete("/{walNum}") {
                val walNum = call.parameters["walNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid wal_num is required")
                
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")
                
                val result = walletsService.deleteWAL(walNum, userId)
                if (!result) throw NotFoundException("Wallet not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Wallet deleted successfully")
                )
            }
        }
    }
}
