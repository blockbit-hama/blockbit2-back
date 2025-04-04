package com.sg

import com.sg.config.DatabaseFactory
import com.sg.config.configureCORS
import com.sg.config.configureHTTPSRedirect
import com.sg.config.configureRouting
import com.sg.config.configureSerialization
import io.ktor.server.application.*

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
