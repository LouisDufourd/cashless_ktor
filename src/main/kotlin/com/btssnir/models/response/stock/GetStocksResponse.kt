package com.btssnir.models.response.stock

import com.btssnir.models.Stock

data class GetStocksResponse(val stocks : ArrayList<Stock>?, val responseString: String) {
}