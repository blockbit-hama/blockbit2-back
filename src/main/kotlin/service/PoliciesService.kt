package com.sg.service

import com.sg.config.factory.DatabaseFactory.dbQuery
import com.sg.dto.PoliciesRequestDTO
import com.sg.dto.PoliciesResponseDTO
import com.sg.repository.PoliciesRepository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PoliciesService(
    private val policiesRepository: PoliciesRepository
) {
    
    suspend fun selectPOLList(offset: Int?, limit: Int?): List<PoliciesResponseDTO> = dbQuery {
        policiesRepository.selectPOLList(offset, limit)
    }
    
    suspend fun selectPOL(polNum: Int): PoliciesResponseDTO? = dbQuery {
        policiesRepository.selectPOL(polNum)
    }
    
    suspend fun insertPOL(request: PoliciesRequestDTO, userId: Int): Int = dbQuery {
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
        
        policiesRepository.insertPOL(
            request = request,
            creusr = userId,
            credat = currentDate,
            cretim = currentTime,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }

    suspend fun updatePOL(requestDTO: PoliciesRequestDTO, userId: Int): Boolean = dbQuery {
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
        
        policiesRepository.updatePOL(
            requestDTO = requestDTO,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
    
    suspend fun deletePOL(polNum: Int, userId: Int): Boolean = dbQuery {
        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        policiesRepository.deletePOL(
            polNum = polNum,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
}
