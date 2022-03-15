package com.onehypernet.demo.component.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtTokenFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val resolver: HandlerExceptionResolver
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = jwtTokenProvider.resolveToken(httpServletRequest)
            if (token != null) {
                jwtTokenProvider.verify(token)
                SecurityContextHolder.getContext().authentication = jwtTokenProvider.getAuthentication(token)
            }
        } catch (e: Exception) {
            SecurityContextHolder.clearContext()
            resolver.resolveException(httpServletRequest, httpServletResponse, null, e)
            return
        }
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse)
        } catch (e: Exception) {
            resolver.resolveException(httpServletRequest, httpServletResponse, null, e)
        }
    }
}