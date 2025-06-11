package com.sg.route

import com.sg.config.factory.DatabaseFactory.dbQuery
import com.sg.dto.WalletAddressesRequestDTO
import com.sg.dto.common.SimpleSuccessResponse
import com.sg.dto.common.SuccessResponse
import com.sg.exception.BadRequestException
import com.sg.exception.NotFoundException
import com.sg.service.WalletAddressesService
import com.sg.utils.JwtUtil
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.walletAddressesRoute(walletAddressesService: WalletAddressesService) {
    authenticate("jwt-auth") {
        route("/api/wad") {
            
            get("/list") {
                val offset = call.request.queryParameters["offset"]?.toIntOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()
                val walNum = call.request.queryParameters["walNum"]?.toIntOrNull()

                val result = dbQuery {
                    walletAddressesService.selectWADList(offset, limit, walNum)
                }
                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            get("/{wadNum}") {
                val wadNum = call.parameters["wadNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid wad_num is required")

                val result = dbQuery {
                    walletAddressesService.selectWAD(wadNum)
                } ?: throw NotFoundException("Wallet address not found")

                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            get("/wallet/{walNum}") {
                val walNum = call.parameters["walNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid wal_num is required")

                val result = dbQuery {
                    walletAddressesService.selectWADByWallet(walNum)
                }
                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<WalletAddressesRequestDTO>()
                val wadNum = dbQuery {
                    walletAddressesService.insertWAD(request, userId)
                }

                call.respond(
                    HttpStatusCode.Created,
                    SuccessResponse(
                        data = mapOf("wadNum" to wadNum),
                        message = "Wallet address created successfully"
                    )
                )
            }
            
            put {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<WalletAddressesRequestDTO>()
                val result = dbQuery {
                    walletAddressesService.updateWAD(request, userId)
                }

                if (!result) throw NotFoundException("Wallet address not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Wallet address updated successfully")
                )
            }
            
            delete("/{wadNum}") {
                val wadNum = call.parameters["wadNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid wad_num is required")
                
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")
                
                val result = dbQuery {
                    walletAddressesService.deleteWAD(wadNum, userId)
                }
                if (!result) throw NotFoundException("Wallet address not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Wallet address deleted successfully")
                )
            }
        }
    }
}
