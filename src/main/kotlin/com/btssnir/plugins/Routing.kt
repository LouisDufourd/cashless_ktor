package com.btssnir.plugins

import com.btssnir.bdd_MySql.Gestion
import com.btssnir.models.*
import com.btssnir.models.parameters.StockCarteAmount
import com.btssnir.models.response.article.AllArticleResponse
import com.btssnir.models.response.carte.*
import com.btssnir.models.response.stand.StandAddResponse
import com.btssnir.models.response.stand.StandEditResponse
import com.btssnir.models.response.stand.StandHistoriqueResponse
import com.btssnir.models.response.stand.StandRemoveResponse
import com.btssnir.models.response.stock.*
import com.btssnir.models.response.utilisateur.*
import com.btssnir.models.security.Login
import com.btssnir.models.security.LoginBenevole
import com.btssnir.models.security.LoginUtilisateur
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.configureRouting() {
    val gestion = Gestion()
    routing {
        get("/") {
            val session = call.sessions.get<Login>()
            if(session == null) {
                call.respondRedirect("/static/preLog.html")
            } else {
                println("-------------\n$session\n-------------")
                if (session.utilisateur != null) {
                    call.respondRedirect("/static/accueil.html")
                } else if(session.benevole != null) {
                    call.respondRedirect("/static/accueil_stand.html")
                } else {
                    call.sessions.clear<Login>()
                }
            }
        }

        route("/") {
            get("deconnexion") {
                call.sessions.clear<Login>()
                call.respondRedirect("/")
            }
        }

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }

        authenticate("sessionUserAuth") {
            route("/") {
                get("historique") {
                    val login = call.sessions.get<Login>()
                    if(login!!.utilisateur == null) {
                        call.respondRedirect("/login")
                    } else {
                        val utilisateur = gestion.getUserByID(login.utilisateur!!.id!!)
                        if (utilisateur!!.carte != null) {
                            call.run {
                                respond(
                                    FreeMarkerContent(
                                        "client/historique.ftl", mapOf(
                                            "historique" to gestion.historiqueCarte(utilisateur.carte!!.id!!)
                                        )
                                    )
                                )
                            }
                        } else {
                            call.respond(
                                HttpStatusCode.NotFound,
                                "Cette utilisateur n'a aucune carte connecter"
                            )
                        }
                    }
                }
                get("solde") {
                    val login = call.sessions.get<Login>()
                    if(login!!.utilisateur == null) {
                        call.respondRedirect("/login")
                    }
                    val utilisateur = gestion.getUserByID(login.utilisateur!!.id!!)
                    if (utilisateur!!.carte != null) {
                        call.run {
                            respond(FreeMarkerContent("client/index.ftl", mapOf("solde" to utilisateur.carte!!.argent)))
                        }
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            "Cette utilisateur n'a aucune carte connecter"
                        )
                    }
                }
            }
        }
        authenticate("sessionBenevolentAuth") {
            route("/") {
                get("gestion") {
                    val login = call.sessions.get<Login>()
                    if(login!!.benevole == null) {
                        call.respond("/loginStand")
                    }
                    val benevole = gestion.getBenevolentByID(login.benevole!!.id)
                    val stocks = gestion.getStock(benevole!!.stand.idStand!!).stocks
                    call.run {
                        respond(FreeMarkerContent("benevole/gestion.ftl", mapOf("gestion" to stocks)))
                    }
                }
                get("historique_stand") {
                    val login = call.sessions.get<Login>()
                    if(login!!.benevole == null) {
                        call.respond("/loginStand")
                    }
                    val benevole =
                        gestion.getBenevolentByID(login.benevole!!.id)
                    val historiques = gestion.historiqueStand(benevole!!.stand.idStand!!)
                    call.run {
                        respond(FreeMarkerContent("benevole/historique.ftl", mapOf("historique" to historiques)))
                    }
                }
                get("ajouter") {
                    val id = call.parameters["id"]!!.toIntOrNull()
                    if(id!=null) {
                        TODO("NOT implemented yet")
                    } else {
                        call.respond(HttpStatusCode.BadRequest,"Il manque un paramètre")
                    }
                }
                get("modifier") {
                    val quantity = call.parameters["quantity"]!!.toIntOrNull()
                    val price = call.parameters["price"]!!.toDoubleOrNull()
                    val idStand = call.parameters["idStand"]!!.toIntOrNull()
                    val idArticle = call.parameters["idArticle"]!!.toIntOrNull()
                    if(quantity != null && price != null && idStand != null && idArticle != null) {
                        gestion.modifierStock(Stock(Stand(idStand,null), Article(idArticle,null),quantity,price))
                        call.respondRedirect("www.google.com")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Il manque un paramètre")
                    }
                }
            }
        }
        authenticate("benevolentAuth") {
            route("/") {
                get("loginStand") {
                    val login = call.principal<LoginBenevole>()
                    if(login != null) {
                        call.sessions.set(Login(login.benevole,null))
                        call.respondRedirect("/static/accueil_stand.html")
                    } else {
                        call.respondRedirect("/login")
                    }

                }
            }
        }
        authenticate("userAuth") {
            route("/") {
                get("login") {
                    val login = call.principal<LoginUtilisateur>()
                    if(login != null) {
                        call.sessions.set(Login(null,login.utilisateur))
                        call.respondRedirect("/static/accueil.html")
                    } else {
                        call.respondRedirect("/login")
                    }
                }
            }
        }

        route("rest/") {
            get("/") {
                call.respond(HttpStatusCode.OK)
            }
            get("client_connect/{user}/{password}") {
                val user = call.parameters["user"]
                val password = call.parameters["password"]
                if (user != null && password != null) {
                    val response = ClientConnectResponse(
                        gestion.connexionDeUtilisateur(user, password),
                        "l'identifiant et le mots de passe de l'utilisateur est correcte"
                    )
                    if (response.isGoodLogin) {
                        call.respond(HttpStatusCode.OK, response)
                    } else {
                        response.responseString = "l'identifiant ou le mots de passe est incorrecte"
                        call.respond(HttpStatusCode.NotFound, response)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            } //11
            post("client_register") {
                val login = call.receive<Utilisateur>()
                if (gestion.inscriptionUtilisateur(login)) {
                    call.respond(HttpStatusCode.OK, ClientRegisterResponse("L'inscription a été effectué avec succès"))
                } else {
                    call.respond(HttpStatusCode.Found, ClientRegisterResponse("Cette utilisateur est déjà inscrit"))
                }
            } //16
            delete("client_unsubscribe") {
                val login = call.receive<Utilisateur>()
                if (gestion.desinscriptionUtilisateur(login.id!!)) {
                    call.respond(HttpStatusCode.OK, ClientUnsubscribeResponse("L'utilisateur à été supprimer"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "l'utilisateur n'existe pas")
                }
            } //3
            put("client_edit") {
                val login = call.receive<Utilisateur>()
                if (gestion.modifierUtilisateur(login)) {
                    call.respond(HttpStatusCode.OK, ClientEditResponse("L'utilisateur à bien été modifier.ftl"))
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ClientEditResponse("La carte relier à l'utilisateur n'existe pas")
                    )
                }
            } //15
            put("card_connect") {
                val login = call.receive<Utilisateur>()
                val cardConnectResponse = CardConnectResponse(
                    gestion.connecteCarteUtilisateur(login) == 1,
                    "La carte à été connecter à l'utilisateur"
                )
                if (cardConnectResponse.isAttach) {
                    call.respond(HttpStatusCode.OK, cardConnectResponse)
                } else {
                    cardConnectResponse.responseString = "La carte est déjà attacher à un autre utilisateur"
                    call.respond(HttpStatusCode.Found, cardConnectResponse)
                }
            } //14
            post("create_card") {
                val carte = call.receive<Carte>()
                println("salut")
                when (gestion.creeCarte(carte)) {
                    1 -> call.respond(HttpStatusCode.OK)
                    2 -> call.respond(HttpStatusCode.Found, CreateCardResponse("pin already set"))
                    3 -> call.respond(HttpStatusCode.Found, CreateCardResponse("card already added"))
                }
            } //10
            put("modify_card") {
                val carte = call.receive<Carte>()
                if (carte.id != null && carte.argent != null && carte.codeNFC != null && carte.pin != null) {
                    when (gestion.modifierCarte(carte)) {
                        0 -> call.respond(HttpStatusCode.OK, ModifyCardResponse("La carte à été modifier.ftl"))
                        1 -> call.respond(
                            HttpStatusCode.Found,
                            ModifyCardResponse("Le code nfc déjà associer à une autre carte dans la base de donnée")
                        )

                        2 -> call.respond(
                            HttpStatusCode.Found,
                            ModifyCardResponse("Le pin déjà associer à une autre carte dans la base de donnée")
                        )
                    }
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ModifyCardResponse("Impossible de convertir de JSON à Objet, verifier qu'il ne manque rien")
                    )
                }
            } //9
            put("card_debit") {
                val carte = call.receive<Carte>()
                if (carte.codeNFC != null && carte.argent != null && carte.argent!! >= 0) {
                    when (gestion.debiterCarte(carte)) {
                        0 -> call.respond(HttpStatusCode.OK, CardDebitResponse("La carte à été débiter"))
                        1 -> call.respond(
                            HttpStatusCode(452, "not enough money on balance"),
                            CardDebitResponse("Le solde de la carte n'est pas suffisant")
                        )

                        2 -> call.respond(
                            HttpStatusCode.BadRequest,
                            CardDebitResponse("La quantité ne peut être négatif")
                        )
                    }
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        CardDebitResponse("Il manque certain paramètre dans le Json")
                    )
                }
            }//8
            put("card_credit") {
                val carte = call.receive<Carte>()
                if (carte.codeNFC != null && carte.argent != null && carte.argent!! >= 0) {
                    if (gestion.crediterCarte(carte)) {
                        call.respond(HttpStatusCode.OK, CardCreditResponse("La solde de la carte à été modifier.ftl"))
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            CardCreditResponse("La quantité ne peut pas être négatif")
                        )
                    }
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        CardCreditResponse("Il manque certain paramètre dans le Json")
                    )
                }
            }//7
            get("card_balance/{codeNFC}") {
                val codeNFC = call.parameters["codeNFC"]
                call.respond(
                    HttpStatusCode.OK,
                    CardBalanceResponse(gestion.getSolde(codeNFC!!), "Solde de la carte avec le codeNFC : $codeNFC")
                )
            } //0
            delete("delete_card") {
                val carte = call.receive<Carte>()
                if (carte.id != null) {
                    when (gestion.supprimerCarte(carte.id!!)) {
                        -1 -> call.respond(
                            HttpStatusCode.Conflict,
                            DeleteCardResponse("La carte est utiliser dans une autre table")
                        )

                        0 -> call.respond(HttpStatusCode.NotFound, DeleteCardResponse("La carte n'existe pas"))
                        1 -> call.respond(HttpStatusCode.OK, DeleteCardResponse("La carte à été supprimer"))
                    }
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        DeleteCardResponse("Il manque l'id de la carte pour pouvoir la supprimer")
                    )
                }
            } //2
            put("stock_add_article") {
                val stock = call.receive<Stock>()
                if (stock.quantite!! >= 0) {
                    when (gestion.ajouterArticleStand(stock)) {
                        0 -> call.respond(
                            HttpStatusCode.Found,
                            StockAddArticleResponse("l'article n'est pas associer à ce stand")
                        )

                        1 -> call.respond(
                            HttpStatusCode.OK,
                            StockAddArticleResponse("Le nombre d'article à été augmenter")
                        )
                    }
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        StockAddArticleResponse("La quantité ne peut être négative")
                    )
                }
            } //13
            put("stock_remove_article") {
                val stock = call.receive<Stock>()
                if (stock.quantite!! >= 0) {
                    when (gestion.retirerArticleStand(stock)) {
                        -1 -> call.respond(
                            HttpStatusCode(453, "article can't be negate"),
                            StockRemoveArticleResponse("le nombre d'article ne peut pas être négatifs")
                        )

                        0 -> call.respond(
                            HttpStatusCode.NotFound,
                            StockRemoveArticleResponse("Article non associer à ce stand")
                        )

                        1 -> call.respond(
                            HttpStatusCode.OK,
                            StockRemoveArticleResponse("La quantité d'article à été réduite")
                        )
                    }
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        StockAddArticleResponse("La quantité ne peut être négative")
                    )
                }
            } //6
            delete("stock_remove") {
                val stock = call.receive<Stock>()
                when (gestion.suprimerStock(stock)) {
                    0 -> call.respond(HttpStatusCode.NotFound, StockRemoveResponse("Article non associer à ce stand"))
                    1 -> call.respond(HttpStatusCode.OK, StockRemoveResponse("L'article à été supprimer du stand"))
                }
            } //5
            put("stock_edit") {
                val stock = call.receive<Stock>()
                if (stock.prix != null) {
                    when (gestion.modifierStock(stock)) {
                        -2 -> call.respond(HttpStatusCode.BadRequest, StockEditResponse("Le prix ne peut être négatif"))
                        -1 -> call.respond(
                            HttpStatusCode.BadRequest,
                            StockEditResponse("La quantité ne peut être négative")
                        )

                        0 -> call.respond(
                            HttpStatusCode.NotFound,
                            StockEditResponse("cette article n'est pas associer à ce stand")
                        )

                        1 -> call.respond(HttpStatusCode.OK, StockEditResponse("Le stock à été modifier.ftl"))
                    }
                }
            } //12
            post("stock_add") {
                val stock = call.receive<Stock>()
                if (stock.prix != null) {
                    when (gestion.ajouterStock(stock)) {
                        0 -> call.respond(
                            HttpStatusCode.Found,
                            StockAddResponse("Cette article est déjà associer à ce stand")
                        )

                        1 -> call.respond(
                            HttpStatusCode.OK,
                            StockAddResponse("L'article à été ajouter au stock su stand")
                        )
                    }
                }
            } //17
            delete("stand_remove") {
                val stand = call.receive<Stand>()
                when (gestion.suprimerStand(stand)) {
                    -1 -> call.respond(
                        HttpStatusCode.Conflict,
                        StandRemoveResponse("Ce stand est utiliser dans une autre table")
                    )

                    0 -> call.respond(HttpStatusCode.NotFound, StandRemoveResponse("Ce stand n'existe pas"))
                    1 -> call.respond(HttpStatusCode.OK, StandRemoveResponse("Le stand à été supprimer"))
                }
            } //4
            get("stand_historic/{idStand}") {
                val idStand = call.parameters["idStand"]?.toIntOrNull()
                if (idStand != null) {
                    call.respond(HttpStatusCode.OK, StandHistoriqueResponse("", gestion.historiqueStand(idStand)))
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        StandHistoriqueResponse("Le paramètre idStand doit être un entier", null)
                    )
                }
            } //1
            put("stand_edit") {
                val stand = call.receive<Stand>()
                if (stand.standName != null) {
                    when (gestion.modifierStand(stand)) {
                        0 -> call.respond(HttpStatusCode.NotFound, StandEditResponse("Le stand n'existe pas"))
                        1 -> call.respond(HttpStatusCode.OK, StandEditResponse("Le stand à bien été modifier.ftl"))
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, StandEditResponse("Il manque le nom du stand"))
                }
            } //19
            post("stand_add") {
                val stand = call.receive<Stand>()
                if (stand.standName != null) {
                    gestion.ajouterStand(stand)
                    call.respond(HttpStatusCode.OK, StandAddResponse("Le stand à été ajouter"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, StandAddResponse("Il manque le nom du stand"))
                }
            } //18
            get("benevolent_connect/{user}/{password}") {
                val user = call.parameters["user"]
                val password = call.parameters["password"]
                call.respond(HttpStatusCode.OK, gestion.connexionDuBenevole(user!!, password!!))
            } //20
            get("stocks/{standID}") {
                val standID = call.parameters["standID"]?.toIntOrNull()
                if (standID != null) {
                    call.respond(HttpStatusCode.OK, gestion.getStock(standID))
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        GetStocksResponse(null, "Il manque le paramètre standID ou le format du paramètre est invalide")
                    )
                }
            } //21
            get("all_article") {
                call.respond(HttpStatusCode.OK, AllArticleResponse(gestion.getAllArticle()))
            } //22
            put("achat") {
                val stockCarte = call.receive<StockCarteAmount>()
                when (gestion.achat(stockCarte.carte, stockCarte.stock, stockCarte.amount)) {
                    -1 -> call.respond(
                        HttpStatusCode(453, "article can't be negate"),
                        StockRemoveArticleResponse("le nombre d'article ne peut pas être négatifs")
                    )

                    0 -> call.respond(HttpStatusCode.OK)
                    1 -> call.respond(
                        HttpStatusCode(452, "not enough money on balance"),
                        StockRemoveArticleResponse("Le solde de la carte n'est pas suffisant")
                    )

                    2 -> call.respond(
                        HttpStatusCode.BadRequest,
                        StockRemoveArticleResponse("quantite ne peut être négatif")
                    )

                    3 -> call.respond(
                        HttpStatusCode.NotFound,
                        StockRemoveArticleResponse("Article non associer à ce stand")
                    )

                    4 -> call.respond(HttpStatusCode.InternalServerError, StockRemoveArticleResponse("erreur inconnue"))
                    5 -> call.respond(
                        HttpStatusCode.NotFound,
                        StockRemoveArticleResponse("impossible de trouver la carte")
                    )
                }
            }//23
        }
    }
}
