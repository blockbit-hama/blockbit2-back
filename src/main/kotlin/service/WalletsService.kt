package com.sg.service

import com.sg.dto.WalletsRequestDTO
import com.sg.dto.WalletsResponseDTO
import com.sg.repository.WalletsRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WalletsService(
    private val walletsRepository: WalletsRepository
) {
    
    suspend fun selectWALList(offset: Int?, limit: Int?): List<WalletsResponseDTO> {
        return walletsRepository.selectWALList(offset, limit)
    }
    
    suspend fun selectWAL(walNum: Int): WalletsResponseDTO? {
        return walletsRepository.selectWAL(walNum)
    }
    
    suspend fun insertWAL(request: WalletsRequestDTO, userId: Int): Int {
        require(request.walName.isNotBlank()) { "Wallet name is required" }
        require(request.walType.isNotBlank()) { "Wallet type is required" }
        require(request.walProtocol.isNotBlank()) { "Wallet protocol is required" }
        require(request.walStatus.isNotBlank()) { "Wallet status is required" }
        
        // 유효성 검증
        require(request.walType in listOf("Self-custody Hot", "Cold", "Trading")) { 
            "Wallet type must be one of: Self-custody Hot, Cold, Trading" 
        }
        require(request.walProtocol in listOf("MPC", "Multisig")) { 
            "Wallet protocol must be one of: MPC, Multisig" 
        }
        require(request.walStatus in listOf("frozen", "archived", "active")) { 
            "Wallet status must be one of: frozen, archived, active" 
        }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return walletsRepository.insertWAL(
            request = request,
            creusr = userId,
            credat = currentDate,
            cretim = currentTime,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }

    suspend fun updateWAL(requestDTO: WalletsRequestDTO, userId: Int): Boolean {
        require(requestDTO.walName.isNotBlank()) { "Wallet name is required" }
        require(requestDTO.walType.isNotBlank()) { "Wallet type is required" }
        require(requestDTO.walProtocol.isNotBlank()) { "Wallet protocol is required" }
        require(requestDTO.walStatus.isNotBlank()) { "Wallet status is required" }
        
        // 유효성 검증
        require(requestDTO.walType in listOf("Self-custody Hot", "Cold", "Trading")) { 
            "Wallet type must be one of: Self-custody Hot, Cold, Trading" 
        }
        require(requestDTO.walProtocol in listOf("MPC", "Multisig")) { 
            "Wallet protocol must be one of: MPC, Multisig" 
        }
        require(requestDTO.walStatus in listOf("frozen", "archived", "active")) { 
            "Wallet status must be one of: frozen, archived, active" 
        }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return walletsRepository.updateWAL(
            requestDTO = requestDTO,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
    
    suspend fun deleteWAL(walNum: Int, userId: Int): Boolean {
        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return walletsRepository.deleteWAL(
            walNum = walNum,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
}
