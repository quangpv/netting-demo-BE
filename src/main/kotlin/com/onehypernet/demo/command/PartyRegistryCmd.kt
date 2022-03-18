package com.onehypernet.demo.command

import com.onehypernet.demo.AppConst
import com.onehypernet.demo.component.security.JwtTokenProvider
import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.model.entity.UserDetailEntity
import com.onehypernet.demo.model.entity.UserEntity
import com.onehypernet.demo.model.enumerate.UserRole
import com.onehypernet.demo.model.request.PartyRegistryRequest
import com.onehypernet.demo.model.response.LoginResponse
import com.onehypernet.demo.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class PartyRegistryCmd(
    private val userRepository: UserRepository,
    private val validator: Validator,
    private val tokenProvider: JwtTokenProvider
) {
    operator fun invoke(request: PartyRegistryRequest): LoginResponse {
        validator.checkEmail(request.email)
        validator.requireNotAdminEmail(request.email)
//        validator.checkLocation(request.countryCode)
//        validator.checkCurrency(request.currency)
//        validator.requireNotBlank(request.bank.accountName) { "Bank account name" }
//        validator.requireNotBlank(request.bank.accountNumber) { "Bank account number" }
//        validator.requireNotBlank(request.bank.address) { "Bank address" }
//        validator.requireNotBlank(request.bank.swift) { "Bank swift" }

        if (userRepository.findByEmail(request.email) != null)
            throws("Email ${request.email} exists")

        val detail = UserDetailEntity(
            name = request.name,
            currency = AppConst.BRIDGING_CURRENCY,
        )
        val user = UserEntity(
            email = request.email,
            password = "",
            role = UserRole.Party,
            detail = detail
        )
        detail.user = user

        val result = userRepository.save(user)

        val token = tokenProvider.createToken(result.id, UserRole.Party)
        return LoginResponse(token, request.email, request.name)
    }
}