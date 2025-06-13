package com.sg.route

import com.sg.config.factory.DatabaseFactory.dbQuery
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

                val result = dbQuery {
                    walletsService.selectWALList(offset, limit)
                }
                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            get("/{walNum}") {
                val walNum = call.parameters["walNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid wal_num is required")

                val result = dbQuery {
                    walletsService.selectWAL(walNum)
                } ?: throw NotFoundException("Wallet not found")

                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<WalletsRequestDTO>()
                val walNum = dbQuery {
                    walletsService.insertWAL(request, userId)
                }

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
                val result = dbQuery {
                    walletsService.updateWAL(request, userId)
                }

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
                
                val result = dbQuery {
                    walletsService.deleteWAL(walNum, userId)
                }
                if (!result) throw NotFoundException("Wallet not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Wallet deleted successfully")
                )
            }
            
            // 특정 사용자의 지갑 목록과 상세정보 조회 (wallets + wal_usi_mapp 조인)
            route("/wad") {
                get("/list/{usiNum}") {
                    val usiNum = call.parameters["usiNum"]?.toIntOrNull()
                        ?: throw BadRequestException("Valid usi_num is required")
                    
                    val offset = call.request.queryParameters["offset"]?.toIntOrNull()
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull()

                    val result = dbQuery {
                        walletsService.selectWADList(usiNum, offset, limit)
                    }
                    
                    call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
                }
            }
            
            // 특정 지갑과 이해관계가 있는 사용자 정보들 조회 (user_info + wal_usi_mapp 조인)
            route("/users") {
                get("/list/{walNum}") {
                    val walNum = call.parameters["walNum"]?.toIntOrNull()
                        ?: throw BadRequestException("Valid wal_num is required")
                    
                    val offset = call.request.queryParameters["offset"]?.toIntOrNull()
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull()

                    val result = dbQuery {
                        walletsService.selectWalletUsersList(walNum, offset, limit)
                    }
                    
                    call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
                }
            }
        }
    }
}
