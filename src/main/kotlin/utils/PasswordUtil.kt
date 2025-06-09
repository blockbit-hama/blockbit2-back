package com.sg.utils

import org.mindrot.jbcrypt.BCrypt

/**
 * 안전한 패스워드 해싱을 위한 유틸리티 클래스
 * BCrypt를 사용하여 Salt와 함께 패스워드를 해싱합니다.
 */
object PasswordUtil {
    
    /**
     * 패스워드를 BCrypt로 해싱합니다.
     * @param password 원본 패스워드
     * @return 해싱된 패스워드 (salt 포함)
     */
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(12))
    }
    
    /**
     * 입력된 패스워드가 해싱된 패스워드와 일치하는지 확인합니다.
     * @param password 확인할 원본 패스워드
     * @param hashedPassword 저장된 해싱된 패스워드
     * @return 패스워드 일치 여부
     */
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return try {
            BCrypt.checkpw(password, hashedPassword)
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 패스워드 강도를 검증합니다.
     * @param password 검증할 패스워드
     * @return 유효한 패스워드인지 여부
     */
    fun isValidPassword(password: String): Boolean {
        // 최소 8자, 대소문자, 숫자, 특수문자 포함
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$".toRegex()
        return password.matches(passwordRegex)
    }
    
    /**
     * 패스워드 강도 메시지를 반환합니다.
     * @param password 검증할 패스워드
     * @return 패스워드 강도에 대한 메시지
     */
    fun getPasswordStrengthMessage(password: String): String {
        return when {
            password.length < 8 -> "패스워드는 최소 8자 이상이어야 합니다."
            !password.any { it.isLowerCase() } -> "패스워드에 소문자가 포함되어야 합니다."
            !password.any { it.isUpperCase() } -> "패스워드에 대문자가 포함되어야 합니다."
            !password.any { it.isDigit() } -> "패스워드에 숫자가 포함되어야 합니다."
            !password.any { it in "@\$!%*?&" } -> "패스워드에 특수문자(@\$!%*?&)가 포함되어야 합니다."
            else -> "강력한 패스워드입니다."
        }
    }
}
