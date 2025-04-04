package com.sg.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sg.utils.JwtUtil
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.jwt.*

fun Application.configureJwtAuthentication() {
    authentication {
        jwt("jwt-auth") {
            realm = "Access to 'protected' resources"
            
            verifier(JwtUtil.jwtVerifier())
            
            validate { credential ->
                val userId = credential.payload.subject
                val userNum = credential.payload.getClaim("usiNum").asInt()
                
                // 실제 프로젝트에서는 여기서 DB에서 추가 검증을 할 수 있습니다.
                if (userId != null && userNum != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}