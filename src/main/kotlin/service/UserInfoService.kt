package com.sg.service

import com.sg.dto.*
import com.sg.exception.ValidationException
import com.sg.repository.UserInfoRepository
import com.sg.utils.PasswordUtil

class UserInfoService(private val repository: UserInfoRepository = UserInfoRepository()) {
    
    // 모든 사용자 목록 조회
    fun getAllUsers(): List<UserInfoResponseDTO> {
        return repository.getAllUsers()
    }
    
    // 사용자 ID로 사용자 정보 조회
    fun getUserById(id: String): UserInfoResponseDTO? {
        return repository.getUserById(id)
    }
    
    // 사용자 번호로 사용자 정보 조회
    fun getUserByNum(num: Int): UserInfoResponseDTO? {
        return repository.getUserByNum(num)
    }
    
    // 새 사용자 추가
    fun addUser(user: UserInfoDTO): Int {
        // 패스워드 강도 검증
        user.usiPwd?.let { password ->
            if (!PasswordUtil.isValidPassword(password)) {
                throw ValidationException(PasswordUtil.getPasswordStrengthMessage(password))
            }
        }
        
        // 사용자 생성 시간 설정
        val currentDate = DateTimeUtil.getCurrentDate()
        val currentTime = DateTimeUtil.getCurrentTime()
        
        val userWithTimestamp = user.copy(
            credat = currentDate,
            cretim = currentTime,
            lmodat = currentDate,
            lmotim = currentTime
        )
        
        return repository.addUser(userWithTimestamp)
    }
    
    // 사용자 정보 업데이트
    fun updateUser(user: UserInfoDTO): Boolean {
        // 업데이트 시간 설정
        val currentDate = DateTimeUtil.getCurrentDate()
        val currentTime = DateTimeUtil.getCurrentTime()
        
        val userWithTimestamp = user.copy(
            lmodat = currentDate,
            lmotim = currentTime
        )
        
        return repository.updateUser(userWithTimestamp)
    }
    
    // 사용자 비밀번호 변경
    fun changePassword(changePasswordDTO: ChangePasswordDTO): Boolean {
        val user = repository.getUserForAuthentication(changePasswordDTO.usiEmail) ?: return false
        
        // 현재 비밀번호 확인
        if (!PasswordUtil.verifyPassword(changePasswordDTO.currentPassword, user.usiPwd ?: "")) {
            return false
        }
        
        // 새 패스워드 강도 검증
        if (!PasswordUtil.isValidPassword(changePasswordDTO.newPassword)) {
            throw ValidationException(PasswordUtil.getPasswordStrengthMessage(changePasswordDTO.newPassword))
        }
        
        return repository.updatePassword(user.usiNum!!, changePasswordDTO.newPassword)
    }
    
    // 사용자 삭제 (비활성화)
    fun deleteUser(usiNum: Int): Boolean {
        return repository.deleteUser(usiNum)
    }
    
    // 로그인 처리
    fun login(loginDTO: LoginDTO): LoginResponseDTO? {
        val user = repository.getUserForAuthentication(loginDTO.usiEmail) ?: return null
        
        // 비밀번호 검증
        if (!PasswordUtil.verifyPassword(loginDTO.usiPwd, user.usiPwd ?: "")) {
            return LoginResponseDTO(
                usiNum = 0,
                usiEmail = "",
                usiName = "",
                success = false,
                message = "아이디 또는 비밀번호가 일치하지 않습니다.",
                token = null
            )
        }
        
        // 계정 활성화 상태 확인
        if (user.active != "1") {
            return LoginResponseDTO(
                usiNum = 0,
                usiEmail = "",
                usiName = "",
                success = false,
                message = "비활성화된 계정입니다.",
                token = null
            )
        }
        
        // 로그인 시간 업데이트
        val currentDate = DateTimeUtil.getCurrentDate()
        val currentTime = DateTimeUtil.getCurrentTime()
        repository.updateLoginTime(user.usiNum!!, currentDate, currentTime)
        
        // JWT 토큰 생성
        val token = com.sg.utils.JwtUtil.generateToken(user.usiEmail, user.usiNum)
        
        return LoginResponseDTO(
            usiNum = user.usiNum,
            usiEmail = user.usiEmail,
            usiName = user.usiName,
            token = token
        )
    }
}