package com.sg.service

import com.sg.dto.PoliciesRequestDTO
import com.sg.dto.PoliciesResponseDTO
import com.sg.repository.PoliciesRepository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PoliciesService(
    private val policiesRepository: PoliciesRepository
) {
    
    fun selectPOLList(offset: Int?, limit: Int?): List<PoliciesResponseDTO> {
        return policiesRepository.selectPOLList(offset, limit)
    }
    
    fun selectPOL(polNum: Int): PoliciesResponseDTO? {
        return policiesRepository.selectPOL(polNum)
    }
    
    fun insertPOL(request: PoliciesRequestDTO, userId: Int): Int {
        require(request.polApprovalThreshold > 0) { "Approval threshold must be positive" }
        require(request.polMaxDailyLimit == null || request.polMaxDailyLimit.toLong() > 0) {
            "Max daily limit must be positive" 
        }
        require(request.polSpendingLimit == null || request.polSpendingLimit.toLong() > 0) {
            "Spending limit must be positive" 
        }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return policiesRepository.insertPOL(
            request = request,
            creusr = userId,
            credat = currentDate,
            cretim = currentTime,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }

    fun updatePOL(requestDTO: PoliciesRequestDTO, userId: Int): Boolean {
        require(requestDTO.polApprovalThreshold > 0) { "Approval threshold must be positive" }
        require(requestDTO.polMaxDailyLimit == null || requestDTO.polMaxDailyLimit.toLong() > 0) {
            "Max daily limit must be positive" 
        }
        require(requestDTO.polSpendingLimit == null || requestDTO.polSpendingLimit.toLong() > 0) {
            "Spending limit must be positive" 
        }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return policiesRepository.updatePOL(
            requestDTO = requestDTO,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
    
    fun deletePOL(polNum: Int, userId: Int): Boolean {
        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return policiesRepository.deletePOL(
            polNum = polNum,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
}
