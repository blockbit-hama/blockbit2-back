package com.sg.services

import com.sg.dtos.UserInfoDTO
import com.sg.repositories.UserRepository

class UserService(private val repository: UserRepository) {
    suspend fun getAllUsers(): List<UserInfoDTO> {
        return repository.getAll().map { mapToUserResponse(it) }
    }

    suspend fun getUserById(id: Int): UserInfoDTO? {
        return repository.getById(id)?.let { mapToUserResponse(it) }
    }

    suspend fun createUser(request: UserInfoDTO): UserInfoDTO {
        // 비밀번호 해싱 등의 로직이 여기 들어갈 수 있음
        val usi = UserInfoDTO(
            usiId = request.usiId,
            usiPwd = request.usiPwd,
            usiName = request.usiName
        )

        val createdUser = repository.create(usi)
        return mapToUserResponse(createdUser)
    }

    suspend fun updateUser(id: Int, request: UserInfoDTO): Boolean {
        val existingUser = repository.getById(id) ?: return false

        val updatedUser = existingUser.copy(
            usiId = request.usiId,
            usiPwd = request.usiPwd,
            usiName = request.usiName
        )

        return repository.update(id, updatedUser)
    }

    suspend fun deleteUser(id: Int): Boolean {
        return repository.delete(id)
    }

    private fun mapToUserResponse(usi: UserInfoDTO): UserInfoDTO {
        return UserInfoDTO(
            usiId = usi.usiId,
            usiPwd = usi.usiPwd,
            usiName = usi.usiName
        )
    }
}