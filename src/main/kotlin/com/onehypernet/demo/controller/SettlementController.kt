package com.onehypernet.demo.controller

import com.onehypernet.demo.command.SettleCmd
import com.onehypernet.demo.component.security.JwtTokenProvider
import com.onehypernet.demo.guard.Guard
import com.onehypernet.demo.model.enumerate.UserRole
import com.onehypernet.demo.model.request.SettleRequest
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("settlement")
class SettlementController(
    private val settleCmd: SettleCmd,
    private val tokenProvider: JwtTokenProvider
) {

    @Guard(UserRole.Party)
    @PostMapping("{nettingId}")
    fun settle(
        @ApiIgnore
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable("nettingId") nettingId: String,
        @RequestBody request: SettleRequest
    ) {
        settleCmd(tokenProvider.resolveId(authHeader), nettingId, request)
    }
}