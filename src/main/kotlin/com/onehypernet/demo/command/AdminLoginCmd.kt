package com.onehypernet.demo.command

import com.onehypernet.demo.component.ApplicationProperties
import com.onehypernet.demo.component.PasswordHashes
import com.onehypernet.demo.component.security.JwtTokenProvider
import com.onehypernet.demo.component.validator.Validator
import com.onehypernet.demo.exception.EmailOrPasswordNotFoundException
import com.onehypernet.demo.model.entity.UserEntity
import com.onehypernet.demo.model.enumerate.UserRole
import com.onehypernet.demo.model.request.AccountRequest
import com.onehypernet.demo.model.response.LoginResponse
import com.onehypernet.demo.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AdminLoginCmd(
    private val userRepository: UserRepository,
    private val validator: Validator,
    private val jwtTokenProvider: JwtTokenProvider,
    private val appProperties: ApplicationProperties,
    private val passwordHashes: PasswordHashes
) {
    operator fun invoke(request: AccountRequest): LoginResponse {
        with(validator) {
            checkEmail(request.email)
            checkPassword(request.password)
            requireAdminEmail(request.email)
        }
        var user = userRepository.findByEmail(request.email)

        if (user == null) {
            user = createNewAdminUser(request)
            user = userRepository.save(user)
        } else {
            if (user.password != passwordHashes.digest(request.password)) {
                user = null
            }
        }

        if (user == null) throw EmailOrPasswordNotFoundException()

        val accessToken = jwtTokenProvider.createToken(user.id, user.role)
        return LoginResponse(accessToken)
    }

    private fun createNewAdminUser(request: AccountRequest): UserEntity {
        return UserEntity(
            email = request.email,
            password = passwordHashes.digest(appProperties.adminDefaultPassword),
            role = UserRole.Admin
        )
    }

}