package com.sg.service

import com.sg.dto.CommonCodeRequestDTO
import com.sg.dto.CommonCodeResponseDTO
import com.sg.repository.CommonCodeRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CommonCodeService(
    private val commonCodeRepository: CommonCodeRepository
) {
    
    suspend fun selectCODList(offset: Int, limit: Int): List<CommonCodeResponseDTO> {
        return commonCodeRepository.selectCODList(offset, limit)
    }
    
    suspend fun selectCOD(codNum: Int): CommonCodeResponseDTO? {
        return commonCodeRepository.selectCOD(codNum)
    }
    
    suspend fun insertCOD(request: CommonCodeRequestDTO, userId: Int): Int {
        require(request.codType.isNotBlank()) { "Code type is required" }
        require(request.codKey.isNotBlank()) { "Code key is required" }
        require(request.codVal.isNotBlank()) { "Code value is required" }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return commonCodeRepository.insertCOD(
            request = request,
            creusr = userId,
            credat = currentDate,
            cretim = currentTime,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }

    suspend fun updateCOD(requestDTO: CommonCodeRequestDTO, userId: Int): Boolean {
        require(requestDTO.codType.isNotBlank()) { "Code type is required" }
        require(requestDTO.codKey.isNotBlank()) { "Code key is required" }
        require(requestDTO.codVal.isNotBlank()) { "Code value is required" }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return commonCodeRepository.updateCOD(
            requestDTO = requestDTO,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
    
    suspend fun deleteCOD(codNum: Int, userId: Int): Boolean {
        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return commonCodeRepository.deleteCOD(
            codNum = codNum,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
}
