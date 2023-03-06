package com.btssnir.bdd_MySql

import java.sql.*

class Connexion(url:String, username:String, password:String) {

    private lateinit var conn: Connection
    private val url=url
    private val username =username
    private val password = password


     init {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (ex: ClassNotFoundException) {
            println("erreur com.mysql.jdbc.Driver")
        }
        try {
            conn = DriverManager.getConnection(url,username,password)
            println("connexion OK")
        } catch (ex: SQLException) {
            println("erreur de connexion à la bdd")
        }
    }

    fun voirLesBdd():ArrayList<String>{
        var reponse = ArrayList<String>()
        lateinit var stmt: Statement
        lateinit var resultSet: ResultSet
        try {
            stmt = conn.createStatement()
            resultSet = stmt.executeQuery("SHOW DATABASES")
            while (resultSet.next()) {
                reponse.add(resultSet.getString("Database"))
            }
            return reponse
        } catch (ex: SQLException) {
            reponse.add("erreur SHOW DATABASES ")
            return reponse
        }
    }

    fun fermerConnexion(){
        conn.close()
    }

    fun getConnexion():Connection{
        return conn
    }


}