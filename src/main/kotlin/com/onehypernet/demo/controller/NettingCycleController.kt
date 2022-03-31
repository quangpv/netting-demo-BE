package com.onehypernet.demo.controller

import com.onehypernet.demo.command.*
import com.onehypernet.demo.component.security.JwtTokenProvider
import com.onehypernet.demo.guard.Guard
import com.onehypernet.demo.model.enumerate.UserRole
import com.onehypernet.demo.model.request.NettingCycleRequest
import com.onehypernet.demo.model.request.PagingRequest
import com.onehypernet.demo.model.response.ListResponse
import com.onehypernet.demo.model.response.NettingCycleDetailResponse
import com.onehypernet.demo.model.response.NettingCycleResponse
import com.onehypernet.demo.model.response.NettingOverviewResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("/netting-cycles")
class NettingCycleController(
    private val createNettingCycleCmd: CreateNettingCycleCmd,
    private val deleteNettingCycleCmd: DeleteNettingCycleCmd,
    private val getNettingCyclesCmd: GetNettingCyclesCmd,
    private val getNettingCycleByIdCmd: GetNettingCycleByIdCmd,
    private val getNettingCycleOverviewCmd: GetNettingCycleOverviewCmd,
    private val tokenProvider: JwtTokenProvider
) {

    @PostMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun create(@RequestBody request: NettingCycleRequest): NettingCycleResponse {
        return createNettingCycleCmd(request)
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun delete(@PathVariable id: String) {
        deleteNettingCycleCmd(id)
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun getList(
        @ApiIgnore
        @RequestHeader("Authorization") authHeader: String,
        request: PagingRequest
    ): ListResponse<NettingCycleResponse> {
        val userId = tokenProvider.resolveId(authHeader)
        return getNettingCyclesCmd(request, userId)
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getDetail(
        @ApiIgnore
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable id: String,
    ): NettingCycleDetailResponse {
        val userId = tokenProvider.resolveId(authHeader)
        return getNettingCycleByIdCmd(userId, id)
    }

    @GetMapping("overview")
    @ResponseStatus(HttpStatus.OK)
    fun getOverview(
        @ApiIgnore
        @RequestHeader("Authorization") authHeader: String,
    ): NettingOverviewResponse {
        val userId = tokenProvider.resolveId(authHeader)
        return getNettingCycleOverviewCmd(userId)
    }
}