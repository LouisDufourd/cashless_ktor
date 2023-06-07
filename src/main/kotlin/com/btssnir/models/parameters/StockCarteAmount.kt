package com.btssnir.models.parameters

import com.btssnir.models.Carte
import com.btssnir.models.Stock

data class StockCarteAmount(val carte: Carte, val stock: Stock, val amount:Int)