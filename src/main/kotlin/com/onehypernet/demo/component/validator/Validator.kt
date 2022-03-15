package com.onehypernet.demo.component.validator

import com.onehypernet.demo.component.ApplicationProperties
import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.model.entity.NettedTransactionEntity
import com.onehypernet.demo.model.entity.NettingCycleEntity
import com.onehypernet.demo.model.enumerate.NettingStatus
import org.springframework.stereotype.Component
import java.util.*
import java.util.regex.Pattern

@Component
class Validator(private val applicationProperties: ApplicationProperties) {
    private val COUNTRIES = Locale.getISOCountries()

    fun checkEmail(email: String) {
        checkBlank(email, "Email")
        !Pattern.compile("^(.+)@(\\S+)$").matcher(email).matches() throws "Email invalid"
    }

    fun checkPassword(password: String) {
        checkBlank(password, "Password")
    }

    private fun checkBlank(value: String, prefixMessage: String) {
        value.isBlank() throws "$prefixMessage should not be blank"
    }

    fun checkLocation(locationCode: String) {
        if (!COUNTRIES.contains(locationCode.trim().toUpperCase()))
            throws("Location code $locationCode is incorrect")
    }

    fun checkCurrency(currency: String) {
        try {
            Currency.getInstance(currency)
        } catch (e: Throwable) {
            throws("Currency code $currency is incorrect")
        }
    }

    fun requirePositive(value: Double, extraInfo: () -> String) {
        if (value < 0)
            throws("$value should not be negative at row ${extraInfo()}")
    }

    fun requireNotAdminEmail(email: String) {
        if (email == applicationProperties.adminEmail)
            throws("This email is admin, Please using admin login api")
    }

    fun requireAdminEmail(email: String) {
        if (email != applicationProperties.adminEmail)
            throws("This api just support admin login, please use admin email")
    }

    fun checkNettingGroup(group: String) {
        if (group.isBlank())
            throws("The group name should not be blank")
        if (group.length <= 2)
            throws("The group name length at least 2 character")
    }

    fun checkNettingId(id: String) {
        if (id.isBlank() || id.length <= 3)
            throws("The id $id is invalid")
    }

    fun checkId(it: String) {
        checkBlank(it, "Id")
    }

    fun checkCounterPartyName(it: String) {
        checkBlank(it, "Counter party")
    }

    fun requireOpening(nettingCycle: NettingCycleEntity) {
        if (nettingCycle.status > NettingStatus.Open) {
            throws("Netting ${nettingCycle.id} is in progressing")
        }
    }

    fun requireNotExists(nettedTransactions: List<NettedTransactionEntity>) {
        if (nettedTransactions.isNotEmpty()) {
            throws("Transactions ${nettedTransactions.joinToString { it.id }} exists")
        }
    }

    fun requireNotBlank(value: String, function: () -> String) {
        checkBlank(value, function())
    }
}