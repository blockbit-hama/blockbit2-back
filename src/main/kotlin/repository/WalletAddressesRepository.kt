package com.sg.repository

import com.sg.dto.WalletAddressesRequestDTO
import com.sg.dto.WalletAddressesResponseDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object WalletAddressesTable : Table("wallet_addresses") {
    val wadNum = integer("wad_num").autoIncrement()             // 월렛 주소 번호 (SERIAL PRIMARY KEY)
    val walNum = integer("wal_num")                             // 월렛 번호 (외래키, NOT NULL)
    val wadAddress = varchar("wad_address", 100)                // 블록체인 주소 (UNIQUE, NOT NULL)
    val wadKeyInfo = text("wad_key_info")                       // 키 정보 (JSONB, NOT NULL)
    val wadScriptInfo = text("wad_script_info").nullable()      // 스크립트/메타정보 (JSONB)
    val creusr = integer("creusr").nullable()                   // 생성자
    val credat = char("credat", 8).nullable()                   // 생성일자 (YYYYMMDD)
    val cretim = char("cretim", 6).nullable()                   // 생성시간 (HHMMSS)
    val lmousr = integer("lmousr").nullable()                   // 수정자
    val lmodat = char("lmodat", 8).nullable()                   // 수정일자 (YYYYMMDD)
    val lmotim = char("lmotim", 6).nullable()                   // 수정시간 (HHMMSS)
    val active = char("active", 1).default("1")                 // 활성여부 ('1'=활성, '0'=비활성)

    override val primaryKey = PrimaryKey(wadNum)
    
    init {
        // UNIQUE 제약조건 설정
        uniqueIndex(wadAddress)
        // 외래키 제약조건은 데이터베이스에서 설정
    }
}

class WalletAddressesRepository {

    suspend fun selectWADList(offset: Int?, limit: Int?, walNum: Int? = null): List<WalletAddressesResponseDTO> {
        return newSuspendedTransaction {
            val query = if (walNum != null) {
                WalletAddressesTable
                    .select { (WalletAddressesTable.active eq "1") and (WalletAddressesTable.walNum eq walNum) }
            } else {
                WalletAddressesTable
                    .select { WalletAddressesTable.active eq "1" }
            }
            
            val orderedQuery = query.orderBy(WalletAddressesTable.wadNum to SortOrder.DESC)
            
            val finalQuery = if (limit != null && offset != null) {
                orderedQuery.limit(limit, offset.toLong())
            } else {
                orderedQuery
            }
            
            finalQuery.map { WalletAddressesResponseDTO.fromResultRow(it) }
        }
    }

    suspend fun selectWAD(wadNum: Int): WalletAddressesResponseDTO? {
        return newSuspendedTransaction {
            WalletAddressesTable
                .select { (WalletAddressesTable.wadNum eq wadNum) and (WalletAddressesTable.active eq "1") }
                .map { WalletAddressesResponseDTO.fromResultRow(it) }
                .singleOrNull()
        }
    }

    suspend fun selectWADByWallet(walNum: Int): List<WalletAddressesResponseDTO> {
        return newSuspendedTransaction {
            WalletAddressesTable
                .select { (WalletAddressesTable.walNum eq walNum) and (WalletAddressesTable.active eq "1") }
                .orderBy(WalletAddressesTable.wadNum to SortOrder.DESC)
                .map { WalletAddressesResponseDTO.fromResultRow(it) }
        }
    }

    suspend fun insertWAD(
        request: WalletAddressesRequestDTO,
        creusr: Int,
        credat: String,
        cretim: String,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Int {
        return newSuspendedTransaction {
            val insertStatement = WalletAddressesTable.insert {
                it[walNum] = request.walNum
                it[wadAddress] = request.wadAddress
                it[wadKeyInfo] = request.wadKeyInfo
                it[wadScriptInfo] = request.wadScriptInfo
                it[WalletAddressesTable.creusr] = creusr
                it[WalletAddressesTable.credat] = credat
                it[WalletAddressesTable.cretim] = cretim
                it[WalletAddressesTable.lmousr] = lmousr
                it[WalletAddressesTable.lmodat] = lmodat
                it[WalletAddressesTable.lmotim] = lmotim
                it[active] = "1"
            }
            insertStatement[WalletAddressesTable.wadNum]
        }
    }

    suspend fun updateWAD(
        requestDTO: WalletAddressesRequestDTO,
        lmousr: Int,
        lmodat: String,
        lmotim: String
    ): Boolean {
        return newSuspendedTransaction {
            val updateCount = WalletAddressesTable.update(
                where = { (WalletAddressesTable.wadNum eq requestDTO.wadNum as Int) and (WalletAddressesTable.active eq "1") }
            ) {
                it[walNum] = requestDTO.walNum
                it[wadAddress] = requestDTO.wadAddress
                it[wadKeyInfo] = requestDTO.wadKeyInfo
                it[wadScriptInfo] = requestDTO.wadScriptInfo
                it[WalletAddressesTable.lmousr] = lmousr
                it[WalletAddressesTable.lmodat] = lmodat
                it[WalletAddressesTable.lmotim] = lmotim
            }
            updateCount > 0
        }
    }

    suspend fun deleteWAD(wadNum: Int, lmousr: Int, lmodat: String, lmotim: String): Boolean {
        return newSuspendedTransaction {
            val updateCount = WalletAddressesTable.update(
                where = { (WalletAddressesTable.wadNum eq wadNum) and (WalletAddressesTable.active eq "1") }
            ) {
                it[active] = "0"
                it[WalletAddressesTable.lmousr] = lmousr
                it[WalletAddressesTable.lmodat] = lmodat
                it[WalletAddressesTable.lmotim] = lmotim
            }
            updateCount > 0
        }
    }

    suspend fun selectWADByAddress(wadAddress: String): WalletAddressesResponseDTO? {
        return newSuspendedTransaction {
            WalletAddressesTable
                .select { (WalletAddressesTable.wadAddress eq wadAddress) and (WalletAddressesTable.active eq "1") }
                .map { WalletAddressesResponseDTO.fromResultRow(it) }
                .singleOrNull()
        }
    }
}
