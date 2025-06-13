package com.sg.service

import com.sg.dto.AssetsRequestDTO
import com.sg.dto.AssetsResponseDTO
import com.sg.repository.AssetsRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AssetsService(
    private val assetsRepository: AssetsRepository
) {
    
    fun selectASTList(offset: Int?, limit: Int?): List<AssetsResponseDTO> {
        return assetsRepository.selectASTList(offset, limit)
    }
    
    fun selectAST(astNum: Int): AssetsResponseDTO? {
        return assetsRepository.selectAST(astNum)
    }
    
    fun insertAST(request: AssetsRequestDTO, userId: Int): Int {
        require(request.astName.isNotBlank()) { "Asset name is required" }
        require(request.astSymbol.isNotBlank()) { "Asset symbol is required" }
        require(request.astType.isNotBlank()) { "Asset type is required" }
        require(request.astNetwork.isNotBlank()) { "Asset network is required" }
        require(request.astType in listOf("coin", "token")) { "Asset type must be 'coin' or 'token'" }
        require(request.astNetwork in listOf("mainnet", "testnet")) { "Asset network must be 'mainnet' or 'testnet'" }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return assetsRepository.insertAST(
            request = request,
            creusr = userId,
            credat = currentDate,
            cretim = currentTime,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }

    fun updateAST(requestDTO: AssetsRequestDTO, userId: Int): Boolean {
        require(requestDTO.astName.isNotBlank()) { "Asset name is required" }
        require(requestDTO.astSymbol.isNotBlank()) { "Asset symbol is required" }
        require(requestDTO.astType.isNotBlank()) { "Asset type is required" }
        require(requestDTO.astNetwork.isNotBlank()) { "Asset network is required" }
        require(requestDTO.astType in listOf("coin", "token")) { "Asset type must be 'coin' or 'token'" }
        require(requestDTO.astNetwork in listOf("mainnet", "testnet")) { "Asset network must be 'mainnet' or 'testnet'" }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return assetsRepository.updateAST(
            requestDTO = requestDTO,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
    
    fun deleteAST(astNum: Int, userId: Int): Boolean {
        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return assetsRepository.deleteAST(
            astNum = astNum,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
}
