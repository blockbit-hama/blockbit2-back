package com.sg.repository

import com.sg.dto.WalUsiMappRequestDTO
import com.sg.dto.WalUsiMappResponseDTO
import org.jetbrains.exposed.sql.*

object WalUsiMappTable : Table("wal_usi_mapp") {
    val wumNum = integer("wum_num").autoIncrement()      // 매핑 번호 (SERIAL PRIMARY KEY)
    val usiNum = integer("usi_num")                      // 사용자 번호 (user_info FK)
    val walNum = integer("wal_num")                      // 지갑 번호 (wallets FK)
    val wumRole = varchar("wum_role", 20)                // 지갑 역할
    val creusr = integer("creusr").nullable()            // 생성자
    val credat = char("credat", 8).nullable()            // 생성일자 (YYYYMMDD)
    val cretim = char("cretim", 6).nullable()            // 생성시간 (HHMMSS)
    val lmousr = integer("lmousr").nullable()            // 수정자
    val lmodat = char("lmodat", 8).nullable()            // 수정일자 (YYYYMMDD)
    val lmotim = char("lmotim", 6).nullable()            // 수정시간 (HHMMSS)
    val active = char("active", 1).default("1")          // 활성여부 ('1'=활성, '0'=비활성)

    override val primaryKey = PrimaryKey(wumNum)
    
    init {
        // 복합 유니크 제약조건 (한 사용자는 같은 지갑에 중복 참여 불가)
        uniqueIndex("unique_user_wallet", usiNum, walNum)
    }
}

class WalUsiMappRepository {

    fun selectWUMList(offset: Int?, limit: Int?): List<WalUsiMappResponseDTO> {
        return WalUsiMappTable
            .select { WalUsiMappTable.active eq "1" }
            .orderBy(WalUsiMappTable.wumNum to SortOrder.DESC)
            .let { query ->
                if (offset != null && limit != null) {
                    query.limit(limit, offset.toLong())
                } else {
                    query
                }
            }
            .map { WalUsiMappResponseDTO.fromResultRow(it) }
    }

    fun selectWUM(wumNum: Int): WalUsiMappResponseDTO? {
        return WalUsiMappTable
            .select { (WalUsiMappTable.wumNum eq wumNum) and (WalUsiMappTable.active eq "1") }
            .map { WalUsiMappResponseDTO.fromResultRow(it) }
            .singleOrNull()
    }

    fun insertWUM(
        request: WalUsiMappRequestDTO,
        creusr: Int,
        credat: String,
        cretim: String,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Int {
        val insertStatement = WalUsiMappTable.insert {
            it[usiNum] = request.usiNum
            it[walNum] = request.walNum
            it[wumRole] = request.wumRole
            it[WalUsiMappTable.creusr] = creusr
            it[WalUsiMappTable.credat] = credat
            it[WalUsiMappTable.cretim] = cretim
            it[WalUsiMappTable.lmousr] = lmousr
            it[WalUsiMappTable.lmodat] = lmodat
            it[WalUsiMappTable.lmotim] = lmotim
            it[active] = "1"
        }
        return insertStatement[WalUsiMappTable.wumNum]
    }

    fun updateWUM(
        requestDTO: WalUsiMappRequestDTO,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Boolean {
        val updateCount = WalUsiMappTable.update(
            where = { (WalUsiMappTable.wumNum eq requestDTO.wumNum as Int) and (WalUsiMappTable.active eq "1") }
        ) {
            it[usiNum] = requestDTO.usiNum
            it[walNum] = requestDTO.walNum
            it[wumRole] = requestDTO.wumRole
            it[WalUsiMappTable.lmousr] = lmousr
            it[WalUsiMappTable.lmodat] = lmodat
            it[WalUsiMappTable.lmotim] = lmotim
        }
        return updateCount > 0
    }

    fun deleteWUM(wumNum: Int, lmousr: Int, lmodat: String, lmotim: String): Boolean {
        val updateCount = WalUsiMappTable.update(
            where = { (WalUsiMappTable.wumNum eq wumNum) and (WalUsiMappTable.active eq "1") }
        ) {
            it[active] = "0"
            it[WalUsiMappTable.lmousr] = lmousr
            it[WalUsiMappTable.lmodat] = lmodat
            it[WalUsiMappTable.lmotim] = lmotim
        }
        return updateCount > 0
    }
}
