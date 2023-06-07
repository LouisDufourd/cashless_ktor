package com.btssnir.models.security

import com.btssnir.models.Benevole
import com.google.gson.Gson
import io.ktor.server.auth.*

data class LoginBenevole(val benevole: Benevole) : Principal{
    override fun toString(): String {
        return Gson().toJson(this)
    }
}