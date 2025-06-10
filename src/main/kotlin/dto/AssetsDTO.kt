package com.sg.dto

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import com.sg.repository.AssetsTable
import com.sg.dto.common.CommonResponseDTO
import com.sg.dto.common.CommonRequestDTO

@Serializable
data class AssetsRequestDTO(
    val astNum: Int? = null,
    val astName: String,            // 자산명
    val astSymbol: String,          // 자산 심볼 (예: BTC, ETH)
    val astType: String,            // 자산 타입 ('coin', 'token')
    val astNetwork: String,         // 네트워크 ('mainnet', 'testnet')
    val astDecimals: Int? = null    // 소수점 자리수
) : CommonRequestDTO() {
    init {
        require(astName.isNotBlank()) { "Asset name cannot be blank" }
        require(astSymbol.isNotBlank()) { "Asset symbol cannot be blank" }
        require(astType.isNotBlank()) { "Asset type cannot be blank" }
        require(astNetwork.isNotBlank()) { "Asset network cannot be blank" }
        require(astName.length <= 100) { "Asset name must be 100 characters or less" }
        require(astSymbol.length <= 20) { "Asset symbol must be 20 characters or less" }
        require(astType.length <= 20) { "Asset type must be 20 characters or less" }
        require(astNetwork.length <= 20) { "Asset network must be 20 characters or less" }
        require(astType in listOf("coin", "token")) { "Asset type must be 'coin' or 'token'" }
        require(astNetwork in listOf("mainnet", "testnet")) { "Asset network must be 'mainnet' or 'testnet'" }
        require(astDecimals == null || astDecimals >= 0) { "Asset decimals must be non-negative" }
    }
}

@Serializable
data class AssetsResponseDTO(
    val astNum: Int,                // 자산 번호
    val astName: String,            // 자산명
    val astSymbol: String,          // 자산 심볼
    val astType: String,            // 자산 타입
    val astNetwork: String,         // 네트워크
    val astDecimals: Int?,          // 소수점 자리수
    
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
        fun fromResultRow(row: ResultRow): AssetsResponseDTO {
            return AssetsResponseDTO(
                astNum = row[AssetsTable.astNum],
                astName = row[AssetsTable.astName],
                astSymbol = row[AssetsTable.astSymbol],
                astType = row[AssetsTable.astType],
                astNetwork = row[AssetsTable.astNetwork],
                astDecimals = row[AssetsTable.astDecimals],
                creusr = row[AssetsTable.creusr],
                credat = row[AssetsTable.credat],
                cretim = row[AssetsTable.cretim],
                lmousr = row[AssetsTable.lmousr],
                lmodat = row[AssetsTable.lmodat],
                lmotim = row[AssetsTable.lmotim],
                active = row[AssetsTable.active]
            )
        }
    }
}
