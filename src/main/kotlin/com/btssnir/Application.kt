package com.btssnir

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.btssnir.plugins.*

fun main() {
    val environment = applicationEngineEnvironment {
        connector {
            port = 8080
            host = "0.0.0.0"
        }
        module {
            configureRouting()
            configureSerialization()
        }
    }
    embeddedServer(Netty, environment)
        .start(wait = true)
}
