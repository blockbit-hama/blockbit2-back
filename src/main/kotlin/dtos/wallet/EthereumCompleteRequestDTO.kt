package com.sg.dtos.wallet

import kotlinx.serialization.Serializable

@Serializable
data class EthereumCompleteRequestDTO(
    var firstSignature: PartialSignatureDTO = PartialSignatureDTO(),
    var secondParticipantIndex: Int = 0
)