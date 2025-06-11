package com.sg.service

import com.sg.dto.TransactionsRequestDTO
import com.sg.dto.TransactionsResponseDTO
import com.sg.repository.TransactionsRepository
import com.sg.utils.DateTimeUtil
import java.math.BigDecimal

class TransactionsService(
    private val transactionsRepository: TransactionsRepository
) {
    
    fun selectTRXList(offset: Int?, limit: Int?): List<TransactionsResponseDTO> {
        return transactionsRepository.selectTRXList(offset, limit)
    }
    
    fun selectTRX(trxNum: Int): TransactionsResponseDTO? {
        return transactionsRepository.selectTRX(trxNum)
    }
    
    fun insertTRX(request: TransactionsRequestDTO, userId: Int): Int {
        require(request.trxToAddr.isNotBlank()) { "Recipient address is required" }
        require(request.trxAmount.isNotBlank()) { "Amount is required" }
        require(request.trxAmountDecimal > BigDecimal.ZERO) { "Amount must be greater than zero" }
        require(request.trxStatus.isNotBlank()) { "Transaction status is required" }
        require(request.trxStatus in listOf("created", "signed", "pending", "confirmed", "failed", "cancelled")) { 
            "Invalid status. Must be one of: created, signed, pending, confirmed, failed, cancelled" 
        }
        require(request.walNum > 0) { "Valid wallet number is required" }

        val currentDate = DateTimeUtil.getCurrentDate()
        val currentTime = DateTimeUtil.getCurrentTime()
        
        return transactionsRepository.insertTRX(
            request = request,
            creusr = userId,
            credat = currentDate,
            cretim = currentTime,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }

    fun updateTRX(requestDTO: TransactionsRequestDTO, userId: Int): Boolean {
        require(requestDTO.trxNum != null && requestDTO.trxNum > 0) { "Valid transaction number is required" }
        require(requestDTO.trxToAddr.isNotBlank()) { "Recipient address is required" }
        require(requestDTO.trxAmount.isNotBlank()) { "Amount is required" }
        require(requestDTO.trxAmountDecimal > BigDecimal.ZERO) { "Amount must be greater than zero" }
        require(requestDTO.trxStatus.isNotBlank()) { "Transaction status is required" }
        require(requestDTO.trxStatus in listOf("created", "signed", "pending", "confirmed", "failed", "cancelled")) { 
            "Invalid status. Must be one of: created, signed, pending, confirmed, failed, cancelled" 
        }
        require(requestDTO.walNum > 0) { "Valid wallet number is required" }

        val currentDate = DateTimeUtil.getCurrentDate()
        val currentTime = DateTimeUtil.getCurrentTime()
        
        return transactionsRepository.updateTRX(
            requestDTO = requestDTO,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
    
    fun deleteTRX(trxNum: Int, userId: Int): Boolean {
        val currentDate = DateTimeUtil.getCurrentDate()
        val currentTime = DateTimeUtil.getCurrentTime()
        
        return transactionsRepository.deleteTRX(
            trxNum = trxNum,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
}
