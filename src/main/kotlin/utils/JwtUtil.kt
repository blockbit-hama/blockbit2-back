package com.sg.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtUtil {
    // 보안을 위해 환경변수나 설정에서 가져오는 것이 좋습니다.
    private val secretKey = "YOUR_SECRET_KEY_SHOULD_BE_LONG_AND_COMPLEX" // 실제 운영에서는 안전하게 관리
    private val algorithm = Algorithm.HMAC256(secretKey)
    private const val EXPIRATION_TIME = 1000 * 60 * 60 * 24 // 24시간

    fun generateToken(usiId: String, usiNum: Int): String {
        return JWT.create()
            .withSubject(usiId)
            .withClaim("usiNum", usiNum)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(algorithm)
    }

    fun jwtVerifier(): JWTVerifier {
        return JWT
            .require(algorithm)
            .build()
    }

    fun validateToken(token: String): Boolean {
        return try {
            jwtVerifier().verify(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserIdFromToken(token: String): String? {
        return try {
            val jwt = jwtVerifier().verify(token)
            jwt.subject
        } catch (e: Exception) {
            null
        }
    }

    fun getUserNumFromToken(token: String): Int? {
        return try {
            val jwt = jwtVerifier().verify(token)
            jwt.getClaim("usiNum").asInt()
        } catch (e: Exception) {
            null
        }
    }
}