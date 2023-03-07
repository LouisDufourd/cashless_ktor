package com.btssnir.plugins

import com.btssnir.bdd_MySql.Gestion
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val gestion = Gestion()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        route("rest/") {
            get("client_list") {
                call.respond(HttpStatusCode.OK,gestion.lireLesUtilisateurs())
            }
            post("client_connect") {
                val user = call.parameters["user"]
                val password = call.parameters["password"]
                if(user != null && password != null) {
                    if(gestion.connexionDeUtilisateur(user,password)) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post("client_register") {
                val user = call.parameters["user"]
                val password = call.parameters["password"]
                if(user != null && password != null) {
                    if(gestion.inscriptionUtilisateur(user,password)) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Found,"user allready register")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            delete("client_unsubscribe") {
                val id = call.parameters["id"]
                if(id != null) {
                    if(gestion.desinscriptionUtilisateur(id)) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post("client_edit") {
                val id = call.parameters["idClient"]?.toIntOrNull()
                val idCarte = call.parameters["idCarte"]?.toIntOrNull()
                val user = call.parameters["user"]
                val password = call.parameters["password"]
                if(id != null && user != null && password != null) {
                    if(gestion.modifierUtilisateur(id,idCarte,user,password)) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound,"card not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post("card_connect") {
                val pin = call.parameters["pin"]?.toIntOrNull()
                val id = call.parameters["idUtilisateur"]?.toIntOrNull()
                if(pin != null && id != null) {
                    when(gestion.connecteCarteUtilisateur(pin,id)) {
                        0 -> call.respond(HttpStatusCode.NotFound,"user not found")
                        1 -> call.respond(HttpStatusCode.OK)
                        2 -> call.respond(HttpStatusCode.NotFound, "card not found")
                        3 -> call.respond(HttpStatusCode.Found,"card already attach")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post("create_card") {
                val codeNFC = call.parameters["codeNFC"]
                val pin = call.parameters["pin"]?.toIntOrNull()
                if(pin != null && codeNFC != null) {
                    when (gestion.creeCarte(pin,codeNFC)){
                        0 -> call.respond(HttpStatusCode.OK)
                        1 -> call.respond(HttpStatusCode.Found,"pin already set")
                        2 -> call.respond(HttpStatusCode.Found,"card already added")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post("modify_card") {
                val solde = call.parameters["solde"]?.toDoubleOrNull()
                val idCarte = call.parameters["id"]?.toIntOrNull()
                val codeNFC = call.parameters["codeNFC"]
                val pin = call.parameters["pin"]?.toIntOrNull()
                if(idCarte != null && solde != null && codeNFC != null && pin != null) {
                    when(gestion.modifierCarte(solde, idCarte,codeNFC,pin)) {
                        0 -> call.respond(HttpStatusCode.OK)
                        1 -> call.respond(HttpStatusCode.Found,"Code nfc déjà associer à une autre carte dans la base de donnée")
                        2 -> call.respond(HttpStatusCode.Found,"pin déjà associer à une autre carte dans la base de donnée")
                    }
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post("card_debit") {
                val codeNFC = call.parameters["codeNFC"]
                val amount = call.parameters["amount"]?.toDoubleOrNull()
                if(codeNFC != null && amount != null && amount >= 0) {
                    when(gestion.debiterCarte(codeNFC,amount)) {
                        0 -> call.respond(HttpStatusCode.OK)
                        1 -> call.respond(HttpStatusCode(452, "not enough money on balance"), "Le solde de la carte n'est pas suffisant")
                        2 -> call.respond(HttpStatusCode.BadRequest,"La quantité ne peut être négatif")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post("card_credit"){
                val codeNFC = call.parameters["codeNFC"]
                val amount = call.parameters["amount"]?.toDoubleOrNull()
                if(codeNFC != null && amount != null && amount >= 0) {
                    if(gestion.crediterCarte(codeNFC, amount)) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "La quantité ne peut pas être négatif")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("card_balance") {
                val codeNFC = call.parameters["codeNFC"]
                if(codeNFC != null) {
                    call.respond(gestion.getSolde(codeNFC))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            delete("delete_card") {
                val id = call.parameters["idCarte"]?.toIntOrNull()
                if(id != null) {
                    when(gestion.supprimerCarte(id)) {
                        0->call.respond(HttpStatusCode.NotFound, "la carte n'existe pas")
                        1->call.respond(HttpStatusCode.OK)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post("stock_add_article") {
                val idStand = call.parameters["idStand"]?.toIntOrNull()
                val idArticle = call.parameters["idArticle"]?.toIntOrNull()
                val amount = call.parameters["amount"]?.toIntOrNull()
                if(idStand != null && idArticle != null && amount != null && amount >= 0) {
                    when(gestion.ajouterArticleStand(idStand,idArticle,amount)) {
                        0-> call.respond(HttpStatusCode.Found,"article déjà associer à ce stand")
                        1-> call.respond(HttpStatusCode.OK)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            delete("stock_remove_article") {
                val idStand = call.parameters["idStand"]?.toIntOrNull()
                val idArticle = call.parameters["idArticle"]?.toIntOrNull()
                val amount = call.parameters["amount"]?.toIntOrNull()
                if(idStand != null && idArticle != null && amount != null && amount >= 0) {
                    when (gestion.retirerArticleStand(idStand,idArticle,amount)) {
                        -1-> call.respond(HttpStatusCode(453,"article can't be < 0"), "le nombre d'article ne peut pas être négatifs")
                        0 -> call.respond(HttpStatusCode.NotFound, "article non associer à ce stand")
                        1 -> call.respond(HttpStatusCode.OK)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            delete ("stock_remove") {
                val idStand = call.parameters["idStand"]?.toIntOrNull()
                val idArticle = call.parameters["idArticle"]?.toIntOrNull()
                if(idStand != null && idArticle != null) {
                    when(gestion.suprimerStock(idStand, idArticle)) {
                        0 -> call.respond(HttpStatusCode.NotFound, "article non associer à ce stand")
                        1 -> call.respond(HttpStatusCode.OK)
                    }
                }
            }
            post("stock_edit") {
                val idStand = call.parameters["idStand"]?.toIntOrNull()
                val idArticle = call.parameters["idArticle"]?.toIntOrNull()
                val amount = call.parameters["amount"]?.toIntOrNull()
                val price = call.parameters["price"]?.toDoubleOrNull()
                if(idStand != null && idArticle != null && amount != null && price != null) {
                    when(gestion.modifierStock(idStand,idArticle,amount,price)) {
                        0 -> call.respond(HttpStatusCode.NotFound, "article non associer à ce stand")
                        1 -> call.respond(HttpStatusCode.OK)
                    }
                }
            }
            post("stock_add") {
                val idStand = call.parameters["idStand"]?.toIntOrNull()
                val idArticle = call.parameters["idArticle"]?.toIntOrNull()
                val amount = call.parameters["amount"]?.toIntOrNull()
                val price = call.parameters["price"]?.toDoubleOrNull()
                if(idStand != null && idArticle != null && amount != null && price != null) {
                    when(gestion.ajouterStock(idStand,idArticle,amount,price)) {
                        0 -> call.respond(HttpStatusCode.Found, "article déjà associer à ce stand")
                        1 -> call.respond(HttpStatusCode.OK)
                    }
                }
            }
            delete("stand_remove") {
                val idStand = call.parameters["idStand"]?.toIntOrNull()
                if(idStand != null) {
                    when (gestion.suprimerStand(idStand)) {
                        0-> call.respond(HttpStatusCode.NotFound, "Ce stand n'existe pas")
                        1-> call.respond(HttpStatusCode.OK)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("stand_historic") {
                val idStand = call.parameters["idStand"]?.toIntOrNull()
                if(idStand != null) {
                    call.respond(HttpStatusCode.OK,gestion.historiqueStand(idStand))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post("stand_edit") {
                val idStand = call.parameters["idStand"]?.toIntOrNull()
                val nomStand = call.parameters["nomStand"]
                if(idStand != null && nomStand != null) {
                    when(gestion.modifierStand(idStand,nomStand)) {
                        0 -> call.respond(HttpStatusCode.NotFound, "le stand n'existe pas")
                        1 -> call.respond(HttpStatusCode.OK)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}