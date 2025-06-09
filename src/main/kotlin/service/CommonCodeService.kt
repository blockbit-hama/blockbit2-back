package com.sg.service

import com.sg.dto.CommonCodeRequestDTO
import com.sg.dto.CommonCodeResponseDTO
import com.sg.repository.CommonCodeRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CommonCodeService(
    private val commonCodeRepository: CommonCodeRepository
) {
    
    suspend fun selectCodList(offset: Int, limit: Int): List<CommonCodeResponseDTO> {
        return commonCodeRepository.selectCodList(offset, limit)
    }
    
    suspend fun selectCod(codNum: Int): CommonCodeResponseDTO? {
        return commonCodeRepository.selectCod(codNum)
    }
    
    suspend fun insertCod(request: CommonCodeRequestDTO, userId: Int): Int {
        require(request.codType.isNotBlank()) { "Code type is required" }
        require(request.codKey.isNotBlank()) { "Code key is required" }
        require(request.codVal.isNotBlank()) { "Code value is required" }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return commonCodeRepository.insertCod(
            request = request,
            creusr = userId,
            credat = currentDate,
            cretim = currentTime,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }

    suspend fun updateCod(requestDTO: CommonCodeRequestDTO, userId: Int): Boolean {
        require(requestDTO.codType.isNotBlank()) { "Code type is required" }
        require(requestDTO.codKey.isNotBlank()) { "Code key is required" }
        require(requestDTO.codVal.isNotBlank()) { "Code value is required" }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return commonCodeRepository.updateCod(
            requestDTO = requestDTO,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
    
    suspend fun deleteCod(codNum: Int, userId: Int): Boolean {
        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return commonCodeRepository.deleteCod(
            codNum = codNum,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
}
