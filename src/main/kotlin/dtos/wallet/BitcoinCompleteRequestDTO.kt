package com.sg.dtos.wallet

import kotlinx.serialization.Serializable

@Serializable
data class BitcoinCompleteRequestDTO(
    var partiallySignedTransaction: PartiallySignedTransactionDTO = PartiallySignedTransactionDTO(),
    var privateKeyHex: String = ""
)