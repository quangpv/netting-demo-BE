package com.onehypernet.demo.controller

import com.onehypernet.demo.command.AdminLoginCmd
import com.onehypernet.demo.command.PartyLoginCmd
import com.onehypernet.demo.command.PartyRegistryCmd
import com.onehypernet.demo.model.request.AccountRequest
import com.onehypernet.demo.model.request.PartyLoginRequest
import com.onehypernet.demo.model.request.PartyRegistryRequest
import com.onehypernet.demo.model.response.LoginResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/auth")
class AuthorController(
    private val adminLoginCmd: AdminLoginCmd,
    private val partyLoginCmd: PartyLoginCmd,
    private val partyRegistryCmd: PartyRegistryCmd,
) {

    @PostMapping("admin/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun adminLogin(@RequestBody request: AccountRequest): LoginResponse {
        return adminLoginCmd(request)
    }

    @PostMapping("party/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun userLogin(@RequestBody request: PartyLoginRequest): LoginResponse {
        return partyLoginCmd(request)
    }

    @PostMapping("party/registry")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun userRegistry(@RequestBody request: PartyRegistryRequest):LoginResponse {
        return partyRegistryCmd(request)
    }
}