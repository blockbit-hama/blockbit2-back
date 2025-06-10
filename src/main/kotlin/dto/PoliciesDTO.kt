package com.sg.dto

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import com.sg.repository.PoliciesTable
import com.sg.dto.common.CommonResponseDTO
import com.sg.dto.common.CommonRequestDTO
import java.math.BigDecimal

@Serializable
data class PoliciesRequestDTO(
    val polNum: Int? = null,
    val polApprovalThreshold: Int,              // 승인 임계값 (필수)
    val polMaxDailyLimit: String? = null,       // 일일 최대 한도 (BigDecimal을 String으로 직렬화)
    val polSpendingLimit: String? = null,       // 지출 한도 (BigDecimal을 String으로 직렬화)
    val polWhitelistOnly: Boolean = false       // 화이트리스트 전용 여부
) : CommonRequestDTO() {
    init {
        require(polApprovalThreshold > 0) { "Approval threshold must be positive" }
        
        // BigDecimal 문자열 검증
        if (polMaxDailyLimit != null) {
            try {
                val limit = BigDecimal(polMaxDailyLimit)
                require(limit > BigDecimal.ZERO) { "Max daily limit must be positive" }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid max daily limit format")
            }
        }
        
        if (polSpendingLimit != null) {
            try {
                val limit = BigDecimal(polSpendingLimit)
                require(limit > BigDecimal.ZERO) { "Spending limit must be positive" }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid spending limit format")
            }
        }
    }
    
    // BigDecimal 변환을 위한 확장 프로퍼티
    val polMaxDailyLimitDecimal: BigDecimal?
        get() = polMaxDailyLimit?.let { BigDecimal(it) }
    
    val polSpendingLimitDecimal: BigDecimal?
        get() = polSpendingLimit?.let { BigDecimal(it) }
}

@Serializable
data class PoliciesResponseDTO(
    val polNum: Int,                            // 정책 번호
    val polApprovalThreshold: Int,              // 승인 임계값
    val polMaxDailyLimit: String?,              // 일일 최대 한도 (BigDecimal을 String으로 직렬화)
    val polSpendingLimit: String?,              // 지출 한도 (BigDecimal을 String으로 직렬화)
    val polWhitelistOnly: Boolean,              // 화이트리스트 전용 여부
    
    // 공통 컬럼
    override val creusr: Int?,                  // 생성자
    override val credat: String?,               // 생성일자 (YYYYMMDD)
    override val cretim: String?,               // 생성시간 (HHMMSS)
    override val lmousr: Int?,                  // 수정자
    override val lmodat: String?,               // 수정일자 (YYYYMMDD)
    override val lmotim: String?,               // 수정시간 (HHMMSS)
    override val active: String                 // 활성여부 ('1'=활성, '0'=비활성)
) : CommonResponseDTO() {
    companion object {
        fun fromResultRow(row: ResultRow): PoliciesResponseDTO {
            return PoliciesResponseDTO(
                polNum = row[PoliciesTable.polNum],
                polApprovalThreshold = row[PoliciesTable.polApprovalThreshold],
                polMaxDailyLimit = row[PoliciesTable.polMaxDailyLimit]?.toString(),
                polSpendingLimit = row[PoliciesTable.polSpendingLimit]?.toString(),
                polWhitelistOnly = row[PoliciesTable.polWhitelistOnly],
                creusr = row[PoliciesTable.creusr],
                credat = row[PoliciesTable.credat],
                cretim = row[PoliciesTable.cretim],
                lmousr = row[PoliciesTable.lmousr],
                lmodat = row[PoliciesTable.lmodat],
                lmotim = row[PoliciesTable.lmotim],
                active = row[PoliciesTable.active]
            )
        }
    }
}
