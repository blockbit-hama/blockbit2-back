package com.sg.dto

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import com.sg.repository.WalletsTable
import com.sg.dto.common.CommonResponseDTO
import com.sg.dto.common.CommonRequestDTO

@Serializable
data class WalletsRequestDTO(
    val walNum: Int? = null,
    val walName: String,            // 지갑명
    val walType: String,            // 지갑 타입 (Self-custody Hot, Cold, Trading)
    val walProtocol: String,        // 지갑 프로토콜 (MPC, Multisig)
    val walStatus: String,          // 지갑 상태 (frozen, archived, active)
    val astNum: Int? = null,         // 자산 번호 (외래키)
    val polNum: Int? = null          // 정책 번호 (외래키)
) : CommonRequestDTO() {
    init {
        require(walName.isNotBlank()) { "Wallet name cannot be blank" }
        require(walType.isNotBlank()) { "Wallet type cannot be blank" }
        require(walProtocol.isNotBlank()) { "Wallet protocol cannot be blank" }
        require(walStatus.isNotBlank()) { "Wallet status cannot be blank" }
        require(walName.length <= 100) { "Wallet name must be 100 characters or less" }
        require(walType.length <= 50) { "Wallet type must be 50 characters or less" }
        require(walProtocol.length <= 20) { "Wallet protocol must be 20 characters or less" }
        require(walStatus.length <= 20) { "Wallet status must be 20 characters or less" }
        require(walType in listOf("Self-custody Hot", "Cold", "Trading")) { 
            "Wallet type must be one of: Self-custody Hot, Cold, Trading" 
        }
        require(walProtocol in listOf("MPC", "Multisig")) { 
            "Wallet protocol must be one of: MPC, Multisig" 
        }
        require(walStatus in listOf("frozen", "archived", "active")) { 
            "Wallet status must be one of: frozen, archived, active" 
        }
    }
}

@Serializable
data class WalletsResponseDTO(
    val walNum: Int,                // 지갑 번호
    val walName: String,            // 지갑명
    val walType: String,            // 지갑 타입
    val walProtocol: String,        // 지갑 프로토콜
    val walStatus: String,          // 지갑 상태
    val astNum: Int?,                // 자산 번호 (외래키)
    val polNum: Int?,                // 정책 번호 (외래키)
    
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
        fun fromResultRow(row: ResultRow): WalletsResponseDTO {
            return WalletsResponseDTO(
                walNum = row[WalletsTable.walNum],
                walName = row[WalletsTable.walName],
                walType = row[WalletsTable.walType],
                walProtocol = row[WalletsTable.walProtocol],
                walStatus = row[WalletsTable.walStatus],
                astNum = row[WalletsTable.astNum],
                polNum = row[WalletsTable.polNum],
                creusr = row[WalletsTable.creusr],
                credat = row[WalletsTable.credat],
                cretim = row[WalletsTable.cretim],
                lmousr = row[WalletsTable.lmousr],
                lmodat = row[WalletsTable.lmodat],
                lmotim = row[WalletsTable.lmotim],
                active = row[WalletsTable.active]
            )
        }
    }
}
