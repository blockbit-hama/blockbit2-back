package com.sg.repositories

import com.sg.dtos.UserInfoDTO
import java.sql.Connection
import java.sql.DriverManager

interface UserRepository {
    suspend fun getAll(): List<UserInfoDTO>
    suspend fun getById(id: Int): UserInfoDTO?
    suspend fun create(userInfoDTO: UserInfoDTO): UserInfoDTO
    suspend fun update(id: Int, userInfoDTO: UserInfoDTO): Boolean
    suspend fun delete(id: Int): Boolean
}

class UserRepositoryImpl : UserRepository {
    private val connection: Connection by lazy {
        DriverManager.getConnection(
            "jdbc:postgresql://runtime.co.kr:5432/sbl-report",
            "blockbit",
            "blockbit"
        )
    }

    override suspend fun getAll(): List<UserInfoDTO> {
        val usi = mutableListOf<UserInfoDTO>()
        val statement = connection.prepareStatement("SELECT * FROM USER_INFO")
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            usi.add(
                UserInfoDTO(
                    usiNum = resultSet.getInt("USI_NUM"),
                    usiId = resultSet.getString("USI_ID"),
                    usiPwd = resultSet.getString("USI_PWD"),
                    usiName = resultSet.getString("USI_NAME")
                )
            )
        }

        return usi
    }

    override suspend fun getById(id: Int): UserInfoDTO? {
        val statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            UserInfoDTO(
                usiNum = resultSet.getInt("USI_NUM"),
                usiId = resultSet.getString("USI_ID"),
                usiPwd = resultSet.getString("USI_PWD"),
                usiName = resultSet.getString("USI_NAME")
            )
        } else {
            null
        }
    }

    override suspend fun create(usi: UserInfoDTO): UserInfoDTO {
        val statement = connection.prepareStatement(
            "INSERT INTO users (usiId, usiPwd, usiName) VALUES (?, ?, ?) RETURNING USI_NUM"
        )
        statement.setString(1, usi.usiId)
        statement.setString(2, usi.usiPwd)
        statement.setString(3, usi.usiName)

        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            usi.copy(usiNum = resultSet.getInt("usiNum"))
        } else {
            throw Exception("Failed to create user")
        }
    }

    override suspend fun update(id: Int, usi: UserInfoDTO): Boolean {
        val statement = connection.prepareStatement(
            "UPDATE users SET USI_ID = ?, USI_PWD = ?, USI_NAME = ? WHERE USI_NUM = ?"
        )
        statement.setString(1, usi.usiId)
        statement.setString(2, usi.usiPwd)
        statement.setString(3, usi.usiName)

        return statement.executeUpdate() > 0
    }

    override suspend fun delete(id: Int): Boolean {
        val statement = connection.prepareStatement("DELETE FROM users WHERE USI_NUM = ?")
        statement.setInt(1, id)

        return statement.executeUpdate() > 0
    }
}