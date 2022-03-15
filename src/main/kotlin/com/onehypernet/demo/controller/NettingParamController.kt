package com.onehypernet.demo.controller

import com.onehypernet.demo.command.GetAllNettingParamsCmd
import com.onehypernet.demo.command.UploadNettingParamsCmd
import com.onehypernet.demo.guard.Guard
import com.onehypernet.demo.model.enumerate.UserRole
import com.onehypernet.demo.model.request.NettingParamRequest
import com.onehypernet.demo.model.response.ExchangeRateResponse
import com.onehypernet.demo.model.response.ListResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/netting-params")
class NettingParamController(
    private val uploadNettingParamsCmd: UploadNettingParamsCmd,
    private val getAllNettingParamsCmd: GetAllNettingParamsCmd
) {

    @Guard(UserRole.Admin)
    @PostMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun upload(
        @RequestParam("params") paramsFile: MultipartFile,
    ) {
        uploadNettingParamsCmd(paramsFile.inputStream)
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun getAll(request: NettingParamRequest): ListResponse<ExchangeRateResponse> {
        return getAllNettingParamsCmd(request)
    }
}