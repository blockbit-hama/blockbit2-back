package com.sg.repository

import com.sg.dto.CommonCodeRequestDTO
import com.sg.dto.CommonCodeResponseDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object CommonCodeTable : Table("common_code") {
    val codNum = integer("cod_num").autoIncrement()      // 코드 번호 (SERIAL PRIMARY KEY)
    val codType = varchar("cod_type", 50)                // 코드 타입
    val codKey = varchar("cod_key", 20)                  // 코드 키
    val codVal = varchar("cod_val", 100)                 // 코드 값
    val codDesc = varchar("cod_desc", 200).nullable()    // 코드 설명
    val creusr = integer("creusr").nullable()            // 생성자
    val credat = char("credat", 8).nullable()            // 생성일자 (YYYYMMDD)
    val cretim = char("cretim", 6).nullable()            // 생성시간 (HHMMSS)
    val lmousr = integer("lmousr").nullable()            // 수정자
    val lmodat = char("lmodat", 8).nullable()            // 수정일자 (YYYYMMDD)
    val lmotim = char("lmotim", 6).nullable()            // 수정시간 (HHMMSS)
    val active = char("active", 1).default("1")          // 활성여부 ('1'=활성, '0'=비활성)

    override val primaryKey = PrimaryKey(codNum)
}

class CommonCodeRepository {

    suspend fun selectCodList(offset: Int, limit: Int): List<CommonCodeResponseDTO> {
        return newSuspendedTransaction {
            val query = CommonCodeTable
                .select { CommonCodeTable.active eq "1" }
                .orderBy(CommonCodeTable.codNum to SortOrder.DESC)
                .limit(limit, offset.toLong())

            query.map { CommonCodeResponseDTO.fromResultRow(it) }
        }
    }

    suspend fun selectCod(codNum: Int): CommonCodeResponseDTO? {
        return newSuspendedTransaction {
            CommonCodeTable
                .select { (CommonCodeTable.codNum eq codNum) and (CommonCodeTable.active eq "1") }
                .map { CommonCodeResponseDTO.fromResultRow(it) }
                .singleOrNull()
        }
    }

    suspend fun insertCod(
        request: CommonCodeRequestDTO,
        creusr: Int,
        credat: String,
        cretim: String,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Int {
        return newSuspendedTransaction {
            val insertStatement = CommonCodeTable.insert {
                it[codType] = request.codType
                it[codKey] = request.codKey
                it[codVal] = request.codVal
                it[codDesc] = request.codDesc
                it[CommonCodeTable.creusr] = creusr
                it[CommonCodeTable.credat] = credat
                it[CommonCodeTable.cretim] = cretim
                it[CommonCodeTable.lmousr] = lmousr
                it[CommonCodeTable.lmodat] = lmodat
                it[CommonCodeTable.lmotim] = lmotim
                it[active] = "1"
            }
            insertStatement[CommonCodeTable.codNum]
        }
    }

    suspend fun updateCod(
        requestDTO: CommonCodeRequestDTO,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Boolean {
        return newSuspendedTransaction {
            val updateCount = CommonCodeTable.update(
                where = { (CommonCodeTable.codNum eq requestDTO.codNum as Int) and (CommonCodeTable.active eq "1") }
            ) {
                it[codType] = requestDTO.codType
                it[codKey] = requestDTO.codKey
                it[codVal] = requestDTO.codVal
                it[codDesc] = requestDTO.codDesc
                it[CommonCodeTable.lmousr] = lmousr
                it[CommonCodeTable.lmodat] = lmodat
                it[CommonCodeTable.lmotim] = lmotim
            }
            updateCount > 0
        }
    }

    suspend fun deleteCod(codNum: Int, lmousr: Int, lmodat: String, lmotim: String): Boolean {
        return newSuspendedTransaction {
            val updateCount = CommonCodeTable.update(
                where = { (CommonCodeTable.codNum eq codNum) and (CommonCodeTable.active eq "1") }
            ) {
                it[active] = "0"
                it[CommonCodeTable.lmousr] = lmousr
                it[CommonCodeTable.lmodat] = lmodat
                it[CommonCodeTable.lmotim] = lmotim
            }
            updateCount > 0
        }
    }
}
