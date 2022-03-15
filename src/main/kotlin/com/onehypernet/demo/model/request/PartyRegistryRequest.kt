package com.onehypernet.demo.model.request

class PartyRegistryRequest(
    val email: String = "",
    val name: String = "",
    val countryCode: String = "",
    val currency: String = "",
    val bank: BankRequest = BankRequest()
)

class BankRequest(
    val accountName: String = "",
    val accountNumber: String = "",
    val address: String = "",
    val swift: String = ""
)