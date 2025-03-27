package com.sg.plugins

import com.sg.repositories.UserRepositoryImpl
import com.sg.controllers.userRoutes
import com.sg.services.UserService
import com.sg.controllers.wallet.walletRoutes
import com.sg.services.wallet.BitcoinMultiSigService
import com.sg.services.wallet.EthereumMpcService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    // 의존성 주입을 위한 객체 생성
    val userRepository = UserRepositoryImpl()
    val userService = UserService(userRepository)

    // 지갑 서비스 객체 생성
    val bitcoinMultiSigService = BitcoinMultiSigService()
    val ethereumMpcService = EthereumMpcService()

    routing {
        get("/") {
            call.respondText("Hello Ktor!")
        }

        // 사용자 관련 라우트
        userRoutes(userService)

        // 지갑 관련 라우트 추가
        walletRoutes(bitcoinMultiSigService, ethereumMpcService)

        // 기타 라우트들...
    }
}