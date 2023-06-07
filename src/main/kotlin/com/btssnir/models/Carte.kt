package com.btssnir.models

data class Carte(var id:Int?, val pin:Int?, var argent:Double?, val codeNFC:String?) {
    override fun toString(): String {
        return "id:$id,pin:$pin,argent:$argent,codeNFC:$codeNFC"
    }
}
