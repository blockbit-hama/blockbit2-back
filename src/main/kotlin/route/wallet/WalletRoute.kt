package com.sg.route.wallet

import com.sg.dto.wallet.*
import com.sg.service.wallet.BitcoinMultiSigService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.walletRoutes(
    bitcoinMultiSigService: BitcoinMultiSigService
) {
    route("/api/wallet") {
        route("/bitcoin") {
            // 비트코인 멀티시그 지갑 생성
            post("/create") {
                try {
                    val wallet = bitcoinMultiSigService.createMultisigWallet()
                    call.respond(HttpStatusCode.Created, wallet)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create Bitcoin wallet: ${e.message}"))
                }
            }

            // 비트코인 멀티시그 트랜잭션 생성 (첫 번째 서명)
            post("/transaction/create") {
                try {
                    val request = call.receive<BitcoinTransactionRequestDTO>()
                    val tx = bitcoinMultiSigService.createMultisigTransaction(
                        request.fromAddress,
                        request.toAddress,
                        request.amountSatoshi,
                        request.redeemScriptHex,
                        request.privateKeyHex
                    )
                    call.respond(HttpStatusCode.OK, tx)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create Bitcoin transaction: ${e.message}"))
                }
            }

            // 비트코인 멀티시그 트랜잭션 완료 (두 번째 서명)
            post("/transaction/complete") {
                try {
                    val request = call.receive<BitcoinCompleteRequestDTO>()
                    val txId = bitcoinMultiSigService.addSignatureToTransaction(
                        request.partiallySignedTransaction,
                        request.privateKeyHex
                    )
                    call.respond(HttpStatusCode.OK, mapOf(
                        "txId" to txId,
                        "message" to "Transaction successfully broadcasted to the Bitcoin testnet",
                        "explorerUrl" to "https://mempool.space/testnet/tx/$txId"
                    ))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to complete Bitcoin transaction: ${e.message}"))
                }
            }

            // 트랜잭션 상태 조회
            get("/transaction/{txId}") {
                try {
                    val txId = call.parameters["txId"] ?: return@get call.respond(
                        HttpStatusCode.BadRequest, mapOf("error" to "Transaction ID is required"))

                    val status = bitcoinMultiSigService.getTransactionStatus(txId)
                    call.respond(HttpStatusCode.OK, mapOf(
                        "txId" to txId,
                        "status" to status,
                        "explorerUrl" to "https://mempool.space/testnet/tx/$txId"
                    ))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to get transaction status: ${e.message}"))
                }
            }

            // 주소의 UTXO 목록 조회
            get("/utxos/{address}") {
                try {
                    val address = call.parameters["address"] ?: return@get call.respond(
                        HttpStatusCode.BadRequest, mapOf("error" to "Bitcoin address is required"))

                    val utxoInfo = bitcoinMultiSigService.getAddressUTXOs(address)
                    call.respond(HttpStatusCode.OK, utxoInfo)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve UTXOs: ${e.message}"))
                }
            }

            // 비트코인 주소 잔액 조회
            get("/balance/{address}") {
                try {
                    val address = call.parameters["address"] ?: return@get call.respond(
                        HttpStatusCode.BadRequest, mapOf("error" to "Bitcoin address is required"))

                    // UTXO 정보를 통해 잔액 조회
                    val utxoInfo = bitcoinMultiSigService.getAddressUTXOs(address)

                    val balanceResponse = AddressBalanceResponseDTO(
                        address = address,
                        network = "bitcoin",
                        balance = utxoInfo.totalBalance.toString(),
                        formattedBalance = utxoInfo.totalBalanceBTC,
                        unit = "BTC",
                        decimals = 8
                    )

                    call.respond(HttpStatusCode.OK, balanceResponse)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to get Bitcoin balance: ${e.message}"))
                }
            }
        }
    }
}