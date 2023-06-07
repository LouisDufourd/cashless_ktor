package com.btssnir.models.security

import com.btssnir.models.Benevole
import com.btssnir.models.Utilisateur

data class GoodLogin(val benevole: Benevole?, val utilisateur: Utilisateur?, val isGoodLogin: Boolean)