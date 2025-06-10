package com.sg.route

import com.sg.dto.AssetsRequestDTO
import com.sg.dto.common.SimpleSuccessResponse
import com.sg.dto.common.SuccessResponse
import com.sg.exception.BadRequestException
import com.sg.exception.NotFoundException
import com.sg.service.AssetsService
import com.sg.utils.JwtUtil
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.assetsRoute(assetsService: AssetsService) {
    authenticate("jwt-auth") {
        route("/api/ast") {
            
            get("/list") {
                val offset = call.request.queryParameters["offset"]?.toIntOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()

                val result = assetsService.selectASTList(offset, limit)
                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            get("/{astNum}") {
                val astNum = call.parameters["astNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid ast_num is required")

                val result = assetsService.selectAST(astNum)
                    ?: throw NotFoundException("Asset not found")

                call.respond(HttpStatusCode.OK, SuccessResponse(data = result))
            }
            
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<AssetsRequestDTO>()
                val astNum = assetsService.insertAST(request, userId)

                call.respond(
                    HttpStatusCode.Created,
                    SuccessResponse(
                        data = mapOf("astNum" to astNum),
                        message = "Asset created successfully"
                    )
                )
            }
            
            put {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")

                val request = call.receive<AssetsRequestDTO>()
                val result = assetsService.updateAST(request, userId)

                if (!result) throw NotFoundException("Asset not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Asset updated successfully")
                )
            }
            
            delete("/{astNum}") {
                val astNum = call.parameters["astNum"]?.toIntOrNull()
                    ?: throw BadRequestException("Valid ast_num is required")
                
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.let { JwtUtil.extractUserId(it) }
                    ?: throw BadRequestException("User authentication required")
                
                val result = assetsService.deleteAST(astNum, userId)
                if (!result) throw NotFoundException("Asset not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleSuccessResponse(message = "Asset deleted successfully")
                )
            }
        }
    }
}
