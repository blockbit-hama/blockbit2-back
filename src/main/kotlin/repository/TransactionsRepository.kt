package com.sg.repository

import com.sg.dto.TransactionsRequestDTO
import com.sg.dto.TransactionsResponseDTO
import org.jetbrains.exposed.sql.*
import java.math.BigDecimal

object TransactionsTable : Table("transactions") {
    val trxNum = integer("trx_num").autoIncrement()         // 트랜잭션 번호 (SERIAL PRIMARY KEY)
    val trxToAddr = varchar("trx_to_addr", 255)             // 수신 주소
    val trxAmount = decimal("trx_amount", 20, 8)            // 전송 금액
    val trxFee = decimal("trx_fee", 20, 8).nullable()       // 수수료
    val trxStatus = varchar("trx_status", 20)               // 트랜잭션 상태
    val trxTxId = varchar("trx_tx_id", 64).nullable()       // 블록체인 트랜잭션 ID
    val trxScriptInfo = text("trx_script_info").nullable()  // JSON 형태의 상세 정보
    val trxConfirmedDat = char("trx_confirmed_dat", 8).nullable() // 컨펌 일자 (YYYYMMDD)
    val trxConfirmedTim = char("trx_confirmed_tim", 6).nullable() // 컨펌 시간 (HHMMSS)
    val walNum = integer("wal_num")                         // 지갑 번호
    val wadNum = integer("wad_num")              // 주소 번호
    val creusr = integer("creusr").nullable()               // 생성자
    val credat = char("credat", 8).nullable()               // 생성일자 (YYYYMMDD)
    val cretim = char("cretim", 6).nullable()               // 생성시간 (HHMMSS)
    val lmousr = integer("lmousr").nullable()               // 수정자
    val lmodat = char("lmodat", 8).nullable()               // 수정일자 (YYYYMMDD)
    val lmotim = char("lmotim", 6).nullable()               // 수정시간 (HHMMSS)
    val active = char("active", 1).default("1")             // 활성여부 ('1'=활성, '0'=비활성)

    override val primaryKey = PrimaryKey(trxNum)
}

class TransactionsRepository {

    fun selectTRXList(offset: Int?, limit: Int?): List<TransactionsResponseDTO> {
        return TransactionsTable
            .select { TransactionsTable.active eq "1" }
            .orderBy(TransactionsTable.trxNum to SortOrder.DESC)
            .let { query ->
                if (offset != null && limit != null) {
                    query.limit(limit, offset.toLong())
                } else {
                    query
                }
            }
            .map { TransactionsResponseDTO.fromResultRow(it) }
    }

    fun selectTRX(trxNum: Int): TransactionsResponseDTO? {
        return TransactionsTable
            .select { (TransactionsTable.trxNum eq trxNum) and (TransactionsTable.active eq "1") }
            .map { TransactionsResponseDTO.fromResultRow(it) }
            .singleOrNull()
    }

    fun insertTRX(
        request: TransactionsRequestDTO,
        creusr: Int,
        credat: String,
        cretim: String,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Int {
        val insertStatement = TransactionsTable.insert {
            it[trxToAddr] = request.trxToAddr
            it[trxAmount] = request.trxAmountDecimal
            it[trxFee] = request.trxFeeDecimal
            it[trxStatus] = request.trxStatus
            it[trxTxId] = request.trxTxId
            it[trxScriptInfo] = request.trxScriptInfo
            it[trxConfirmedDat] = request.trxConfirmedDat
            it[trxConfirmedTim] = request.trxConfirmedTim
            it[wadNum] = request.wadNum
            it[TransactionsTable.creusr] = creusr
            it[TransactionsTable.credat] = credat
            it[TransactionsTable.cretim] = cretim
            it[TransactionsTable.lmousr] = lmousr
            it[TransactionsTable.lmodat] = lmodat
            it[TransactionsTable.lmotim] = lmotim
            it[active] = "1"
        }
        return insertStatement[TransactionsTable.trxNum]
    }

    fun updateTRX(
        requestDTO: TransactionsRequestDTO,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Boolean {
        val updateCount = TransactionsTable.update(
            where = { (TransactionsTable.trxNum eq requestDTO.trxNum as Int) and (TransactionsTable.active eq "1") }
        ) {
            it[trxToAddr] = requestDTO.trxToAddr
            it[trxAmount] = requestDTO.trxAmountDecimal
            it[trxFee] = requestDTO.trxFeeDecimal
            it[trxStatus] = requestDTO.trxStatus
            it[trxTxId] = requestDTO.trxTxId
            it[trxScriptInfo] = requestDTO.trxScriptInfo
            it[trxConfirmedDat] = requestDTO.trxConfirmedDat
            it[trxConfirmedTim] = requestDTO.trxConfirmedTim
            it[wadNum] = requestDTO.wadNum
            it[TransactionsTable.lmousr] = lmousr
            it[TransactionsTable.lmodat] = lmodat
            it[TransactionsTable.lmotim] = lmotim
        }
        return updateCount > 0
    }

    fun deleteTRX(trxNum: Int, lmousr: Int, lmodat: String, lmotim: String): Boolean {
        val updateCount = TransactionsTable.update(
            where = { (TransactionsTable.trxNum eq trxNum) and (TransactionsTable.active eq "1") }
        ) {
            it[active] = "0"
            it[TransactionsTable.lmousr] = lmousr
            it[TransactionsTable.lmodat] = lmodat
            it[TransactionsTable.lmotim] = lmotim
        }
        return updateCount > 0
    }
}
