package com.sg.config

import com.sg.route.*
import com.sg.route.wallet.*
import com.sg.repository.*
import com.sg.service.*
import com.sg.service.wallet.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val userInfoRepository = UserInfoRepository()
    val userInfoService = UserInfoService(userInfoRepository)
    
    val commonCodeRepository = CommonCodeRepository()
    val commonCodeService = CommonCodeService(commonCodeRepository)

    val assetsRepository = AssetsRepository();
    val assetsService = AssetsService(assetsRepository)

    val policiesRepository = PoliciesRepository();
    val policiesService = PoliciesService(policiesRepository)

    val walletsRepository = WalletsRepository();
    val walletsService = WalletsService(walletsRepository)

    val walletAddressesRepository = WalletAddressesRepository();
    val walletAddressesService = WalletAddressesService(walletAddressesRepository)

    val walUsiMappRepository = WalUsiMappRepository();
    val walUsiMappService = WalUsiMappService(walUsiMappRepository)

    val transactionsRepository = TransactionsRepository();
    val transactionsService = TransactionsService(transactionsRepository)

    val bitcoinApiUrl = environment.config.propertyOrNull("bitcoin.api.url")?.getString() ?: "https://api.blockcypher.com/v1/btc/test3"
    val bitcoinApiKey = environment.config.propertyOrNull("bitcoin.api.key")?.getString() ?: ""
    val bitcoinMultiSigService = BitcoinMultiSigService(bitcoinApiUrl, bitcoinApiKey, walletsService, walletAddressesService, walUsiMappService)

    routing {
        userInfoRoute(userInfoService)
        commonCodeRoute(commonCodeService)
        bitcoinRoutes(bitcoinMultiSigService)
        assetsRoute(assetsService)
        policiesRoute(policiesService)
        walletsRoute(walletsService)
        walletAddressesRoute(walletAddressesService)
        walUsiMappRoute(walUsiMappService)
        transactionsRoute(transactionsService)
    }
}