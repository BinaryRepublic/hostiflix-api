package com.hostiflix.filter

import com.hostiflix.service.AuthenticationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthFilter (
    private val authenticationService: AuthenticationService
) : Filter {

    @Value("\${noAccessTokenRequiredEndpoints}")
    lateinit var noAccessTokenRequiredEndpoints : List<String>

    /**
     * This method handles all incoming requests and checks whether it has a valid access token in the header or
     * if the request was sent to an endpoint which doesn't require authentication.
     * If one is true, the request get passed along the chain. Otherwise it will return an 403 error.
     *
     * @author  Tobias Kemkes
     * @version 1.0
     * @since   2019-03-11
     */

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if ((request as HttpServletRequest).method == "OPTIONS") {
            chain.doFilter(request, response)
            return
        }

        val accessToken : String? = request.getHeader("Access-Token")
        val path = request.servletPath
        // SE_03 functional concepts & lambda & nullable
        val isAuthenticated = accessToken?.takeIf { authenticationService.isAuthenticated(it) } != null
        val skipAuthentication = !noAccessTokenRequiredEndpoints.firstOrNull{ path.startsWith(it) }.isNullOrEmpty()

        if (skipAuthentication || isAuthenticated) {
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