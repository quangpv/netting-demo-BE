package com.onehypernet.demo.controller

import com.onehypernet.demo.command.CompareRateCmd
import com.onehypernet.demo.command.UploadMarginCmd
import com.onehypernet.demo.guard.Guard
import com.onehypernet.demo.model.enumerate.UserRole
import com.onehypernet.demo.model.request.RateCompareRequest
import com.onehypernet.demo.model.response.RateCompareResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("rate-comparison")
class RateComparisonController(
    private val compareRateCmd: CompareRateCmd,
    private val uploadMarginCmd: UploadMarginCmd
) {

    @PostMapping("")
    fun compare(@RequestBody request: RateCompareRequest): RateCompareResponse {
        return compareRateCmd(request)
    }

    @Guard(UserRole.Admin)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("margins")
    fun margins(
        @RequestParam("file") paramsFile: MultipartFile
    ) {
        uploadMarginCmd(paramsFile.inputStream)
    }
}