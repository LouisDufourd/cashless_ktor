package com.btssnir.models

data class Benevole(val id : Int, val user : String?, val password : String?, val civilite : Civilite, val role : Int, val stand: Stand) {
}