package com.sg.dto

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import com.sg.repository.TransactionsTable
import com.sg.dto.common.CommonResponseDTO
import com.sg.dto.common.CommonRequestDTO
import java.math.BigDecimal

@Serializable
data class TransactionsRequestDTO(
    val trxNum: Int? = null,
    val trxToAddr: String,                      // 수신 주소
    val trxAmount: String,                      // 전송 금액 (BigDecimal을 String으로 직렬화)
    val trxFee: String? = null,                 // 수수료 (BigDecimal을 String으로 직렬화)
    val trxStatus: String,                      // 트랜잭션 상태
    val trxTxId: String? = null,                // 블록체인 트랜잭션 ID
    val trxScriptInfo: String? = null,          // JSON 형태의 상세 정보
    val trxConfirmedDat: String? = null,        // 컨펌 일자
    val trxConfirmedTim: String? = null,        // 컨펌 시간
    val walNum: Int,                            // 지갑 번호
    val wadNum: Int                   // 주소 번호
) : CommonRequestDTO() {
    init {
        require(trxToAddr.isNotBlank()) { "Recipient address cannot be blank" }
        require(trxToAddr.length <= 255) { "Recipient address must be 255 characters or less" }
        require(trxAmount.isNotBlank()) { "Amount cannot be blank" }
        require(trxStatus.isNotBlank()) { "Transaction status cannot be blank" }
        require(trxStatus.length <= 20) { "Transaction status must be 20 characters or less" }
        require(trxStatus in listOf("created", "signed", "pending", "confirmed", "failed", "cancelled")) { 
            "Invalid status. Must be one of: created, signed, pending, confirmed, failed, cancelled" 
        }
        require(walNum > 0) { "Wallet number must be positive" }
        require(wadNum == null || wadNum > 0) { "Wallet address number must be positive if provided" }
        require(trxTxId == null || trxTxId.length <= 64) { "Transaction ID must be 64 characters or less" }
        require(trxConfirmedDat == null || trxConfirmedDat.length == 8) { "Confirmed date must be 8 characters (YYYYMMDD)" }
        require(trxConfirmedTim == null || trxConfirmedTim.length == 6) { "Confirmed time must be 6 characters (HHMMSS)" }
        
        // BigDecimal 문자열 검증
        try {
            val amount = BigDecimal(trxAmount)
            require(amount > BigDecimal.ZERO) { "Amount must be greater than zero" }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid amount format")
        }
        
        if (trxFee != null) {
            try {
                val fee = BigDecimal(trxFee)
                require(fee >= BigDecimal.ZERO) { "Fee must be zero or positive" }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid fee format")
            }
        }
    }
    
    // BigDecimal 변환을 위한 확장 프로퍼티
    val trxAmountDecimal: BigDecimal
        get() = BigDecimal(trxAmount)
    
    val trxFeeDecimal: BigDecimal?
        get() = trxFee?.let { BigDecimal(it) }
}

@Serializable
data class TransactionsUpdateDTO(
    val trxNum: Int,
    val trxToAddr: String,
    val trxAmount: String,
    val trxFee: String? = null,
    val trxStatus: String,
    val trxTxId: String? = null,
    val trxScriptInfo: String? = null,
    val trxConfirmedDat: String? = null,
    val trxConfirmedTim: String? = null,
    val walNum: Int,
    val wadNum: Int? = null
) : CommonRequestDTO() {
    init {
        require(trxNum > 0) { "Transaction number must be positive" }
        require(trxToAddr.isNotBlank()) { "Recipient address cannot be blank" }
        require(trxToAddr.length <= 255) { "Recipient address must be 255 characters or less" }
        require(trxAmount.isNotBlank()) { "Amount cannot be blank" }
        require(trxStatus.isNotBlank()) { "Transaction status cannot be blank" }
        require(trxStatus.length <= 20) { "Transaction status must be 20 characters or less" }
        require(trxStatus in listOf("created", "signed", "pending", "confirmed", "failed", "cancelled")) { 
            "Invalid status. Must be one of: created, signed, pending, confirmed, failed, cancelled" 
        }
        require(walNum > 0) { "Wallet number must be positive" }
        require(wadNum == null || wadNum > 0) { "Wallet address number must be positive if provided" }
        
        // BigDecimal 문자열 검증
        try {
            val amount = BigDecimal(trxAmount)
            require(amount > BigDecimal.ZERO) { "Amount must be greater than zero" }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid amount format")
        }
        
        if (trxFee != null) {
            try {
                val fee = BigDecimal(trxFee)
                require(fee >= BigDecimal.ZERO) { "Fee must be zero or positive" }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid fee format")
            }
        }
    }
}

@Serializable
data class TransactionsResponseDTO(
    val trxNum: Int,                            // 트랜잭션 번호
    val trxToAddr: String,                      // 수신 주소
    val trxAmount: String,                      // 전송 금액 (BigDecimal을 String으로 직렬화)
    val trxFee: String?,                        // 수수료 (BigDecimal을 String으로 직렬화)
    val trxStatus: String,                      // 트랜잭션 상태
    val trxTxId: String?,                       // 블록체인 트랜잭션 ID
    val trxScriptInfo: String?,                 // JSON 형태의 상세 정보
    val trxConfirmedDat: String?,               // 컨펌 일자
    val trxConfirmedTim: String?,               // 컨펌 시간
    val walNum: Int,                            // 지갑 번호
    val wadNum: Int,                           // 주소 번호
    
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
        fun fromResultRow(row: ResultRow): TransactionsResponseDTO {
            return TransactionsResponseDTO(
                trxNum = row[TransactionsTable.trxNum],
                trxToAddr = row[TransactionsTable.trxToAddr],
                trxAmount = row[TransactionsTable.trxAmount].toString(),
                trxFee = row[TransactionsTable.trxFee]?.toString(),
                trxStatus = row[TransactionsTable.trxStatus],
                trxTxId = row[TransactionsTable.trxTxId],
                trxScriptInfo = row[TransactionsTable.trxScriptInfo],
                trxConfirmedDat = row[TransactionsTable.trxConfirmedDat],
                trxConfirmedTim = row[TransactionsTable.trxConfirmedTim],
                walNum = row[TransactionsTable.walNum],
                wadNum = row[TransactionsTable.wadNum],
                creusr = row[TransactionsTable.creusr],
                credat = row[TransactionsTable.credat],
                cretim = row[TransactionsTable.cretim],
                lmousr = row[TransactionsTable.lmousr],
                lmodat = row[TransactionsTable.lmodat],
                lmotim = row[TransactionsTable.lmotim],
                active = row[TransactionsTable.active]
            )
        }
    }
}
