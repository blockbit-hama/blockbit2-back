package com.sg.route

import com.sg.dto.*
import com.sg.dto.common.SimpleSuccessResponse
import com.sg.dto.common.SuccessResponse
import com.sg.exception.BadRequestException
import com.sg.exception.NotFoundException
import com.sg.exception.UnauthorizedException
import com.sg.service.UserInfoService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.Cookie

fun Route.userInfoRoute(userInfoService: UserInfoService) {
    route("/api/users") {
        // 모든 사용자 조회
        get {
            val users = userInfoService.getAllUsers()
            call.respond(HttpStatusCode.OK, SuccessResponse(data = users))
        }
        
        // 사용자 ID로 조회
        get("/{id}") {
            val id = call.parameters["id"] ?: throw BadRequestException("User ID is required")
            val user = userInfoService.getUserById(id) ?: throw NotFoundException("User not found")
            call.respond(HttpStatusCode.OK, SuccessResponse(data = user))
        }
        
        // 사용자 번호로 조회
        get("/num/{num}") {
            val num = call.parameters["num"]?.toIntOrNull() 
                ?: throw BadRequestException("Valid user number is required")
            val user = userInfoService.getUserByNum(num) ?: throw NotFoundException("User not found")
            call.respond(HttpStatusCode.OK, SuccessResponse(data = user))
        }
        
        // 신규 사용자 등록
        post {
            val userDTO = call.receive<UserInfoDTO>()
            val usiNum = userInfoService.addUser(userDTO)
            call.respond(
                HttpStatusCode.Created, 
                SuccessResponse(
                    data = mapOf("usiNum" to usiNum.toString()), 
                    message = "User created successfully"
                )
            )
        }
        
        // 사용자 정보 수정
        put {
            val userDTO = call.receive<UserInfoDTO>()
            if (userDTO.usiNum == null) {
                throw BadRequestException("User number is required")
            }
            
            val result = userInfoService.updateUser(userDTO)
            if (!result) throw NotFoundException("User not found")
            
            call.respond(HttpStatusCode.OK, SimpleSuccessResponse(message = "User updated successfully"))
        }
        
        // 사용자 비밀번호 변경
        put("/change-password") {
            val passwordDTO = call.receive<ChangePasswordDTO>()
            val result = userInfoService.changePassword(passwordDTO)
            
            if (!result) {
                throw BadRequestException("Failed to change password. Check your current password")
            }
            call.respond(HttpStatusCode.OK, SimpleSuccessResponse(message = "Password changed successfully"))
        }
        
        // 사용자 삭제 (비활성화)
        delete("/{usiNum}") {
            val usiNum = call.parameters["usiNum"]?.toIntOrNull() 
                ?: throw BadRequestException("Valid user number is required")
            
            val result = userInfoService.deleteUser(usiNum)
            if (!result) throw NotFoundException("User not found")
            
            call.respond(HttpStatusCode.OK, SimpleSuccessResponse(message = "User deleted successfully"))
        }
        
        // 로그인
        post("/login") {
            val loginDTO = call.receive<LoginDTO>()
            val response = userInfoService.login(loginDTO)
            
            if (response == null || !response.success) {
                throw UnauthorizedException("Login failed")
            }
            
            // 토큰을 쿠키로 설정 (24시간)
            response.token?.let { token ->
                call.response.cookies.append(Cookie(
                    name = "auth_token",
                    value = token,
                    maxAge = 86400000, // 24시간 (밀리초)
                    path = "/",
                    secure = false,
                    httpOnly = true
                ))
            }
            call.respond(HttpStatusCode.OK, SuccessResponse(data = response))
        }
    }
}
