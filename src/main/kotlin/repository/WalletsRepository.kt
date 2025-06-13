package com.sg.repository

import com.sg.dto.WalletsRequestDTO
import com.sg.dto.WalletsResponseDTO
import com.sg.dto.WalletDetailsResponseDTO
import com.sg.dto.WalletUsersResponseDTO
import org.jetbrains.exposed.sql.*

object WalletsTable : Table("wallets") {
    val walNum = integer("wal_num").autoIncrement()          // 지갑 번호 (SERIAL PRIMARY KEY)
    val walName = varchar("wal_name", 100)                   // 지갑명
    val walType = varchar("wal_type", 50)                    // 지갑 타입
    val walProtocol = varchar("wal_protocol", 20)            // 지갑 프로토콜
    val walStatus = varchar("wal_status", 20)                // 지갑 상태
    val astNum = integer("ast_num").nullable()                 // 자산 번호 (외래키)
    val polNum = integer("pol_num").nullable()                 // 정책 번호 (외래키)
    val creusr = integer("creusr").nullable()                // 생성자
    val credat = char("credat", 8).nullable()                // 생성일자 (YYYYMMDD)
    val cretim = char("cretim", 6).nullable()                // 생성시간 (HHMMSS)
    val lmousr = integer("lmousr").nullable()                // 수정자
    val lmodat = char("lmodat", 8).nullable()                // 수정일자 (YYYYMMDD)
    val lmotim = char("lmotim", 6).nullable()                // 수정시간 (HHMMSS)
    val active = char("active", 1).default("1")              // 활성여부 ('1'=활성, '0'=비활성)

    override val primaryKey = PrimaryKey(walNum)
}

class WalletsRepository {

    fun selectWALList(offset: Int?, limit: Int?): List<WalletsResponseDTO> {
        return WalletsTable
            .select { WalletsTable.active eq "1" }
            .orderBy(WalletsTable.walNum to SortOrder.DESC)
            .let { query ->
                if (offset != null && limit != null) {
                    query.limit(limit, offset.toLong())
                } else {
                    query
                }
            }
            .map { WalletsResponseDTO.fromResultRow(it) }
    }

    fun selectWAL(walNum: Int): WalletsResponseDTO? {
        return WalletsTable
            .select { (WalletsTable.walNum eq walNum) and (WalletsTable.active eq "1") }
            .map { WalletsResponseDTO.fromResultRow(it) }
            .singleOrNull()
    }
    fun insertWAL(
        request: WalletsRequestDTO,
        creusr: Int,
        credat: String,
        cretim: String,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Int {
        val insertStatement = WalletsTable.insert {
            it[walName] = request.walName
            it[walType] = request.walType
            it[walProtocol] = request.walProtocol
            it[walStatus] = request.walStatus
            it[astNum] = request.astNum
            it[polNum] = request.polNum
            it[WalletsTable.creusr] = creusr
            it[WalletsTable.credat] = credat
            it[WalletsTable.cretim] = cretim
            it[WalletsTable.lmousr] = lmousr
            it[WalletsTable.lmodat] = lmodat
            it[WalletsTable.lmotim] = lmotim
            it[active] = "1"
        }
        return insertStatement[WalletsTable.walNum]
    }

    fun updateWAL(
        requestDTO: WalletsRequestDTO,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Boolean {
        val updateCount = WalletsTable.update(
            where = { (WalletsTable.walNum eq requestDTO.walNum as Int) and (WalletsTable.active eq "1") }
        ) {
            it[walName] = requestDTO.walName
            it[walType] = requestDTO.walType
            it[walProtocol] = requestDTO.walProtocol
            it[walStatus] = requestDTO.walStatus
            it[astNum] = requestDTO.astNum
            it[polNum] = requestDTO.polNum
            it[WalletsTable.lmousr] = lmousr
            it[WalletsTable.lmodat] = lmodat
            it[WalletsTable.lmotim] = lmotim
        }
        return updateCount > 0
    }

    fun deleteWAL(walNum: Int, lmousr: Int, lmodat: String, lmotim: String): Boolean {
        val updateCount = WalletsTable.update(
            where = { (WalletsTable.walNum eq walNum) and (WalletsTable.active eq "1") }
        ) {
            it[active] = "0"
            it[WalletsTable.lmousr] = lmousr
            it[WalletsTable.lmodat] = lmodat
            it[WalletsTable.lmotim] = lmotim
        }
        return updateCount > 0
    }

    fun selectWADList(usiNum: Int, offset: Int?, limit: Int?): List<WalletDetailsResponseDTO> {
        return WalletsTable
            .join(WalUsiMappTable, JoinType.INNER, WalletsTable.walNum, WalUsiMappTable.walNum)
            .select { 
                (WalUsiMappTable.usiNum eq usiNum) and 
                (WalletsTable.active eq "1") and 
                (WalUsiMappTable.active eq "1")
            }
            .orderBy(WalletsTable.walNum to SortOrder.DESC)
            .let { query ->
                if (offset != null && limit != null) {
                    query.limit(limit, offset.toLong())
                } else {
                    query
                }
            }
            .map { WalletDetailsResponseDTO.fromJoinedResultRow(it) }
    }

    fun selectWalletUsersList(walNum: Int, offset: Int?, limit: Int?): List<WalletUsersResponseDTO> {
        return WalletsTable
            .join(WalUsiMappTable, JoinType.INNER, WalletsTable.walNum, WalUsiMappTable.walNum)
            .join(UserInfoTable, JoinType.INNER, WalUsiMappTable.usiNum, UserInfoTable.usiNum)
            .select { 
                (WalletsTable.walNum eq walNum) and 
                (WalletsTable.active eq "1") and 
                (WalUsiMappTable.active eq "1") and
                (UserInfoTable.active eq "1")
            }
            .orderBy(WalUsiMappTable.wumNum to SortOrder.DESC)
            .let { query ->
                if (offset != null && limit != null) {
                    query.limit(limit, offset.toLong())
                } else {
                    query
                }
            }
            .map { WalletUsersResponseDTO.fromJoinedResultRow(it) }
    }
}
