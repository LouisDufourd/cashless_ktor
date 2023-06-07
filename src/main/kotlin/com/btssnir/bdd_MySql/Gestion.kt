package com.btssnir.bdd_MySql

import com.btssnir.models.*
import com.btssnir.models.response.benevole.BenevolentConnectResponse
import com.btssnir.models.response.stock.GetStocksResponse
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLIntegrityConstraintViolationException


class Gestion() {
    var laConnexion = Connexion("jdbc:mysql://10.0.0.111/mydb", "root", "root")

    fun connexionDeUtilisateur(user:String, password:String): Boolean {
        val prepStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM `utilisateur` WHERE user = ? and password = ?")
        prepStatement.setString(1,user)
        prepStatement.setString(2,password)
        val rs = prepStatement.executeQuery()
        while (rs.next()) {
            return true
        }
        return false
    }

    fun inscriptionUtilisateur(utilisateur: Utilisateur) : Boolean{
        var prepStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM `utilisateur` WHERE user = ?")
        prepStatement.setString(1,utilisateur.user)
        val rs = prepStatement.executeQuery()
        while (rs.next()) {
            return false
        }
        prepStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO `utilisateur` (`id_Utilisateur`, `Carte_id_Carte`, `user`, `password`) VALUES (NULL, NULL, ?, ?)")
        prepStatement.setString(1,utilisateur.user)
        prepStatement.setString(2,utilisateur.password)
        prepStatement.executeUpdate()
        return true
    }

    fun desinscriptionUtilisateur(id: Int) : Boolean {
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM utilisateur WHERE id_Utilisateur=?")
        preparedStatement.setInt(1,id)
        val rs = preparedStatement.executeQuery()
        while (!rs.next()) {
            return false
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "DELETE FROM `utilisateur` WHERE id_Utilisateur = ?")
        preparedStatement.setInt(1,id)
        return preparedStatement.executeUpdate() == 1
    }

    fun connecteCarteUtilisateur(utilisateur: Utilisateur) : Int{
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id_Carte FROM carte WHERE pin = ?")
        preparedStatement.setInt(1,utilisateur.carte!!.pin!!)
        var rs = preparedStatement.executeQuery()
        utilisateur.carte.id = -1
        while (rs.next()) {
            utilisateur.carte.id = rs.getInt("id_Carte")
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM utilisateur WHERE Carte_id_Carte = ?")
        preparedStatement.setInt(1, utilisateur.carte.id!!)
        rs = preparedStatement.executeQuery()
        while (rs.next()) {
            return 0
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE `utilisateur` SET `Carte_id_Carte`=? WHERE id_Utilisateur = ?")
        preparedStatement.setInt(1,utilisateur.carte.id!!)
        preparedStatement.setInt(2,utilisateur.id!!)
        return preparedStatement.executeUpdate()
    }

    fun creeUtilisateur(rs:ResultSet) : Utilisateur {
        val utilisateur : Utilisateur
        if(rs.getString("codeNFC") == null) {
            utilisateur = Utilisateur(rs.getInt("id_Utilisateur"),null,rs.getString("user"),rs.getString("password"))
        } else {
            utilisateur = Utilisateur(rs.getInt("id_Utilisateur"), Carte(rs.getInt("id_Carte"),rs.getInt("pin"),rs.getDouble("argent"),rs.getString("codeNFC")),rs.getString("user"),rs.getString("password"))
        }
        return utilisateur
    }

    fun modifierUtilisateur(utilisateur: Utilisateur) : Boolean {
        var preparedStatement:PreparedStatement
        if(utilisateur.carte == null) { // Si l'identifiant de la carte est null, elle met à jour le nom d'utilisateur et le mot de passe de l'utilisateur dont l'identifiant est id.
            preparedStatement = laConnexion.getConnexion().prepareStatement(
                "UPDATE `utilisateur` SET `user`= ? ,`password`= ? WHERE id_Utilisateur = ?")
            preparedStatement.setString(1,utilisateur.user)
            preparedStatement.setString(2,utilisateur.password)
            preparedStatement.setInt(3,utilisateur.id!!)
            preparedStatement.executeUpdate()
            return true
        } else {
            // Sinon, elle vérifie que l'identifiant de la carte existe dans la table 'carte' et met à jour les informations de l'utilisateur en conséquence.
            preparedStatement = laConnexion.getConnexion().prepareStatement(
                "SELECT NULL FROM carte WHERE id_Carte = ?")
            preparedStatement.setInt(1,utilisateur.carte.id!!)
            val rs = preparedStatement.executeQuery()
            while (!rs.next()) {
                return false
            }
            preparedStatement = laConnexion.getConnexion().prepareStatement(
                "UPDATE `utilisateur` SET `Carte_id_Carte`= ? ,`user`= ? ,`password`= ? WHERE id_Utilisateur= ?")
            preparedStatement.setInt(1, utilisateur.carte.id!!)
            preparedStatement.setString(2,utilisateur.user)
            preparedStatement.setString(3,utilisateur.password)
            preparedStatement.setInt(4,utilisateur.id!!)
            preparedStatement.executeUpdate()
            return true
        }
    }

    fun creeCarte(carte: Carte):Int {
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM `carte` WHERE carte.pin = ?")
        preparedStatement.setInt(1,carte.pin!!)
        var rs = preparedStatement.executeQuery()
        while (rs.next()){
            return 2
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM `carte` WHERE `codeNFC`=?")
        preparedStatement.setString(1,carte.codeNFC)
        rs = preparedStatement.executeQuery()
        while (rs.next()){
            return 3
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO `carte`(`id_Carte`, `pin`, `argent`, `codeNFC`) VALUES (NULL,?,0,?)")
        preparedStatement.setInt(1,carte.pin)
        preparedStatement.setString(2,carte.codeNFC)
        return preparedStatement.executeUpdate()
    }

    fun modifierCarte(carte: Carte) : Int {
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT null FROM `carte` WHERE codeNFC = ? AND id_Carte!=?")
        preparedStatement.setString(1,carte.codeNFC)
        preparedStatement.setInt(2,carte.id!!)
        var rs = preparedStatement.executeQuery()
        while (rs.next()) {
            return 1
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT * FROM `carte` WHERE pin=? AND id_Carte!=?")
        preparedStatement.setInt(1,carte.pin!!)
        preparedStatement.setInt(2,carte.id!!)
        rs = preparedStatement.executeQuery()
        while (rs.next()) {
            return 2
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE `carte` SET `argent`=?, codeNFC=?, pin=? WHERE id_Carte=?")
        preparedStatement.setDouble(1,carte.argent!!)
        preparedStatement.setString(2,carte.codeNFC)
        preparedStatement.setInt(3,carte.pin)
        preparedStatement.setInt(4,carte.id!!)
        preparedStatement.executeUpdate()
        return 0
    }

    fun setSolde(solde:Double, codeNFC:String) {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE `carte` SET `argent`=? WHERE codeNFC=?")
        preparedStatement.setDouble(1,solde)
        preparedStatement.setString(2,codeNFC)
        preparedStatement.executeUpdate()
    }

    fun debiterCarte(carte: Carte) : Int {
        if(carte.argent!! < 0) {
            return 2
        }
        var solde = getSolde(carte.codeNFC.toString())
        solde -= carte.argent!!
        if(solde < 0) {
            return 1
        }
        setSolde(solde,carte.codeNFC.toString())
        return 0
    }

    fun debiterCarte(carte: Carte, amount: Double) : Int {
        if(amount < 0) {
            return 2
        }
        carte.argent = carte.argent!!.minus(amount)
        if(carte.argent!! < 0) {
            return 1
        }
        setSolde(carte.argent!!,carte.codeNFC.toString())
        return 0
    }

    fun crediterCarte(carte: Carte): Boolean {
        if(carte.argent!! < 0) {
            return false
        }
        var solde = getSolde(carte.codeNFC.toString())
        solde += carte.argent!!
        setSolde(solde,carte.codeNFC.toString())
        return true
    }

    fun getSolde(codeNFC: String): Double {
        var solde = 0.0
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT argent FROM carte WHERE codeNFC = ?")
        preparedStatement.setString(1,codeNFC)
        val rs = preparedStatement.executeQuery()
        while (rs.next()) {
            solde = rs.getDouble("argent")
        }
        return solde
    }

    fun supprimerCarte(id: Int) :Int{
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM carte WHERE id_Carte=?")
        preparedStatement.setInt(1,id)
        val rs = preparedStatement.executeQuery()
        while (!rs.next()) {
            return 0
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "DELETE FROM `carte` WHERE id_Carte = ?")
        preparedStatement.setInt(1,id)
        return try {
            preparedStatement.executeUpdate()
        } catch (e: SQLIntegrityConstraintViolationException) {
            -1
        }
    }

    fun ajouterArticleStand(stock: Stock) : Int {
        var quantite = getQuantiteArticleStand(stock.stand.idStand!!,stock.article.idArticle)
        if(quantite == -1) {
            return 0
        }
        quantite += stock.quantite!!
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE `stand_has_article` SET `quantite`=? WHERE Stand_id_Stand = ? AND article_id_Article = ?")
        preparedStatement.setInt(1,quantite)
        preparedStatement.setInt(2,stock.stand.idStand)
        preparedStatement.setInt(3,stock.article.idArticle)
        return preparedStatement.executeUpdate()
    }

    fun retirerArticleStand(stock: Stock): Int {
        var quantite = getQuantiteArticleStand(stock.stand.idStand!!,stock.article.idArticle)
        if(quantite == -1) {
            return 0
        }
        quantite -= stock.quantite!!
        if(quantite < 0) {
            return -1
        }
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE `stand_has_article` SET quantite = ? WHERE `Stand_id_Stand` = ? AND `article_id_Article` = ?")
        preparedStatement.setInt(1,quantite)
        preparedStatement.setInt(2,stock.stand.idStand)
        preparedStatement.setInt(3,stock.article.idArticle)
        return preparedStatement.executeUpdate()
    }

    fun getQuantiteArticleStand(idStand:Int, idArticle: Int) : Int{
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT quantite FROM stand_has_article WHERE Stand_id_Stand = ? AND article_id_Article = ?")
        preparedStatement.setInt(1,idStand)
        preparedStatement.setInt(2,idArticle)
        val rs = preparedStatement.executeQuery()
        while (rs.next()) {
            return rs.getInt("quantite")
        }
        return -1
    }

    fun suprimerStock(stock: Stock): Int {
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM stand_has_article WHERE Stand_id_Stand=? AND article_id_Article = ?")
        preparedStatement.setInt(1,stock.stand.idStand!!)
        preparedStatement.setInt(2,stock.article.idArticle)
        val rs = preparedStatement.executeQuery()
        while (!rs.next()) {
            return 0
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "DELETE FROM `stand_has_article` WHERE `Stand_id_Stand` = ? AND `article_id_Article` = ?")
        preparedStatement.setInt(1,stock.stand.idStand)
        preparedStatement.setInt(2,stock.article.idArticle)
        return preparedStatement.executeUpdate()
    }

    fun modifierStock(stock: Stock): Int {
        if(stock.quantite!! < 0) {
            return -1
        }
        if (stock.prix!! < 0) {
            return -2
        }
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE stand_has_article SET quantite=?,prix=? WHERE Stand_id_Stand = ? AND article_id_Article = ?")
        preparedStatement.setInt(1, stock.quantite)
        preparedStatement.setDouble(2,stock.prix)
        preparedStatement.setInt(3,stock.stand.idStand!!)
        preparedStatement.setInt(4,stock.article.idArticle)
        return preparedStatement.executeUpdate()
    }

    fun ajouterStock(stock: Stock): Int {
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM stand_has_article WHERE Stand_id_Stand = ? AND article_id_Article = ?")
        preparedStatement.setInt(1,stock.stand.idStand!!)
        preparedStatement.setInt(2,stock.article.idArticle)
        val rs = preparedStatement.executeQuery()
        while (rs.next()) {
            return 0
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO `stand_has_article`(`Stand_id_Stand`, `article_id_Article`, `quantite`, `prix`) VALUES (?,?,?,?)")
        preparedStatement.setInt(1,stock.stand.idStand)
        preparedStatement.setInt(2,stock.article.idArticle)
        preparedStatement.setInt(3,stock.quantite!!)
        preparedStatement.setDouble(4,stock.prix!!)
        return preparedStatement.executeUpdate()
    }

    fun suprimerStand(stand: Stand): Int {
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM stand WHERE id_Stand = ?")
        preparedStatement.setInt(1,stand.idStand!!)
        val rs = preparedStatement.executeQuery()
        while (!rs.next()) {
            return 0
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "DELETE FROM `stand` WHERE id_Stand = ?")
        preparedStatement.setInt(1,stand.idStand)
        return try {
            preparedStatement.executeUpdate()
        } catch (e: SQLIntegrityConstraintViolationException) {
            -1
        }
    }

    fun historiqueStand(idStand: Int): ArrayList<Historique> {
        val historics : ArrayList<Historique> = ArrayList<Historique>()
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT * FROM `historique_des_transactions` " +
                    "JOIN carte ON historique_des_transactions.Carte_id_Carte = carte.id_Carte " +
                    "JOIN stand ON historique_des_transactions.Stand_id_Stand = stand.id_Stand " +
                    "WHERE Stand_id_Stand = ?")
        preparedStatement.setInt(1,idStand)
        return executeQueryHistorique(preparedStatement)
    }

    fun historiqueCarte(idCarte: Int): ArrayList<Historique> {
        val historics : ArrayList<Historique> = ArrayList<Historique>()
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT * FROM `historique_des_transactions` " +
                    "JOIN carte ON historique_des_transactions.Carte_id_Carte = carte.id_Carte " +
                    "JOIN stand ON historique_des_transactions.Stand_id_Stand = stand.id_Stand " +
                    "WHERE Carte_id_Carte = ?")
        preparedStatement.setInt(1,idCarte)
        return executeQueryHistorique(preparedStatement)
    }

    fun historiqueCarte(codeNFC: String): ArrayList<Historique> {
        val historics : ArrayList<Historique> = ArrayList<Historique>()
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT * FROM `historique_des_transactions` " +
                    "JOIN carte ON historique_des_transactions.Carte_id_Carte = carte.id_Carte " +
                    "JOIN stand ON historique_des_transactions.Stand_id_Stand = stand.id_Stand " +
                    "WHERE carte.codeNFC = ?"
        )
        preparedStatement.setString(1,codeNFC)
        return executeQueryHistorique(preparedStatement)
    }

    fun executeQueryHistorique(preparedStatement: PreparedStatement) : ArrayList<Historique> {
        val historics = ArrayList<Historique>()
        val rs = preparedStatement.executeQuery()
        while (rs.next()) {
            historics.add(
                Historique(
                    rs.getInt("id_historique"),
                    rs.getString("horodatage"),
                    Carte(
                        rs.getInt("Carte_id_Carte"),
                        rs.getInt("pin"),
                        rs.getDouble("carte.argent"),
                        rs.getString("codeNFC")
                    ),
                    Stand(
                        rs.getInt("Stand_id_Stand"),
                        rs.getString("nom_stand")
                    ),
                    rs.getDouble("historique_des_transactions.argent")
                )
            )
        }
        return historics
    }

    fun modifierStand(stand: Stand): Int {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE `stand` SET `nom_stand`= ? WHERE id_Stand = ?")
        preparedStatement.setString(1,stand.standName)
        preparedStatement.setInt(2,stand.idStand!!)
        return preparedStatement.executeUpdate()
    }

    fun getCardID(nfcCode: String) : Int{
        var idCarte = -1
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id_Carte FROM carte WHERE codeNFC=?"
        )
        preparedStatement.setString(1,nfcCode)
        val rs = preparedStatement.executeQuery()
        while (rs.next()) {
            idCarte = rs.getInt("id_Carte")
            println(rs.getInt("id_Carte"))
        }
        return idCarte
    }

    fun achat(carte: Carte, paramStock: Stock , amount: Int): Int {
        var prix = 0.0
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
                    "SELECT prix FROM `stand_has_article` " +
                    "INNER JOIN stand ON stand_has_article.Stand_id_Stand = stand.id_Stand " +
                    "INNER JOIN article ON stand_has_article.article_id_Article = article.id_Article " +
                    "WHERE stand_has_article.Stand_id_Stand = ? AND stand_has_article.article_id_Article = ?"
        )
        preparedStatement.setInt(1,paramStock.stand.idStand!!)
        preparedStatement.setInt(2,paramStock.article.idArticle)
        val rs = preparedStatement.executeQuery()
        while (rs.next()) {
           prix = rs.getDouble("prix")
        }
        carte.argent = getSolde(carte.codeNFC!!)
        carte.id = getCardID(carte.codeNFC)
        if(carte.id == -1) {
            return 5
        }
        when(debiterCarte(carte,prix * amount)) {
            1-> {
                return 1
            }
            2-> {
                return 2
            }
        }
        when(retirerArticleStand(Stock(paramStock.stand,paramStock.article, amount,null))){
            -1 -> {
                return -1
            }
            0 -> {
                return 3
            }
        }

        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO `historique_des_transactions`(`id_historique`, `horodatage`, `Carte_id_Carte`, `Stand_id_Stand`, `argent`) " +
                    "VALUES (NULL,NOW(),?,?,?)"
        )
        preparedStatement.setInt(1,carte.id!!)
        preparedStatement.setInt(2,paramStock.stand.idStand)
        preparedStatement.setDouble(3,prix)
        when(preparedStatement.executeUpdate()) {
            1 -> { return 0}
            else -> { return 4}
        }
    }
    fun ajouterStand(stand: Stand) {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO `stand`(`id_Stand`, `nom_stand`) VALUES (NULL,?)"
        )
        preparedStatement.setString(1,stand.standName)
        preparedStatement.executeUpdate()
    }

    fun connexionDuBenevole(user: String, password: String): BenevolentConnectResponse {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
                "SELECT id_Utilisateur,civilite_id_civilite,role,Stand_id_Stand,nom,prenom,age,nom_stand FROM benevole " +
                        "JOIN civilite ON civilite.id_civilite = benevole.civilite_id_civilite " +
                        "JOIN stand ON stand.id_Stand = benevole.Stand_id_Stand " +
                        "WHERE user = ? AND password = ?"
        )
        preparedStatement.setString(1,user)
        preparedStatement.setString(2,password)
        val rs = preparedStatement.executeQuery()
        if(rs.next()) {
            return BenevolentConnectResponse(true,
                createBenevolent(rs),
                ""
            )
        }
        return BenevolentConnectResponse(false, null,"")
    }

    fun getStock(standID: Int) : GetStocksResponse {
        val stocks = ArrayList<Stock>()
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id_Stand,nom_stand,id_Article,produit,quantite,prix FROM stand_has_article " +
                    "JOIN stand ON stand.id_Stand = stand_has_article.Stand_id_Stand " +
                    "JOIN article ON article.id_Article = stand_has_article.article_id_Article " +
                    "WHERE Stand_id_Stand = ?"
        )
        preparedStatement.setInt(1,standID)
        val rs = preparedStatement.executeQuery()
        while (rs.next()) {
            stocks.add(
                Stock(
                    Stand(
                        rs.getInt("id_Stand"),
                        rs.getString("nom_stand")
                    ),
                    Article(
                        rs.getInt("id_Article"),
                        rs.getString("produit")
                    ),
                    rs.getInt("quantite"),
                    rs.getDouble("prix")
                )
            )
        }
        return GetStocksResponse(stocks,"")
    }

    fun getAllArticle(): ArrayList<Article> {
        val articles = ArrayList<Article>()
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT * FROM article"
        )
        val rs = preparedStatement.executeQuery()
        while (rs.next()) {
            articles.add(Article(rs.getInt("id_Article"),rs.getString("produit")))
        }
        return articles
    }

    fun getUserIdByName(user: String, password: String): Utilisateur? {
        val prepStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT * FROM `utilisateur` WHERE user = ? and password = ?")
        prepStatement.setString(1,user)
        prepStatement.setString(2,password)
        val rs = prepStatement.executeQuery()
        while (rs.next()) {
            return Utilisateur(rs.getInt("id_Utilisateur"),null,rs.getString("user"),rs.getString("password"))
        }
        return null
    }

    fun getCardByID(idCarte: Int): Carte? {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT * FROM carte WHERE id_Carte = ?"
        )
        preparedStatement.setInt(1,idCarte)
        val rs = preparedStatement.executeQuery()
        if (rs.next()) {
            return Carte(
                rs.getInt("id_Carte"),
                rs.getInt("pin"),
                rs.getDouble("argent"),
                rs.getString("codeNFC")
            )
        }
        return null;
    }

    fun getUserIdByCardID(cardID: Int): Utilisateur? {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id_Carte,id_Utilisateur,user,pin,argent,codeNFC FROM utilisateur " +
                    "JOIN carte ON Carte_id_Carte = carte.id_Carte " +
                    "WHERE `Carte_id_Carte` = ?"
        )
        preparedStatement.setInt(1,cardID)
        return getUserRequestResult(preparedStatement.executeQuery())
    }

    fun getUserByID(id: Int): Utilisateur? {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id_Carte,id_Utilisateur,user,pin,argent,codeNFC FROM utilisateur " +
                    "JOIN carte ON Carte_id_Carte = carte.id_Carte " +
                    "WHERE `id_Utilisateur` = ?"
        )
        preparedStatement.setInt(1,id)
        return getUserRequestResult(preparedStatement.executeQuery())
    }

    fun getUserRequestResult(rs : ResultSet) : Utilisateur? {
        if(rs.next()) {
            return Utilisateur(
                rs.getInt("id_Utilisateur"),
                Carte(
                    rs.getInt("id_Carte"),
                    rs.getInt("pin"),
                    rs.getDouble("argent"),
                    rs.getString("codeNFC")
                ),
                rs.getString("user"),
                null
            )
        }
        return null
    }

    fun getBenevolentByID(id: Int) : Benevole? {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id_Utilisateur,civilite_id_civilite,role,Stand_id_Stand,nom,prenom,age,nom_stand FROM benevole " +
                    "JOIN civilite ON civilite.id_civilite = benevole.civilite_id_civilite " +
                    "JOIN stand ON stand.id_Stand = benevole.Stand_id_Stand " +
                    "WHERE id_Utilisateur = ?"
        )
        preparedStatement.setInt(1,id)
        val rs = preparedStatement.executeQuery()
        return if(rs.next()) {
            createBenevolent(rs)
        } else {
            null
        }
    }

    fun createBenevolent(rs: ResultSet) : Benevole {
        return Benevole(
            rs.getInt("id_Utilisateur"),
            null,
            null,
            Civilite(
                rs.getInt("civilite_id_civilite"),
                rs.getString("prenom"),
                rs.getString("nom"),
                rs.getInt("age")
            ),
            rs.getInt("role"),
            Stand(
                rs.getInt("Stand_id_Stand"),
                rs.getString("nom_stand")
            )
        )
    }
}