package com.onehypernet.demo.datasource

import com.onehypernet.demo.model.dto.ExchangeRateGroupDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ForexApi {
    @GET("aggs/grouped/locale/global/market/fx/{date}")
    fun getGroup(@Path("date") date: String): Call<ExchangeRateGroupDTO>
}