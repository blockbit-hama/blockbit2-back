package com.sg.config

import com.sg.route.*
import com.sg.route.wallet.*
import com.sg.repository.UserInfoRepository
import com.sg.repository.CommonCodeRepository
import com.sg.service.UserInfoService
import com.sg.service.CommonCodeService
import com.sg.service.wallet.BitcoinMultiSigService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val userInfoRepository = UserInfoRepository()
    val userInfoService = UserInfoService(userInfoRepository)
    
    val commonCodeRepository = CommonCodeRepository()
    val commonCodeService = CommonCodeService(commonCodeRepository)

    val bitcoinApiUrl = environment.config.propertyOrNull("bitcoin.api.url")?.getString() ?: "https://api.blockcypher.com/v1/btc/test3"
    val bitcoinApiKey = environment.config.propertyOrNull("bitcoin.api.key")?.getString() ?: ""
    val bitcoinMultiSigService = BitcoinMultiSigService(bitcoinApiUrl, bitcoinApiKey)

    routing {
        get("/") {
            call.respondText("Hello Ktor!")
        }
        userInfoRoute(userInfoService)
        commonCodeRoute(commonCodeService)
        walletRoutes(bitcoinMultiSigService)

    }
}