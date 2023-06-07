package com.btssnir.plugins

import com.btssnir.models.security.Login
import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun Application.configureSession() {
    install(Sessions) {
        cookie<Login>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 10
        }
    }
}