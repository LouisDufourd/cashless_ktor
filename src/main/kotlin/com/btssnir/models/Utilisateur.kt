package com.btssnir.models

data class Utilisateur(val id:Int?, val carte: Carte?, val user:String?, val password:String?) {
    override fun toString(): String {
        return "id:$id;carteID:${carte},user:$user,password:$password"
    }
}
