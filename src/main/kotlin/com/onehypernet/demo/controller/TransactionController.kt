package com.onehypernet.demo.controller

import com.onehypernet.demo.command.UploadTransactionCmd
import com.onehypernet.demo.component.security.JwtTokenProvider
import com.onehypernet.demo.guard.Guard
import com.onehypernet.demo.model.enumerate.UserRole
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val uploadTransactionCmd: UploadTransactionCmd,
    private val tokenProvider: JwtTokenProvider
) {

    @Guard(UserRole.Party)
    @PostMapping("{nettingId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun upload(
        @ApiIgnore
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable("nettingId") nettingId: String,
        @RequestParam("csv") csvFile: MultipartFile
    ) {
        uploadTransactionCmd(csvFile, tokenProvider.resolveId(authHeader), nettingId)
    }
}