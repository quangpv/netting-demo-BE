package com.onehypernet.demo.datasource

import com.onehypernet.demo.model.dto.WiseDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigDecimal

interface WiseApi {

    @GET("comparisons")
    fun getRate(
        @Query("sourceCurrency") sourceCurrency: String,
        @Query("targetCurrency") targetCurrency: String,
        @Query("sendAmount") sendAmount: BigDecimal,
    ): Call<WiseDTO>
}