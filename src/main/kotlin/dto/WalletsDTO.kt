package com.sg.dto

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import com.sg.repository.WalletsTable
import com.sg.repository.WalUsiMappTable
import com.sg.repository.UserInfoTable
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
data class WalletUsersResponseDTO(
    // 지갑 기본 정보
    val walNum: Int,                    // 지갑 번호
    val walName: String,                // 지갑명
    val walType: String,                // 지갑 타입
    val walProtocol: String,            // 지갑 프로토콜
    val walStatus: String,              // 지갑 상태
    val astNum: Int?,                   // 자산 번호
    val polNum: Int?,                   // 정책 번호
    
    // 사용자 정보 (user_info)
    val usiNum: Int,                    // 사용자 번호
    val usiId: String,                  // 사용자 ID
    val usiName: String,                // 사용자 이름
    val usiPhoneNum: String,            // 사용자 전화번호
    val usiEmail: String,               // 사용자 이메일
    val usiLoginDat: String?,           // 로그인일자
    val usiLoginTim: String?,           // 로그인시간
    val usiLastLoginDat: String?,       // 지난로그인일자
    val usiLastLoginTim: String?,       // 지난로그인시간
    
    // 매핑 정보 (wal_usi_mapp)
    val wumNum: Int,                    // 매핑 번호
    val wumRole: String,                // 지갑에서의 역할
    
    // 지갑 공통 컬럼
    val walCreusr: Int?,
    val walCredat: String?,
    val walCretim: String?,
    val walLmousr: Int?,
    val walLmodat: String?,
    val walLmotim: String?,
    val walActive: String,
    
    // 사용자 공통 컬럼
    val usiCreusr: Int?,
    val usiCredat: String?,
    val usiCretim: String?,
    val usiLmousr: Int?,
    val usiLmodat: String?,
    val usiLmotim: String?,
    val usiActive: String,
    
    // 매핑 공통 컬럼
    val wumCreusr: Int?,
    val wumCredat: String?,
    val wumCretim: String?,
    val wumLmousr: Int?,
    val wumLmodat: String?,
    val wumLmotim: String?,
    val wumActive: String
) {
    companion object {
        fun fromJoinedResultRow(row: ResultRow): WalletUsersResponseDTO {
            return WalletUsersResponseDTO(
                // Wallets 정보
                walNum = row[WalletsTable.walNum],
                walName = row[WalletsTable.walName],
                walType = row[WalletsTable.walType],
                walProtocol = row[WalletsTable.walProtocol],
                walStatus = row[WalletsTable.walStatus],
                astNum = row[WalletsTable.astNum],
                polNum = row[WalletsTable.polNum],
                
                // UserInfo 정보  
                usiNum = row[UserInfoTable.usiNum],
                usiId = row[UserInfoTable.usiId],
                usiName = row[UserInfoTable.usiName],
                usiPhoneNum = row[UserInfoTable.usiPhoneNum],
                usiEmail = row[UserInfoTable.usiEmail],
                usiLoginDat = row[UserInfoTable.usiLoginDat],
                usiLoginTim = row[UserInfoTable.usiLoginTim],
                usiLastLoginDat = row[UserInfoTable.usiLastLoginDat],
                usiLastLoginTim = row[UserInfoTable.usiLastLoginTim],
                
                // WalUsiMapp 정보
                wumNum = row[WalUsiMappTable.wumNum],
                wumRole = row[WalUsiMappTable.wumRole],
                
                // Wallets 공통 컬럼
                walCreusr = row[WalletsTable.creusr],
                walCredat = row[WalletsTable.credat],
                walCretim = row[WalletsTable.cretim],
                walLmousr = row[WalletsTable.lmousr],
                walLmodat = row[WalletsTable.lmodat],
                walLmotim = row[WalletsTable.lmotim],
                walActive = row[WalletsTable.active],
                
                // UserInfo 공통 컬럼
                usiCreusr = row[UserInfoTable.creusr],
                usiCredat = row[UserInfoTable.credat],
                usiCretim = row[UserInfoTable.cretim],
                usiLmousr = row[UserInfoTable.lmousr],
                usiLmodat = row[UserInfoTable.lmodat],
                usiLmotim = row[UserInfoTable.lmotim],
                usiActive = row[UserInfoTable.active],
                
                // WalUsiMapp 공통 컬럼
                wumCreusr = row[WalUsiMappTable.creusr],
                wumCredat = row[WalUsiMappTable.credat],
                wumCretim = row[WalUsiMappTable.cretim],
                wumLmousr = row[WalUsiMappTable.lmousr],
                wumLmodat = row[WalUsiMappTable.lmodat],
                wumLmotim = row[WalUsiMappTable.lmotim],
                wumActive = row[WalUsiMappTable.active]
            )
        }
    }
}

@Serializable
data class WalletDetailsResponseDTO(
    // Wallets 테이블 정보 (주요 정보)
    val walNum: Int,                    // 지갑 번호
    val walName: String,                // 지갑명
    val walType: String,                // 지갑 타입
    val walProtocol: String,            // 지갑 프로토콜
    val walStatus: String,              // 지갑 상태
    val astNum: Int?,                   // 자산 번호
    val polNum: Int?,                   // 정책 번호
    
    // WalUsiMapp 테이블 정보 (사용자 관련 정보)
    val wumNum: Int,                    // 매핑 번호
    val usiNum: Int,                    // 사용자 번호
    val wumRole: String,                // 지갑에서의 역할 (owner, admin, viewer 등)
    
    // 지갑 공통 컬럼
    val walCreusr: Int?,
    val walCredat: String?,
    val walCretim: String?,
    val walLmousr: Int?,
    val walLmodat: String?,
    val walLmotim: String?,
    val walActive: String,
    
    // 매핑 공통 컬럼
    val wumCreusr: Int?,
    val wumCredat: String?,
    val wumCretim: String?,
    val wumLmousr: Int?,
    val wumLmodat: String?,
    val wumLmotim: String?,
    val wumActive: String
) {
    companion object {
        fun fromJoinedResultRow(row: ResultRow): WalletDetailsResponseDTO {
            return WalletDetailsResponseDTO(
                // Wallets 정보
                walNum = row[WalletsTable.walNum],
                walName = row[WalletsTable.walName],
                walType = row[WalletsTable.walType],
                walProtocol = row[WalletsTable.walProtocol],
                walStatus = row[WalletsTable.walStatus],
                astNum = row[WalletsTable.astNum],
                polNum = row[WalletsTable.polNum],
                
                // WalUsiMapp 정보
                wumNum = row[WalUsiMappTable.wumNum],
                usiNum = row[WalUsiMappTable.usiNum],
                wumRole = row[WalUsiMappTable.wumRole],
                
                // Wallets 공통 컬럼
                walCreusr = row[WalletsTable.creusr],
                walCredat = row[WalletsTable.credat],
                walCretim = row[WalletsTable.cretim],
                walLmousr = row[WalletsTable.lmousr],
                walLmodat = row[WalletsTable.lmodat],
                walLmotim = row[WalletsTable.lmotim],
                walActive = row[WalletsTable.active],
                
                // WalUsiMapp 공통 컬럼
                wumCreusr = row[WalUsiMappTable.creusr],
                wumCredat = row[WalUsiMappTable.credat],
                wumCretim = row[WalUsiMappTable.cretim],
                wumLmousr = row[WalUsiMappTable.lmousr],
                wumLmodat = row[WalUsiMappTable.lmodat],
                wumLmotim = row[WalUsiMappTable.lmotim],
                wumActive = row[WalUsiMappTable.active]
            )
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
