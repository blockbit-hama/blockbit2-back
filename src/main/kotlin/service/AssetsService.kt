package com.sg.service

import com.sg.config.factory.DatabaseFactory.dbQuery
import com.sg.dto.AssetsRequestDTO
import com.sg.dto.AssetsResponseDTO
import com.sg.repository.AssetsRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AssetsService(
    private val assetsRepository: AssetsRepository
) {
    
    suspend fun selectASTList(offset: Int?, limit: Int?): List<AssetsResponseDTO> = dbQuery {
        assetsRepository.selectASTList(offset, limit)
    }
    
    suspend fun selectAST(astNum: Int): AssetsResponseDTO? = dbQuery {
        assetsRepository.selectAST(astNum)
    }
    
    suspend fun insertAST(request: AssetsRequestDTO, userId: Int): Int = dbQuery {
        require(request.astName.isNotBlank()) { "Asset name is required" }
        require(request.astSymbol.isNotBlank()) { "Asset symbol is required" }
        require(request.astType.isNotBlank()) { "Asset type is required" }
        require(request.astNetwork.isNotBlank()) { "Asset network is required" }
        require(request.astType in listOf("coin", "token")) { "Asset type must be 'coin' or 'token'" }
        require(request.astNetwork in listOf("mainnet", "testnet")) { "Asset network must be 'mainnet' or 'testnet'" }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        assetsRepository.insertAST(
            request = request,
            creusr = userId,
            credat = currentDate,
            cretim = currentTime,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }

    suspend fun updateAST(requestDTO: AssetsRequestDTO, userId: Int): Boolean = dbQuery {
        require(requestDTO.astName.isNotBlank()) { "Asset name is required" }
        require(requestDTO.astSymbol.isNotBlank()) { "Asset symbol is required" }
        require(requestDTO.astType.isNotBlank()) { "Asset type is required" }
        require(requestDTO.astNetwork.isNotBlank()) { "Asset network is required" }
        require(requestDTO.astType in listOf("coin", "token")) { "Asset type must be 'coin' or 'token'" }
        require(requestDTO.astNetwork in listOf("mainnet", "testnet")) { "Asset network must be 'mainnet' or 'testnet'" }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        assetsRepository.updateAST(
            requestDTO = requestDTO,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
    
    suspend fun deleteAST(astNum: Int, userId: Int): Boolean = dbQuery {
        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        assetsRepository.deleteAST(
            astNum = astNum,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
}
