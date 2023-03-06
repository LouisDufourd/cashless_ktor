package com.btssnir.models

data class Carte(val id:Int, val pin:Int, val argent:Double, val codeNFC:String) {
    override fun toString(): String {
        return "id:$id,pin:$pin,argent:$argent,codeNFC:$codeNFC"
    }
}
