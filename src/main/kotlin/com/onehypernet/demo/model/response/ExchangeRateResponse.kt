package com.onehypernet.demo.model.response

data class ExchangeRateResponse(
    val id: String = "",
    val fromCurrency: String = "",
    val toCurrency: String = "",
    val margin: Double = 0.0,
    val fee: Double = 0.0,
    val minFee: Double = 0.0,
    val maxFee: Double = 0.0,
    val fixedFee: Double = 0.0,
    val exchangeRate: Double = 0.0,
    val atLocation: String = "",
    val destinationLocations: String = ""
)