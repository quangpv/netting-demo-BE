package com.onehypernet.demo.controller

import com.onehypernet.demo.command.CompareRateCmd
import com.onehypernet.demo.model.request.RateCompareRequest
import com.onehypernet.demo.model.response.RateCompareResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("rate-comparison")
class RateComparisonController(
    private val compareRateCmd: CompareRateCmd
) {

    @PostMapping("")
    fun compare(@RequestBody request: RateCompareRequest): RateCompareResponse {
        return compareRateCmd(request)
    }
}