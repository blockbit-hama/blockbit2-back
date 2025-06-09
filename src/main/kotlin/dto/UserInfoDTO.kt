package com.sg.dto

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import com.sg.repository.UserInfoTable
import com.sg.dto.common.CommonResponseDTO
import com.sg.dto.common.CommonRequestDTO
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Serializable
data class UserInfoDTO(
    val usiNum: Int? = null,
    val usiId: String,
    val usiPwd: String? = null,
    val usiName: String,
    val usiPhoneNum: String,
    val usiEmail: String,
    val usiLoginDat: String? = null,
    val usiLoginTim: String? = null,
    val usiLastLoginDat: String? = null,
    val usiLastLoginTim: String? = null,
    
    // 공통 컬럼
    override val creusr: Int? = null,
    override val credat: String? = null,
    override val cretim: String? = null,
    override val lmousr: Int? = null,
    override val lmodat: String? = null,
    override val lmotim: String? = null,
    override val active: String = "1"
) : CommonResponseDTO()

// Password만 변경하는 DTO
@Serializable
data class ChangePasswordDTO(
    val usiEmail: String,
    val currentPassword: String,
    val newPassword: String
) : CommonRequestDTO()

// 사용자 로그인 DTO
@Serializable
data class LoginDTO(
    val usiEmail: String,
    val usiPwd: String
) : CommonRequestDTO()

// 로그인 응답 DTO
@Serializable
data class LoginResponseDTO(
    val usiNum: Int,
    val usiEmail: String,
    val usiName: String,
    val success: Boolean = true,
    val message: String = "로그인 성공",
    val token: String? = null
)

// 비밀번호를 제외한 사용자 정보를 반환하는 DTO
@Serializable
data class UserInfoResponseDTO(
    val usiNum: Int,
    val usiId: String,
    val usiName: String,
    val usiPhoneNum: String,
    val usiEmail: String,
    val usiLoginDat: String? = null,
    val usiLoginTim: String? = null,
    val usiLastLoginDat: String? = null,
    val usiLastLoginTim: String? = null,
    
    // 공통 컬럼
    override val creusr: Int? = null,
    override val credat: String? = null,
    override val cretim: String? = null,
    override val lmousr: Int? = null,
    override val lmodat: String? = null,
    override val lmotim: String? = null,
    override val active: String = "1"
) : CommonResponseDTO() {
    
    companion object {
        fun fromResultRow(row: ResultRow): UserInfoResponseDTO {
            return UserInfoResponseDTO(
                usiNum = row[UserInfoTable.usiNum],
                usiId = row[UserInfoTable.usiId],
                usiName = row[UserInfoTable.usiName],
                usiPhoneNum = row[UserInfoTable.usiPhoneNum],
                usiEmail = row[UserInfoTable.usiEmail],
                usiLoginDat = row[UserInfoTable.usiLoginDat],
                usiLoginTim = row[UserInfoTable.usiLoginTim],
                usiLastLoginDat = row[UserInfoTable.usiLastLoginDat],
                usiLastLoginTim = row[UserInfoTable.usiLastLoginTim],
                creusr = row[UserInfoTable.creusr],
                credat = row[UserInfoTable.credat],
                cretim = row[UserInfoTable.cretim],
                lmousr = row[UserInfoTable.lmousr],
                lmodat = row[UserInfoTable.lmodat],
                lmotim = row[UserInfoTable.lmotim],
                active = row[UserInfoTable.active]
            )
        }
    }
}

// 현재 날짜와 시간을 얻는 유틸리티 함수
object DateTimeUtil {
    fun getCurrentDate(): String {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    }

    fun getCurrentTime(): String {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"))
    }
}
