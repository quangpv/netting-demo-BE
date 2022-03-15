package com.onehypernet.demo.command

import com.onehypernet.demo.component.security.JwtTokenProvider
import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.extension.throws
import com.onehypernet.demo.model.enumerate.UserRole
import com.onehypernet.demo.model.request.PartyLoginRequest
import com.onehypernet.demo.model.response.LoginResponse
import com.onehypernet.demo.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class PartyLoginCmd(
    private val userRepository: UserRepository,
    private val validator: Validator,
    private val tokenProvider: JwtTokenProvider,
) {
    operator fun invoke(request: PartyLoginRequest): LoginResponse {
        validator.checkEmail(request.email)
        validator.requireNotAdminEmail(request.email)

        val user = userRepository.findByEmail(request.email)
            ?: throws("Not found user by email ${request.email}")

        if (user.role == UserRole.Admin) throws("Please use admin api to login")

        val accessToken = tokenProvider.createToken(user.id, user.role)
        return LoginResponse(accessToken)
    }
}