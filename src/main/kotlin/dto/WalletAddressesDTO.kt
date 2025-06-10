package com.sg.dto

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import com.sg.repository.WalletAddressesTable
import com.sg.dto.common.CommonResponseDTO
import com.sg.dto.common.CommonRequestDTO
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

@Serializable
data class WalletAddressesRequestDTO(
    val wadNum: Int? = null,
    val walNum: Int,                    // 월렛 번호 (외래키, 필수)
    val wadAddress: String,             // 블록체인 주소 (필수, 유니크)
    val wadKeyInfo: String,             // 키 정보 (JSONB, 필수)
    val wadScriptInfo: String? = null   // 스크립트/메타정보 (JSONB, 선택)
) : CommonRequestDTO() {
    companion object {
        private val gson = Gson()
    }
    
    init {
        require(walNum > 0) { "Valid wallet number is required" }
        require(wadAddress.isNotBlank()) { "Wallet address cannot be blank" }
        require(wadKeyInfo.isNotBlank()) { "Key info cannot be blank" }
        require(wadAddress.length <= 100) { "Wallet address must be 100 characters or less" }
        
        // JSON 유효성 검증
        try {
            gson.fromJson(wadKeyInfo, Any::class.java)
        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Key info must be valid JSON format")
        }
        
        if (wadScriptInfo != null) {
            try {
                gson.fromJson(wadScriptInfo, Any::class.java)
            } catch (e: JsonSyntaxException) {
                throw IllegalArgumentException("Script info must be valid JSON format")
            }
        }
    }
}

@Serializable
data class WalletAddressesResponseDTO(
    val wadNum: Int,                    // 월렛 주소 번호
    val walNum: Int,                    // 월렛 번호
    val wadAddress: String,             // 블록체인 주소
    val wadKeyInfo: String,             // 키 정보 (JSON 문자열)
    val wadScriptInfo: String?,         // 스크립트/메타정보 (JSON 문자열)
    
    // 공통 컬럼
    override val creusr: Int?,          // 생성자
    override val credat: String?,       // 생성일자 (YYYYMMDD)
    override val cretim: String?,       // 생성시간 (HHMMSS)
    override val lmousr: Int?,          // 수정자
    override val lmodat: String?,       // 수정일자 (YYYYMMDD)
    override val lmotim: String?,       // 수정시간 (HHMMSS)
    override val active: String         // 활성여부 ('1'=활성, '0'=비활성)
) : CommonResponseDTO() {
    companion object {
        fun fromResultRow(row: ResultRow): WalletAddressesResponseDTO {
            return WalletAddressesResponseDTO(
                wadNum = row[WalletAddressesTable.wadNum],
                walNum = row[WalletAddressesTable.walNum],
                wadAddress = row[WalletAddressesTable.wadAddress],
                wadKeyInfo = row[WalletAddressesTable.wadKeyInfo],
                wadScriptInfo = row[WalletAddressesTable.wadScriptInfo],
                creusr = row[WalletAddressesTable.creusr],
                credat = row[WalletAddressesTable.credat],
                cretim = row[WalletAddressesTable.cretim],
                lmousr = row[WalletAddressesTable.lmousr],
                lmodat = row[WalletAddressesTable.lmodat],
                lmotim = row[WalletAddressesTable.lmotim],
                active = row[WalletAddressesTable.active]
            )
        }
    }
    
    // JSON 파싱을 위한 유틸리티 메서드
    fun getKeyInfoAsMap(): Map<String, Any>? {
        return try {
            Gson().fromJson(wadKeyInfo, Map::class.java) as? Map<String, Any>
        } catch (e: Exception) {
            null
        }
    }
    
    fun getScriptInfoAsMap(): Map<String, Any>? {
        return try {
            wadScriptInfo?.let { Gson().fromJson(it, Map::class.java) as? Map<String, Any> }
        } catch (e: Exception) {
            null
        }
    }
}
