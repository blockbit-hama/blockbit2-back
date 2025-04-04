package com.sg.controller

import com.sg.dto.*
import com.sg.service.UserInfoService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class UserInfoController(private val userInfoService: UserInfoService) {
    
    fun Route.userInfoRouting() {
        route("/api/users") {
            // 모든 사용자 조회
            get {
                val users = userInfoService.getAllUsers()
                call.respond(users)
            }
            
            // 사용자 ID로 조회
            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "사용자 ID가 필요합니다."))
                
                val user = userInfoService.getUserById(id)
                if (user != null) {
                    call.respond(user)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "사용자를 찾을 수 없습니다."))
                }
            }
            
            // 사용자 번호로 조회
            get("/num/{num}") {
                val num = call.parameters["num"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "유효한 사용자 번호가 필요합니다."))
                
                val user = userInfoService.getUserByNum(num)
                if (user != null) {
                    call.respond(user)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "사용자를 찾을 수 없습니다."))
                }
            }
            
            // 신규 사용자 등록
            post {
                try {
                    val userDTO = call.receive<UserInfoDTO>()
                    val usiNum = userInfoService.addUser(userDTO)
                    call.respond(HttpStatusCode.Created, mapOf("usiNum" to usiNum, "message" to "사용자가 생성되었습니다."))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "사용자 생성 중 오류가 발생했습니다.")))
                }
            }
            
            // 사용자 정보 수정
            put {
                try {
                    val userDTO = call.receive<UserInfoDTO>()
                    if (userDTO.usiNum == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "사용자 번호가 필요합니다."))
                        return@put
                    }
                    
                    val result = userInfoService.updateUser(userDTO)
                    if (result) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "사용자 정보가 업데이트되었습니다."))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "사용자를 찾을 수 없습니다."))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "사용자 정보 업데이트 중 오류가 발생했습니다.")))
                }
            }
            
            // 사용자 비밀번호 변경
            put("/change-password") {
                try {
                    val passwordDTO = call.receive<ChangePasswordDTO>()
                    val result = userInfoService.changePassword(passwordDTO)
                    
                    if (result) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "비밀번호가 변경되었습니다."))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "비밀번호 변경에 실패했습니다. 현재 비밀번호를 확인해주세요."))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "비밀번호 변경 중 오류가 발생했습니다.")))
                }
            }
            
            // 사용자 삭제 (비활성화)
            delete("/{usiNum}") {
                val usiNum = call.parameters["usiNum"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "유효한 사용자 번호가 필요합니다."))
                
                val result = userInfoService.deleteUser(usiNum)
                if (result) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "사용자가 삭제되었습니다."))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "사용자를 찾을 수 없습니다."))
                }
            }
            
            // 로그인
            post("/login") {
                try {
                    val loginDTO = call.receive<LoginDTO>()
                    val response = userInfoService.login(loginDTO)
                    
                    if (response != null && response.success) {
                        call.respond(HttpStatusCode.OK, response)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, response ?: mapOf("error" to "로그인에 실패했습니다."))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "로그인 중 오류가 발생했습니다.")))
                }
            }
        }
    }
}

// Application에서 라우팅 설정을 위한 확장 함수
fun Application.configureUserRoutes(userInfoService: UserInfoService) {
    routing {
        val userInfoController = UserInfoController(userInfoService)
        with(userInfoController) {
            userInfoRouting()
        }
    }
}
