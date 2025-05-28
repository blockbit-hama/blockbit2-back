package com.sg.config

import com.sg.controller.userInfoRoutes
import com.sg.controller.protectedRoutes
import com.sg.repository.UserInfoRepository
import com.sg.service.UserInfoService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val userInfoRepository = UserInfoRepository()
    val userInfoService = UserInfoService(userInfoRepository)

    routing {
        get("/") {
            call.respondText("Hello Ktor!")
        }
        protectedRoutes()
        userInfoRoutes(userInfoService)
    }
}