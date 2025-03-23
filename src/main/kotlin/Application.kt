package com.sg

import com.sg.plugins.configureRouting
import com.sg.plugins.configureSerialization
import com.sg.plugins.configureCORS
import com.sg.plugins.configureHTTPSRedirect
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    configureRouting()
    configureSerialization()
    configureCORS()
    configureHTTPSRedirect()
}