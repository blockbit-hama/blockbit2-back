package com.sg.models

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val usiNum: Int? = null,
    val usiId: String? = null,
    val usiPwd: String? = null,
    val usiName: String? = null,
)