package com.btssnir.models.security

import com.btssnir.models.Benevole
import com.btssnir.models.Utilisateur
import com.google.gson.Gson
import io.ktor.server.auth.*

data class Login(var benevole: Benevole?, var utilisateur: Utilisateur?) : Principal {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}
