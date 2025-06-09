package com.sg.dto

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import com.sg.repository.CommonCodeTable

@Serializable
data class CommonCodeRequestDTO(
    val codNum: Int? = null,
    val codType: String,        // 코드 타입 (예: docRoleType, status)
    val codKey: String,         // 코드 키 (예: 1, 2, 3)
    val codVal: String,         // 코드 값 (예: Admin, User, Guest)
    val codDesc: String? = null // 코드 설명
) {
    init {
        require(codType.isNotBlank()) { "Code type cannot be blank" }
        require(codKey.isNotBlank()) { "Code key cannot be blank" }
        require(codVal.isNotBlank()) { "Code value cannot be blank" }
        require(codType.length <= 50) { "Code type must be 50 characters or less" }
        require(codKey.length <= 20) { "Code key must be 20 characters or less" }
        require(codVal.length <= 100) { "Code value must be 100 characters or less" }
        require(codDesc == null || codDesc.length <= 200) { "Code description must be 200 characters or less" }
    }
}

@Serializable
data class CommonCodeResponseDTO(
    val codNum: Int,            // 코드 번호
    val codType: String,        // 코드 타입
    val codKey: String,         // 코드 키
    val codVal: String,         // 코드 값
    val codDesc: String?,       // 코드 설명
    
    // 공통 컬럼
    val creusr: Int?,           // 생성자
    val credat: String?,        // 생성일자 (YYYYMMDD)
    val cretim: String?,        // 생성시간 (HHMMSS)
    val lmousr: Int?,           // 수정자
    val lmodat: String?,        // 수정일자 (YYYYMMDD)
    val lmotim: String?,        // 수정시간 (HHMMSS)
    val active: String          // 활성여부 ('1'=활성, '0'=비활성)
) {
    companion object {
        fun fromResultRow(row: ResultRow): CommonCodeResponseDTO {
            return CommonCodeResponseDTO(
                codNum = row[CommonCodeTable.codNum],
                codType = row[CommonCodeTable.codType],
                codKey = row[CommonCodeTable.codKey],
                codVal = row[CommonCodeTable.codVal],
                codDesc = row[CommonCodeTable.codDesc],
                creusr = row[CommonCodeTable.creusr],
                credat = row[CommonCodeTable.credat],
                cretim = row[CommonCodeTable.cretim],
                lmousr = row[CommonCodeTable.lmousr],
                lmodat = row[CommonCodeTable.lmodat],
                lmotim = row[CommonCodeTable.lmotim],
                active = row[CommonCodeTable.active]
            )
        }
    }
}
