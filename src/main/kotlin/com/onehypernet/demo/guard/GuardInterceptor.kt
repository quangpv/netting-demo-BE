package com.onehypernet.demo.guard

import com.onehypernet.demo.component.security.JwtTokenProvider
import com.onehypernet.demo.exception.PrivilegeException
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class GuardInterceptor(
    private val tokenProvider: JwtTokenProvider,
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler !is HandlerMethod) return true
        val requireRoles = handler.getMethodAnnotation(Guard::class.java)?.roles
            ?: handler.bean.javaClass.getDeclaredAnnotation(Guard::class.java)?.roles
            ?: return true
        val accessToken = tokenProvider.resolveToken(request)
        if (accessToken == null && requireRoles.isNotEmpty()) {
            throw PrivilegeException("You do not have permission to access")
        }
        accessToken ?: return true

        val tokenRole = tokenProvider.getRole(accessToken)
        if (tokenRole !in requireRoles.toList()) {
            throw PrivilegeException("Your roles not in [${requireRoles.joinToString()}]")
        }
        return true
    }
}