package com.onehypernet.demo.controller

import com.onehypernet.demo.command.GetMyBalanceCmd
import com.onehypernet.demo.component.security.JwtTokenProvider
import com.onehypernet.demo.model.response.Amount
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("balances")
class BalanceController(
    private val getMyBalanceCmd: GetMyBalanceCmd,
    private val tokenProvider: JwtTokenProvider
) {
    @GetMapping("")
    fun getBalance(
        @ApiIgnore
        @RequestHeader("Authorization") authHeader: String,
    ): Amount {
        return getMyBalanceCmd(tokenProvider.resolveId(authHeader))
    }
}