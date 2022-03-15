package com.onehypernet.demo.command

import com.onehypernet.demo.AppConst
import com.onehypernet.demo.model.response.Amount
import com.onehypernet.demo.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class GetMyBalanceCmd(private val userRepository: UserRepository) {
    operator fun invoke(userId: String): Amount {
        val userDetail = userRepository.findById(userId).get().detail
        val currency = userDetail?.currency ?: AppConst.BRIDGING_CURRENCY
        return Amount(currency, 0.0.toBigDecimal())
    }
}