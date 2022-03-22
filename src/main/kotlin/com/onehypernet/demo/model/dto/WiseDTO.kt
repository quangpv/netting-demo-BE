package com.onehypernet.demo.model.dto

import com.google.gson.annotations.SerializedName


data class WiseDTO(
    val sourceCurrency: String? = null,
    val targetCurrency: String? = null,
    val sourceCountry: Any? = null,
    val targetCountry: Any? = null,
    val providerCountry: Any? = null,
    val providerType: Any? = null,
    val sendAmount: Long? = null,
    val providers: List<ProviderDTO>? = null
)

data class ProviderDTO(
    val id: Long? = null,
    val alias: String? = null,
    val name: String? = null,
    val logo: String? = null,
    val logos: LogosDTO? = null,
    val type: String? = null,
    val partner: Boolean? = null,
    val quotes: List<QuoteDTO>? = null
)

data class LogosDTO(
    val normal: InverseDTO? = null,
    val inverse: InverseDTO? = null,
    val white: InverseDTO? = null
)

data class InverseDTO(
    @SerializedName("svgUrl")
    val svgURL: String? = null,

    @SerializedName("pngUrl")
    val pngURL: String? = null
)

data class QuoteDTO(
    val rate: Double? = null,
    val fee: Double? = null,
    val receivedAmount: Double? = null,
    val dateCollected: String? = null,
    val sourceCountry: String? = null,
    val targetCountry: String? = null,
    val markup: Double? = null,
    val deliveryEstimation: DeliveryEstimationDTO? = null
)

data class DeliveryEstimationDTO(
    val duration: DurationDTO? = null,
    val providerGivesEstimate: Boolean? = null
)

data class DurationDTO(
    val min: String? = null,
    val max: String? = null
)
