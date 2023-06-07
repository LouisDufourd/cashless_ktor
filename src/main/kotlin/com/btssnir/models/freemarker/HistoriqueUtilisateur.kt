package com.btssnir.models.freemarker

import com.btssnir.models.Historique
import com.btssnir.models.Utilisateur

data class HistoriqueUtilisateur(val historiques: ArrayList<Historique>, val utilisateur: Utilisateur)
