package com.sg.service

import com.sg.dto.WalUsiMappRequestDTO
import com.sg.dto.WalUsiMappResponseDTO
import com.sg.repository.WalUsiMappRepository
import com.sg.utils.DateTimeUtil

class WalUsiMappService(
    private val walUsiMappRepository: WalUsiMappRepository
) {
    
    fun selectWUMList(offset: Int?, limit: Int?): List<WalUsiMappResponseDTO> {
        return walUsiMappRepository.selectWUMList(offset, limit)
    }
    
    fun selectWUM(wumNum: Int): WalUsiMappResponseDTO? {
        return walUsiMappRepository.selectWUM(wumNum)
    }
    
    fun insertWUM(request: WalUsiMappRequestDTO, userId: Int): Int {
        require(request.usiNum > 0) { "Valid user number is required" }
        require(request.walNum > 0) { "Valid wallet number is required" }
        require(request.wumRole.isNotBlank()) { "Wallet role is required" }
        require(request.wumRole in listOf("owner", "admin", "viewer", "signer")) { 
            "Invalid role. Must be one of: owner, admin, viewer, signer" 
        }

        val currentDate = DateTimeUtil.getCurrentDate()
        val currentTime = DateTimeUtil.getCurrentTime()
        
        return walUsiMappRepository.insertWUM(
            request = request,
            creusr = userId,
            credat = currentDate,
            cretim = currentTime,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }

    fun updateWUM(requestDTO: WalUsiMappRequestDTO, userId: Int): Boolean {
        require(requestDTO.wumNum != null && requestDTO.wumNum > 0) { "Valid mapping number is required" }
        require(requestDTO.usiNum > 0) { "Valid user number is required" }
        require(requestDTO.walNum > 0) { "Valid wallet number is required" }
        require(requestDTO.wumRole.isNotBlank()) { "Wallet role is required" }
        require(requestDTO.wumRole in listOf("owner", "admin", "viewer", "signer")) { 
            "Invalid role. Must be one of: owner, admin, viewer, signer" 
        }

        val currentDate = DateTimeUtil.getCurrentDate()
        val currentTime = DateTimeUtil.getCurrentTime()
        
        return walUsiMappRepository.updateWUM(
            requestDTO = requestDTO,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
    
    fun deleteWUM(wumNum: Int, userId: Int): Boolean {
        val currentDate = DateTimeUtil.getCurrentDate()
        val currentTime = DateTimeUtil.getCurrentTime()
        
        return walUsiMappRepository.deleteWUM(
            wumNum = wumNum,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
}
