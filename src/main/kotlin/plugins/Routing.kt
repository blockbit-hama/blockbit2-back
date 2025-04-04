package com.sg.plugins

import com.sg.controller.UserInfoController
import com.sg.controller.configureUserRoutes
import com.sg.controller.wallet.walletRoutes
import com.sg.repository.UserInfoRepository
import com.sg.service.UserInfoService
import com.sg.service.wallet.BitcoinMultiSigService
import com.sg.service.wallet.EthereumMpcService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    // 의존성 주입을 위한 객체 생성
    val userInfoRepository = UserInfoRepository()
    val userInfoService = UserInfoService(userInfoRepository)

    // 지갑 서비스 객체 생성
    val bitcoinMultiSigService = BitcoinMultiSigService()
    val ethereumMpcService = EthereumMpcService()

    routing {
        get("/") {
            call.respondText("Hello Ktor!")
        }

        // 사용자 관련 라우트
        configureUserRoutes(userInfoService)

        // 지갑 관련 라우트 추가
        walletRoutes(bitcoinMultiSigService, ethereumMpcService)

        // 기타 라우트들...
    }
}