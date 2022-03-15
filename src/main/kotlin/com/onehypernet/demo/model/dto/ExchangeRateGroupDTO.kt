package com.onehypernet.demo.model.dto

import com.google.gson.annotations.SerializedName

data class ExchangeRateGroupDTO(
    val queryCount: Long? = null,
    val resultsCount: Long? = null,
    val adjusted: Boolean? = null,
    val results: List<Result>? = null,
    val status: String? = null,

    @SerializedName("request_id")
    val requestID: String? = null,

    val count: Long? = null
)

data class Result(
    @SerializedName("T")
    val exchangeSymbols: String? = null,

    val v: Long? = null,
    val vw: Double? = null,
    @SerializedName("o")
    val openRate: Double? = null,
    val c: Double? = null,
    val h: Double? = null,
    val l: Double? = null,

    @SerializedName("t")
    val resultT: Long? = null,

    val n: Long? = null
)
