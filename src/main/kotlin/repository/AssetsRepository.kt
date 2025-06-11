package com.sg.repository

import com.sg.dto.AssetsRequestDTO
import com.sg.dto.AssetsResponseDTO
import org.jetbrains.exposed.sql.*

object AssetsTable : Table("assets") {
    val astNum = integer("ast_num").autoIncrement()         // 자산 번호 (SERIAL PRIMARY KEY)
    val astName = varchar("ast_name", 100)                  // 자산명
    val astSymbol = varchar("ast_symbol", 20)               // 자산 심볼
    val astType = varchar("ast_type", 20)                   // 자산 타입 ('coin', 'token')
    val astNetwork = varchar("ast_network", 20)             // 네트워크 ('mainnet', 'testnet')
    val astDecimals = integer("ast_decimals").nullable()    // 소수점 자리수
    val creusr = integer("creusr").nullable()               // 생성자
    val credat = char("credat", 8).nullable()               // 생성일자 (YYYYMMDD)
    val cretim = char("cretim", 6).nullable()               // 생성시간 (HHMMSS)
    val lmousr = integer("lmousr").nullable()               // 수정자
    val lmodat = char("lmodat", 8).nullable()               // 수정일자 (YYYYMMDD)
    val lmotim = char("lmotim", 6).nullable()               // 수정시간 (HHMMSS)
    val active = char("active", 1).default("1")             // 활성여부 ('1'=활성, '0'=비활성)

    override val primaryKey = PrimaryKey(astNum)
}

class AssetsRepository {

    fun selectASTList(offset: Int?, limit: Int?): List<AssetsResponseDTO> {
        return AssetsTable
            .select { AssetsTable.active eq "1" }
            .orderBy(AssetsTable.astNum to SortOrder.DESC)
            .let { query ->
                if (offset != null && limit != null) {
                    query.limit(limit, offset.toLong())
                } else {
                    query
                }
            }
            .map { AssetsResponseDTO.fromResultRow(it) }
    }

    fun selectAST(astNum: Int): AssetsResponseDTO? {
        return AssetsTable
            .select { (AssetsTable.astNum eq astNum) and (AssetsTable.active eq "1") }
            .map { AssetsResponseDTO.fromResultRow(it) }
            .singleOrNull()
    }

    fun insertAST(
        request: AssetsRequestDTO,
        creusr: Int,
        credat: String,
        cretim: String,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Int {
        val insertStatement = AssetsTable.insert {
            it[astName] = request.astName
            it[astSymbol] = request.astSymbol
            it[astType] = request.astType
            it[astNetwork] = request.astNetwork
            it[astDecimals] = request.astDecimals
            it[AssetsTable.creusr] = creusr
            it[AssetsTable.credat] = credat
            it[AssetsTable.cretim] = cretim
            it[AssetsTable.lmousr] = lmousr
            it[AssetsTable.lmodat] = lmodat
            it[AssetsTable.lmotim] = lmotim
            it[active] = "1"
        }
        return insertStatement[AssetsTable.astNum]
    }

    fun updateAST(
        requestDTO: AssetsRequestDTO,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Boolean {
        val updateCount = AssetsTable.update(
            where = { (AssetsTable.astNum eq requestDTO.astNum as Int) and (AssetsTable.active eq "1") }
        ) {
            it[astName] = requestDTO.astName
            it[astSymbol] = requestDTO.astSymbol
            it[astType] = requestDTO.astType
            it[astNetwork] = requestDTO.astNetwork
            it[astDecimals] = requestDTO.astDecimals
            it[AssetsTable.lmousr] = lmousr
            it[AssetsTable.lmodat] = lmodat
            it[AssetsTable.lmotim] = lmotim
        }
        return updateCount > 0
    }

    fun deleteAST(astNum: Int, lmousr: Int, lmodat: String, lmotim: String): Boolean {
        val updateCount = AssetsTable.update(
            where = { (AssetsTable.astNum eq astNum) and (AssetsTable.active eq "1") }
        ) {
            it[active] = "0"
            it[AssetsTable.lmousr] = lmousr
            it[AssetsTable.lmodat] = lmodat
            it[AssetsTable.lmotim] = lmotim
        }
        return updateCount > 0
    }
}
