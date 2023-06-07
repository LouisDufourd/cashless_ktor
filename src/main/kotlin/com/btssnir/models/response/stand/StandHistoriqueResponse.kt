package com.btssnir.models.response.stand

import com.btssnir.models.Historique

data class StandHistoriqueResponse(var responseString: String, val historiques : ArrayList<Historique>?)
