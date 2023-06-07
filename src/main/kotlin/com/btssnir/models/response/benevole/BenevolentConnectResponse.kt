package com.btssnir.models.response.benevole

import com.btssnir.models.Benevole

data class BenevolentConnectResponse(val isGoodLogin : Boolean, val benevole: Benevole?,val responseString: String)
