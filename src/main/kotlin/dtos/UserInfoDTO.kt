package com.sg.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDTO(
    val usiNum: Int? = null,
    val usiId: String? = null,
    val usiPwd: String? = null,
    val usiName: String? = null,
)