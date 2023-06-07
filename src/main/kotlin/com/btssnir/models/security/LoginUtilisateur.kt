package com.btssnir.models.security

import com.btssnir.models.Utilisateur
import com.google.gson.Gson
import io.ktor.server.auth.*

data class LoginUtilisateur(val utilisateur: Utilisateur) : Principal {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}