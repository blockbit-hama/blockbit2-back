package com.sg

import com.sg.config.DatabaseFactory
import com.sg.plugins.configureCORS
import com.sg.plugins.configureHTTPSRedirect
import com.sg.plugins.configureRouting
import com.sg.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String> = emptyArray()) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(environment)
    
    configureRouting()
    configureSerialization()
    configureCORS()
    configureHTTPSRedirect()
}
