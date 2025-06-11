package com.sg.route

import com.sg.config.factory.DatabaseFactory.dbQuery
import com.sg.dto.WalUsiMappRequestDTO
import com.sg.dto.common.SimpleSuccessResponse
import com.sg.dto.common.SuccessResponse
import com.sg.exception.BadRequestException
import com.sg.exception.NotFoundException
import com.sg.service.WalUsiMappService
import com.sg.utils.JwtUtil
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.walUsiMappRoute(walUsiMappService: WalUsiMappService) {
    authenticate("jwt-auth") {
        route("/api/wum") {
            
            get("/list") {
                val offset = call.request.queryParameters["offset"]?.toIntOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()

                val result = dbQuery {
                    walUsiMappService.selectWUMList(offset, limit)
                }
                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            get("/{wumNum}") {
                val wumNum = call.parameters["wumNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid wum_num is required")

                val result = dbQuery {
                    walUsiMappService.selectWUM(wumNum)
                } ?: throw NotFoundException("Wallet user mapping not found")

                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<WalUsiMappRequestDTO>()
                val wumNum = dbQuery {
                    walUsiMappService.insertWUM(request, userId)
                }

                call.respond(
                    HttpStatusCode.Created,
                    SuccessResponse(
                        data = mapOf("wumNum" to wumNum),
                        message = "Wallet user mapping created successfully"
                    )
                )
            }
            
            put("/{wumNum}") {
                val wumNum = call.parameters["wumNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid wum_num is required")
                
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<WalUsiMappRequestDTO>()
                val requestWithNum = request.copy(wumNum = wumNum)
                
                val result = dbQuery {
                    walUsiMappService.updateWUM(requestWithNum, userId)
                }

                if (!result) throw NotFoundException("Wallet user mapping not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Wallet user mapping updated successfully")
                )
            }
            
            delete("/{wumNum}") {
                val wumNum = call.parameters["wumNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid wum_num is required")
                
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")
                
                val result = dbQuery {
                    walUsiMappService.deleteWUM(wumNum, userId)
                }
                if (!result) throw NotFoundException("Wallet user mapping not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Wallet user mapping deleted successfully")
                )
            }
        }
    }
}
