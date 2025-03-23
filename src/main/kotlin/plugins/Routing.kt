package com.sg.plugins

import com.sg.repositories.UserRepositoryImpl
import com.sg.routes.userRoutes
import com.sg.services.UserService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    // 의존성 주입을 위한 객체 생성
    val userRepository = UserRepositoryImpl()
    val userService = UserService(userRepository)

    routing {
        get("/") {
            call.respondText("Hello Ktor!")
        }

        // 사용자 관련 라우트
        userRoutes(userService)

        // 기타 라우트들...
    }
}