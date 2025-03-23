package com.sg.services

import com.sg.models.UserInfo
import com.sg.repositories.UserRepository

class UserService(private val repository: UserRepository) {
    suspend fun getAllUsers(): List<UserInfo> {
        return repository.getAll().map { mapToUserResponse(it) }
    }

    suspend fun getUserById(id: Int): UserInfo? {
        return repository.getById(id)?.let { mapToUserResponse(it) }
    }

    suspend fun createUser(request: UserInfo): UserInfo {
        // 비밀번호 해싱 등의 로직이 여기 들어갈 수 있음
        val usi = UserInfo(
            usiId = request.usiId,
            usiPwd = request.usiPwd,
            usiName = request.usiName
        )

        val createdUser = repository.create(usi)
        return mapToUserResponse(createdUser)
    }

    suspend fun updateUser(id: Int, request: UserInfo): Boolean {
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

    private fun mapToUserResponse(usi: UserInfo): UserInfo {
        return UserInfo(
            usiId = usi.usiId,
            usiPwd = usi.usiPwd,
            usiName = usi.usiName
        )
    }
}