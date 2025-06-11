package com.sg.dto

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import com.sg.repository.WalUsiMappTable
import com.sg.dto.common.CommonResponseDTO
import com.sg.dto.common.CommonRequestDTO

@Serializable
data class WalUsiMappRequestDTO(
    val wumNum: Int? = null,
    val usiNum: Int,                    // 사용자 번호 (user_info FK)
    val walNum: Int,                    // 지갑 번호 (wallets FK)
    val wumRole: String                 // 지갑 역할 ('owner', 'admin', 'viewer', 'signer')
) : CommonRequestDTO() {
    init {
        require(usiNum > 0) { "User number must be positive" }
        require(walNum > 0) { "Wallet number must be positive" }
        require(wumRole.isNotBlank()) { "Wallet role cannot be blank" }
        require(wumRole.length <= 20) { "Wallet role must be 20 characters or less" }
        require(wumRole in listOf("owner", "admin", "viewer", "signer")) { 
            "Invalid role. Must be one of: owner, admin, viewer, signer" 
        }
    }
}

@Serializable
data class WalUsiMappUpdateDTO(
    val wumNum: Int,
    val usiNum: Int,
    val walNum: Int,
    val wumRole: String
) : CommonRequestDTO() {
    init {
        require(wumNum > 0) { "Mapping number must be positive" }
        require(usiNum > 0) { "User number must be positive" }
        require(walNum > 0) { "Wallet number must be positive" }
        require(wumRole.isNotBlank()) { "Wallet role cannot be blank" }
        require(wumRole.length <= 20) { "Wallet role must be 20 characters or less" }
        require(wumRole in listOf("owner", "admin", "viewer", "signer")) { 
            "Invalid role. Must be one of: owner, admin, viewer, signer" 
        }
    }
}

@Serializable
data class WalUsiMappResponseDTO(
    val wumNum: Int,                    // 매핑 번호
    val usiNum: Int,                    // 사용자 번호
    val walNum: Int,                    // 지갑 번호
    val wumRole: String,                // 지갑 역할
    
    // 공통 컬럼
    override val creusr: Int?,           // 생성자
    override val credat: String?,        // 생성일자 (YYYYMMDD)
    override val cretim: String?,        // 생성시간 (HHMMSS)
    override val lmousr: Int?,           // 수정자
    override val lmodat: String?,        // 수정일자 (YYYYMMDD)
    override val lmotim: String?,        // 수정시간 (HHMMSS)
    override val active: String          // 활성여부 ('1'=활성, '0'=비활성)
) : CommonResponseDTO() {
    companion object {
        fun fromResultRow(row: ResultRow): WalUsiMappResponseDTO {
            return WalUsiMappResponseDTO(
                wumNum = row[WalUsiMappTable.wumNum],
                usiNum = row[WalUsiMappTable.usiNum],
                walNum = row[WalUsiMappTable.walNum],
                wumRole = row[WalUsiMappTable.wumRole],
                creusr = row[WalUsiMappTable.creusr],
                credat = row[WalUsiMappTable.credat],
                cretim = row[WalUsiMappTable.cretim],
                lmousr = row[WalUsiMappTable.lmousr],
                lmodat = row[WalUsiMappTable.lmodat],
                lmotim = row[WalUsiMappTable.lmotim],
                active = row[WalUsiMappTable.active]
            )
        }
    }
}
