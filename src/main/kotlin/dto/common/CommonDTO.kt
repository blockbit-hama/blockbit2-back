package com.sg.dto.common

import kotlinx.serialization.Serializable

@Serializable
abstract class CommonResponseDTO {
    abstract val creusr: Int?      // 생성자
    abstract val credat: String?   // 생성일자 (YYYYMMDD)
    abstract val cretim: String?   // 생성시간 (HHMMSS)
    abstract val lmousr: Int?      // 수정자
    abstract val lmodat: String?   // 수정일자 (YYYYMMDD)
    abstract val lmotim: String?   // 수정시간 (HHMMSS)
    abstract val active: String    // 활성여부 ('1'=활성, '0'=비활성)
}

@Serializable
abstract class CommonRequestDTO
