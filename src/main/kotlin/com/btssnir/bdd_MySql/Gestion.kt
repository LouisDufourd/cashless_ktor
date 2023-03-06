package com.btssnir.bdd_MySql

import com.btssnir.models.Carte
import com.btssnir.models.Historique
import com.btssnir.models.Stand
import com.btssnir.models.Utilisateur
import java.sql.PreparedStatement
import java.sql.ResultSet


class Gestion() {
    var laConnexion = Connexion("jdbc:mysql://localhost/mydb", "root", "")

    fun lireLesUtilisateurs(): ArrayList<Utilisateur> {
        val arLesUtilisateur = ArrayList<Utilisateur>()
        val prepStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT * FROM `utilisateur` LEFT JOIN carte ON utilisateur.Carte_id_Carte = carte.id_Carte;")
        val rs = prepStatement.executeQuery()
        var utilisateur:Utilisateur
        while (rs.next()) {
            utilisateur = creeUtilisateur(rs);
            arLesUtilisateur.add(utilisateur)
        }
        return arLesUtilisateur
    }

    fun connexionDeUtilisateur(user:String, password:String): Boolean {
        val prepStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT 'user' 'password' FROM `utilisateur` WHERE user = ? and password = ?;")
        prepStatement.setString(1,user)
        prepStatement.setString(2,password)
        val rs = prepStatement.executeQuery()
        while (rs.next()) {
            return true
        }
        return false
    }

    fun inscriptionUtilisateur(user: String, password: String) : Boolean{
        var prepStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT 'user' 'password' FROM `utilisateur` WHERE user = ?;")
        prepStatement.setString(1,user)
        val rs = prepStatement.executeQuery()
        while (rs.next()) {
            return false
        }
        prepStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO `utilisateur` (`id_Utilisateur`, `Carte_id_Carte`, `user`, `password`) VALUES (NULL, NULL, ?, ?);")
        prepStatement.setString(1,user)
        prepStatement.setString(2,password)
        return true
    }

    fun desinscriptionUtilisateur(id: String) : Boolean {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "DELETE FROM `utilisateur` WHERE id_Utilisateur = ?")
        preparedStatement.setString(1,id)
        when(preparedStatement.executeUpdate()) {
            0->return false
            1->return true
            else->return false
        }
    }

    fun connecteCarteUtilisateur(pin:Int,idUtilisateur: Int) : Int{
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT id_Carte FROM carte WHERE pin = ?")
        preparedStatement.setInt(1,pin)
        var rs = preparedStatement.executeQuery()
        var idCarte = -1;
        while (rs.next()) {
            idCarte = rs.getInt("id_Carte")
        }
        if(idCarte == -1) {
            return 2
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM utilisateur WHERE Carte_id_Carte = ?")
        preparedStatement.setInt(1,idCarte)
        rs = preparedStatement.executeQuery()
        while (rs.next()) {
            return 3
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE `utilisateur` SET `Carte_id_Carte`=? WHERE id_Utilisateur = ?")
        preparedStatement.setInt(1,idCarte)
        preparedStatement.setInt(2,idUtilisateur)
        return preparedStatement.executeUpdate()
    }

    fun creeUtilisateur(rs:ResultSet) : Utilisateur {
        val utilisateur : Utilisateur;
        if(rs.getString("code NFC") == null) {
            utilisateur = Utilisateur(rs.getInt("id_Utilisateur"),null,rs.getString("user"),rs.getString("password"))
        } else {
            utilisateur = Utilisateur(rs.getInt("id_Utilisateur"), Carte(rs.getInt("id_Carte"),rs.getInt("pin"),rs.getDouble("argent"),rs.getString("code NFC")),rs.getString("user"),rs.getString("password"))
        }
        return utilisateur
    }

    fun modifierUtilisateur(id: Int, idCarte: Int?, user: String, password: String) : Boolean {
        var preparedStatement:PreparedStatement
        if(idCarte == null) {
            preparedStatement = laConnexion.getConnexion().prepareStatement(
                "UPDATE `utilisateur` SET `user`= ? ,`password`= ? WHERE id_Utilisateur = ?")
            preparedStatement.setString(1,user)
            preparedStatement.setString(2,password)
            preparedStatement.setInt(3,id)
            preparedStatement.executeUpdate()
            return true
        } else {
            preparedStatement = laConnexion.getConnexion().prepareStatement(
                "SELECT NULL FROM carte WHERE id_Carte = ?")
            preparedStatement.setInt(1,idCarte)
            val rs = preparedStatement.executeQuery()
            while (!rs.next()) {
                return false
            }
            preparedStatement = laConnexion.getConnexion().prepareStatement(
                "UPDATE `utilisateur` SET `Carte_id_Carte`= ? ,`user`= ? ,`password`= ? WHERE id_Utilisateur= ?")
            preparedStatement.setInt(1,idCarte)
            preparedStatement.setString(2,user)
            preparedStatement.setString(3,password)
            preparedStatement.setInt(4,id)
            preparedStatement.executeUpdate()
            return true
        }
    }

    fun creeCarte(pin: Int, codeNFC:String):Int {
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM `carte` WHERE carte.pin = ?")
        preparedStatement.setInt(1,pin)
        var rs = preparedStatement.executeQuery()
        while (rs.next()){
            return 1
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM `carte` WHERE `codeNFC`=?;")
        preparedStatement.setString(1,codeNFC)
        rs = preparedStatement.executeQuery()
        while (rs.next()){
            return 2
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO `carte`(`id_Carte`, `pin`, `argent`, `codeNFC`) VALUES (NULL,?,0,?)")
        preparedStatement.setInt(1,pin)
        preparedStatement.setString(2,codeNFC)
        preparedStatement.executeUpdate()
        return 0
    }

    fun modifierCarte(solde:Double, idCarte:Int, codeNFC: String, pin: Int) : Int {
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT null FROM `carte` WHERE codeNFC = ? AND id_Carte!=?;")
        preparedStatement.setString(1,codeNFC)
        preparedStatement.setInt(2,idCarte)
        var rs = preparedStatement.executeQuery()
        while (rs.next()) {
            return 1
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT * FROM `carte` WHERE pin=? AND id_Carte!=?;")
        preparedStatement.setInt(1,pin)
        preparedStatement.setInt(2,idCarte)
        rs = preparedStatement.executeQuery()
        while (rs.next()) {
            return 2
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE `carte` SET `argent`=?, codeNFC=?, pin=? WHERE id_Carte=?")
        preparedStatement.setDouble(1,solde)
        preparedStatement.setString(2,codeNFC)
        preparedStatement.setInt(3,pin)
        preparedStatement.setInt(4,idCarte)
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

    fun debiterCarte(codeNFC:String, amount:Double) : Boolean {
        var solde = getSolde(codeNFC)
        solde -= amount
        if(solde < 0) {
            return false
        }
        setSolde(solde,codeNFC)
        return true
    }

    fun crediterCarte(codeNFC: String, amount: Double): Boolean {
        var solde = getSolde(codeNFC)
        solde += amount
        setSolde(solde,codeNFC)
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

    fun supprimerCarte(id: Int) {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "DELETE FROM `carte` WHERE id_Carte = ?")
        preparedStatement.setInt(1,id)
        preparedStatement.executeUpdate()
    }

    fun ajouterArticleStand(idStand: Int, idArticle: Int, amount:Int) : Int {
        var quantite = getQuantiteArticleStand(idStand,idArticle)
        if(quantite == -1) {
            return 0
        }
        quantite += amount
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE `stand_has_article` SET `quantite`=? WHERE Stand_id_Stand = ? AND article_id_Article = ?")
        preparedStatement.setInt(1,quantite)
        preparedStatement.setInt(2,idStand)
        preparedStatement.setInt(3,idArticle)
        return preparedStatement.executeUpdate()
    }

    fun retirerArticleStand(idStand: Int, idArticle: Int, amount: Int): Int {
        var quantite = getQuantiteArticleStand(idStand,idArticle)
        if(quantite == -1) {
            return 0
        }
        quantite -= amount
        if(quantite < 0) {
            return -1
        }
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE `stand_has_article` SET quantite = ? WHERE `Stand_id_Stand` = ? AND `article_id_Article` = ?")
        preparedStatement.setInt(1,quantite)
        preparedStatement.setInt(2,idStand)
        preparedStatement.setInt(3,idArticle)
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

    fun suprimerStock(idStand: Int, idArticle: Int): Int {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "DELETE FROM `stand_has_article` WHERE `Stand_id_Stand` = ? AND `article_id_Article` = ?")
        preparedStatement.setInt(1,idStand)
        preparedStatement.setInt(2,idArticle)
        return preparedStatement.executeUpdate()
    }

    fun modifierStock(idStand: Int, idArticle: Int, amount: Int, price: Double): Int {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE `stand_has_article` SET `quantite`=?,`prix`=? WHERE Stand_id_Stand = ? AND article_id_Article = ?")
        preparedStatement.setInt(1,amount)
        preparedStatement.setDouble(2,price)
        preparedStatement.setInt(3,idStand)
        preparedStatement.setInt(4,idArticle)
        return preparedStatement.executeUpdate()
    }

    fun ajouterStock(idStand: Int, idArticle: Int, amount: Int, price: Double): Int {
        var preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT NULL FROM stand_has_article WHERE Stand_id_Stand = ? AND article_id_Article = ?")
        preparedStatement.setInt(1,idStand)
        preparedStatement.setInt(2,idArticle)
        var rs = preparedStatement.executeQuery()
        while (rs.next()) {
            return 0
        }
        preparedStatement = laConnexion.getConnexion().prepareStatement(
            "INSERT INTO `stand_has_article`(`Stand_id_Stand`, `article_id_Article`, `quantite`, `prix`) VALUES (?,?,?,?)")
        preparedStatement.setInt(1,idStand)
        preparedStatement.setInt(2,idArticle)
        preparedStatement.setInt(3,amount)
        preparedStatement.setDouble(4,price)
        return preparedStatement.executeUpdate()
    }

    fun suprimerStand(idStand: Int): Int {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "DELETE FROM `stand` WHERE id_Stand = ?")
        preparedStatement.setInt(1,idStand)
        return preparedStatement.executeUpdate()
    }

    fun historiqueStand(idStand: Int): ArrayList<Historique> {
        val historics : ArrayList<Historique> = ArrayList<Historique>()
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "SELECT * FROM `historique_des_transactions` JOIN carte ON historique_des_transactions.Carte_id_Carte = carte.id_Carte JOIN stand ON historique_des_transactions.Stand_id_Stand = stand.id_Stand WHERE Stand_id_Stand = ?;")
        preparedStatement.setInt(1,idStand)
        val rs = preparedStatement.executeQuery()
        while (rs.next()) {
            historics.add(Historique(rs.getInt("id_historique"),rs.getString("horodatage"),Carte(rs.getInt("id_Carte"),rs.getInt("pin"),rs.getDouble("carte.argent"),rs.getString("code NFC")),Stand(idStand,rs.getString("nom_stand")),rs.getDouble("historique_des_transactions.argent")))
        }
        return historics
    }

    fun modifierStand(idStand: Int, nomStand:String): Int {
        val preparedStatement = laConnexion.getConnexion().prepareStatement(
            "UPDATE `stand` SET `nom_stand`= ? WHERE id_Stand = ?")
        preparedStatement.setString(1,nomStand)
        preparedStatement.setInt(2,idStand)
        return preparedStatement.executeUpdate()
    }
}