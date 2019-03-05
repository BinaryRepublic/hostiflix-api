package com.hostiflix.filter

import com.hostiflix.services.AuthenticationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletResponse

@Component
class AuthFilter (
    private val authenticationService: AuthenticationService
) : Filter {

    @Value("\${noAccessTokenRequiredEndpoints}")
    lateinit var noAccessTokenRequiredEndpoints : List<String>

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val accessToken : String? = (request as HttpServletRequest).getHeader("AccessToken")
        val path = request.servletPath

        var isAuthenticated = accessToken?.takeIf { authenticationService.isAuthenticated(it) } != null
        val skipAuthentication = !noAccessTokenRequiredEndpoints.firstOrNull{ path.startsWith(it) }.isNullOrEmpty()

        if (skipAuthentication || isAuthenticated ) {
            chain.doFilter(request, response)
        } else {
            (response as HttpServletResponse).status = 403
            response.writer.write("Invalid access token")
        }
    }

    override fun destroy() {}

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {}
}