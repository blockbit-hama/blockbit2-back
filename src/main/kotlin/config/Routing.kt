package com.sg.config

import com.sg.controller.userInfoRoutes
import com.sg.controller.protectedRoutes
import com.sg.controller.commonCodeRoutes
import com.sg.repository.UserInfoRepository
import com.sg.repository.CommonCodeRepository
import com.sg.service.UserInfoService
import com.sg.service.CommonCodeService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val userInfoRepository = UserInfoRepository()
    val userInfoService = UserInfoService(userInfoRepository)
    
    val commonCodeRepository = CommonCodeRepository()
    val commonCodeService = CommonCodeService(commonCodeRepository)

    routing {
        get("/") {
            call.respondText("Hello Ktor!")
        }
        protectedRoutes()
        userInfoRoutes(userInfoService)
        commonCodeRoutes(commonCodeService)
    }
}