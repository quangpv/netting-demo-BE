package com.onehypernet.demo.component.security

import org.springframework.stereotype.Component
import javax.servlet.*
import javax.servlet.http.HttpServletResponse

@Component
class SimpleCORSFilter : Filter {
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        //        val request = req as HttpServletRequest
        val response = res as HttpServletResponse
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, PATCH, OPTIONS, DELETE")
        response.setHeader(
            "Access-Control-Allow-Headers",
            "Origin, Accept, Authorization, Content-Type, X-Requested-With, Range, Referer, User-Agent"
        )
        chain.doFilter(req, res)
    }

    override fun init(filterConfig: FilterConfig) {}
    override fun destroy() {}
}