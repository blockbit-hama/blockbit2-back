package com.sg.route

import com.sg.config.factory.DatabaseFactory.dbQuery
import com.sg.dto.TransactionsRequestDTO
import com.sg.dto.common.SimpleSuccessResponse
import com.sg.dto.common.SuccessResponse
import com.sg.exception.BadRequestException
import com.sg.exception.NotFoundException
import com.sg.service.TransactionsService
import com.sg.utils.JwtUtil
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.transactionsRoute(transactionsService: TransactionsService) {
    authenticate("jwt-auth") {
        route("/api/trx") {
            
            get("/list") {
                val offset = call.request.queryParameters["offset"]?.toIntOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()

                val result = dbQuery {
                    transactionsService.selectTRXList(offset, limit)
                }
                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            get("/{trxNum}") {
                val trxNum = call.parameters["trxNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid trx_num is required")

                val result = dbQuery {
                    transactionsService.selectTRX(trxNum)
                } ?: throw NotFoundException("Transaction not found")

                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<TransactionsRequestDTO>()
                val trxNum = dbQuery {
                    transactionsService.insertTRX(request, userId)
                }

                call.respond(
                    HttpStatusCode.Created,
                    SuccessResponse(
                        data = mapOf("trxNum" to trxNum),
                        message = "Transaction created successfully"
                    )
                )
            }
            
            put("/{trxNum}") {
                val trxNum = call.parameters["trxNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid trx_num is required")
                
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<TransactionsRequestDTO>()
                val requestWithNum = request.copy(trxNum = trxNum)
                
                val result = dbQuery {
                    transactionsService.updateTRX(requestWithNum, userId)
                }

                if (!result) throw NotFoundException("Transaction not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Transaction updated successfully")
                )
            }
            
            delete("/{trxNum}") {
                val trxNum = call.parameters["trxNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid trx_num is required")
                
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")
                
                val result = dbQuery {
                    transactionsService.deleteTRX(trxNum, userId)
                }
                if (!result) throw NotFoundException("Transaction not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Transaction deleted successfully")
                )
            }
        }
    }
}
