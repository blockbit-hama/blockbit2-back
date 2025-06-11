package com.sg.route

import com.sg.config.factory.DatabaseFactory.dbQuery
import com.sg.dto.PoliciesRequestDTO
import com.sg.dto.common.SimpleSuccessResponse
import com.sg.dto.common.SuccessResponse
import com.sg.exception.BadRequestException
import com.sg.exception.NotFoundException
import com.sg.service.PoliciesService
import com.sg.utils.JwtUtil
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.policiesRoute(policiesService: PoliciesService) {
    authenticate("jwt-auth") {
        route("/api/pol") {
            
            get("/list") {
                val offset = call.request.queryParameters["offset"]?.toIntOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()

                val result = dbQuery {
                    policiesService.selectPOLList(offset, limit)
                }
                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            get("/{polNum}") {
                val polNum = call.parameters["polNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid pol_num is required")

                val result = dbQuery {
                    policiesService.selectPOL(polNum)
                } ?: throw NotFoundException("Policy not found")

                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<PoliciesRequestDTO>()
                val polNum = dbQuery {
                    policiesService.insertPOL(request, userId)
                }

                call.respond(
                    HttpStatusCode.Created,
                    SuccessResponse(
                        data = mapOf("polNum" to polNum),
                        message = "Policy created successfully"
                    )
                )
            }
            
            put {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<PoliciesRequestDTO>()
                val result = dbQuery {
                    policiesService.updatePOL(request, userId)
                }

                if (!result) throw NotFoundException("Policy not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Policy updated successfully")
                )
            }
            
            delete("/{polNum}") {
                val polNum = call.parameters["polNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid pol_num is required")
                
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")
                
                val result = dbQuery {
                    policiesService.deletePOL(polNum, userId)
                }
                if (!result) throw NotFoundException("Policy not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Policy deleted successfully")
                )
            }
        }
    }
}
