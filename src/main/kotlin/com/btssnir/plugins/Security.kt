package com.btssnir.plugins

import com.btssnir.bdd_MySql.Gestion
import com.btssnir.models.security.*
import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.response.*

val gestion = Gestion()

fun Application.configureSecurity() {

    authentication {
        basic(name = "userAuth") {
            realm = "Verifier"
            validate { credentials ->
                val user = isUserGoodLogin(credentials.name,credentials.password)
                if(user.isGoodLogin) {
                    LoginUtilisateur(user.utilisateur!!)
                } else {
                    null
                }
            }

        }
        basic(name = "benevolentAuth") {
            realm = "Verifier"
            validate { credentials ->
                val user = isBenevolentLogin(credentials.name, credentials.password)
                if (user.isGoodLogin) {
                    LoginBenevole(user.benevole!!)
                } else {
                    null
                }
            }
        }
        session<Login>("sessionUserAuth") {
            validate {session ->
                if(session.utilisateur != null) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
        session<Login>("sessionBenevolentAuth") {
            validate {session ->
                if(session.benevole != null) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/loginStand")
            }
        }
    }
}

fun isBenevolentLogin(username: String, password: String) : GoodLogin {
    val response = gestion.connexionDuBenevole(username,password)
    return GoodLogin(response.benevole,null,response.isGoodLogin)
}

fun isUserGoodLogin(username : String, password : String) : GoodLogin {
    return if (gestion.connexionDeUtilisateur(username,password)) {
        val result = gestion.getUserIdByName(username,password)
        GoodLogin(null,result,true)
    } else {
        GoodLogin(null,null,false)
    }
}
