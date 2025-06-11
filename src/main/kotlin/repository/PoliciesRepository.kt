package com.sg.repository

import com.sg.dto.PoliciesRequestDTO
import com.sg.dto.PoliciesResponseDTO
import org.jetbrains.exposed.sql.*
import java.math.BigDecimal

object PoliciesTable : Table("policies") {
    val polNum = integer("pol_num").autoIncrement()                     // 정책 번호 (SERIAL PRIMARY KEY)
    val polApprovalThreshold = integer("pol_approval_threshold")        // 승인 임계값 (NOT NULL)
    val polMaxDailyLimit = decimal("pol_max_daily_limit", 20, 8).nullable()  // 일일 최대 한도
    val polSpendingLimit = decimal("pol_spending_limit", 20, 8).nullable()   // 지출 한도
    val polWhitelistOnly = bool("pol_whitelist_only").default(false)    // 화이트리스트 전용 여부
    val creusr = integer("creusr").nullable()                           // 생성자
    val credat = char("credat", 8).nullable()                           // 생성일자 (YYYYMMDD)
    val cretim = char("cretim", 6).nullable()                           // 생성시간 (HHMMSS)
    val lmousr = integer("lmousr").nullable()                           // 수정자
    val lmodat = char("lmodat", 8).nullable()                           // 수정일자 (YYYYMMDD)
    val lmotim = char("lmotim", 6).nullable()                           // 수정시간 (HHMMSS)
    val active = char("active", 1).default("1")                         // 활성여부 ('1'=활성, '0'=비활성)

    override val primaryKey = PrimaryKey(polNum)
}

class PoliciesRepository {

    fun selectPOLList(offset: Int?, limit: Int?): List<PoliciesResponseDTO> {
        return PoliciesTable
            .select { PoliciesTable.active eq "1" }
            .orderBy(PoliciesTable.polNum to SortOrder.DESC)
            .let { query ->
                if (offset != null && limit != null) {
                    query.limit(limit, offset.toLong())
                } else {
                    query
                }
            }
            .map { PoliciesResponseDTO.fromResultRow(it) }
    }

    fun selectPOL(polNum: Int): PoliciesResponseDTO? {
        return PoliciesTable
            .select { (PoliciesTable.polNum eq polNum) and (PoliciesTable.active eq "1") }
            .map { PoliciesResponseDTO.fromResultRow(it) }
            .singleOrNull()
    }

    fun insertPOL(
        request: PoliciesRequestDTO,
        creusr: Int,
        credat: String,
        cretim: String,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Int {
        val insertStatement = PoliciesTable.insert {
            it[polApprovalThreshold] = request.polApprovalThreshold
            it[polMaxDailyLimit] = request.polMaxDailyLimitDecimal
            it[polSpendingLimit] = request.polSpendingLimitDecimal
            it[polWhitelistOnly] = request.polWhitelistOnly
            it[PoliciesTable.creusr] = creusr
            it[PoliciesTable.credat] = credat
            it[PoliciesTable.cretim] = cretim
            it[PoliciesTable.lmousr] = lmousr
            it[PoliciesTable.lmodat] = lmodat
            it[PoliciesTable.lmotim] = lmotim
            it[active] = "1"
        }
        return insertStatement[PoliciesTable.polNum]
    }

    fun updatePOL(
        requestDTO: PoliciesRequestDTO,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Boolean {
        val updateCount = PoliciesTable.update(
            where = { (PoliciesTable.polNum eq requestDTO.polNum as Int) and (PoliciesTable.active eq "1") }
        ) {
            it[polApprovalThreshold] = requestDTO.polApprovalThreshold
            it[polMaxDailyLimit] = requestDTO.polMaxDailyLimitDecimal
            it[polSpendingLimit] = requestDTO.polSpendingLimitDecimal
            it[polWhitelistOnly] = requestDTO.polWhitelistOnly
            it[PoliciesTable.lmousr] = lmousr
            it[PoliciesTable.lmodat] = lmodat
            it[PoliciesTable.lmotim] = lmotim
        }
        return updateCount > 0
    }

    fun deletePOL(polNum: Int, lmousr: Int, lmodat: String, lmotim: String): Boolean {
        val updateCount = PoliciesTable.update(
            where = { (PoliciesTable.polNum eq polNum) and (PoliciesTable.active eq "1") }
        ) {
            it[active] = "0"
            it[PoliciesTable.lmousr] = lmousr
            it[PoliciesTable.lmodat] = lmodat
            it[PoliciesTable.lmotim] = lmotim
        }
        return updateCount > 0
    }
}
