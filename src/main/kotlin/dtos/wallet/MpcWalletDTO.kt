package com.sg.dtos.wallet

import kotlinx.serialization.Serializable

@Serializable
data class MpcWalletDTO(
    var walletId: String = "",
    var address: String = "",
    var publicKey: String = ""
)