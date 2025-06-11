package com.sg.service

import com.sg.dto.WalletAddressesRequestDTO
import com.sg.dto.WalletAddressesResponseDTO
import com.sg.repository.WalletAddressesRepository
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WalletAddressesService(
    private val walletAddressesRepository: WalletAddressesRepository
) {
    private val gson = Gson()
    
    fun selectWADList(offset: Int?, limit: Int?, walNum: Int? = null): List<WalletAddressesResponseDTO> {
        return walletAddressesRepository.selectWADList(offset, limit, walNum)
    }
    
    fun selectWAD(wadNum: Int): WalletAddressesResponseDTO? {
        return walletAddressesRepository.selectWAD(wadNum)
    }
    
    fun selectWADByWallet(walNum: Int): List<WalletAddressesResponseDTO> {
        return walletAddressesRepository.selectWADByWallet(walNum)
    }
    
    fun insertWAD(request: WalletAddressesRequestDTO, userId: Int): Int {
        require(request.walNum > 0) { "Valid wallet number is required" }
        require(request.wadAddress.isNotBlank()) { "Wallet address is required" }
        require(request.wadKeyInfo.isNotBlank()) { "Key info is required" }
        
        // JSON 유효성 검증
        validateJsonString(request.wadKeyInfo, "Key info")
        if (request.wadScriptInfo != null) {
            validateJsonString(request.wadScriptInfo, "Script info")
        }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return walletAddressesRepository.insertWAD(
            request = request,
            creusr = userId,
            credat = currentDate,
            cretim = currentTime,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }

    fun updateWAD(requestDTO: WalletAddressesRequestDTO, userId: Int): Boolean {
        require(requestDTO.walNum > 0) { "Valid wallet number is required" }
        require(requestDTO.wadAddress.isNotBlank()) { "Wallet address is required" }
        require(requestDTO.wadKeyInfo.isNotBlank()) { "Key info is required" }
        
        // JSON 유효성 검증
        validateJsonString(requestDTO.wadKeyInfo, "Key info")
        if (requestDTO.wadScriptInfo != null) {
            validateJsonString(requestDTO.wadScriptInfo, "Script info")
        }

        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return walletAddressesRepository.updateWAD(
            requestDTO = requestDTO,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
    
    fun deleteWAD(wadNum: Int, userId: Int): Boolean {
        val now = LocalDateTime.now()
        val currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"))
        
        return walletAddressesRepository.deleteWAD(
            wadNum = wadNum,
            lmousr = userId,
            lmodat = currentDate,
            lmotim = currentTime
        )
    }
    
    private fun validateJsonString(jsonString: String, fieldName: String) {
        try {
            gson.fromJson(jsonString, Any::class.java)
        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("$fieldName must be valid JSON format")
        }
    }
}
