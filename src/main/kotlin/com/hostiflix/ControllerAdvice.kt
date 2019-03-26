package com.hostiflix

import com.hostiflix.repository.AuthenticationRepository
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@ControllerAdvice
class ControllerAdvice (
    private val authenticationRepository: AuthenticationRepository
) : ResponseBodyAdvice<Any> {

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }

    /**
     * We use github's access tokens to manage authentication and therefore save all access tokens from a customer in our "AuthCredentials" table.
     * As the amount of access tokens one customer can have is limited, we make sure that always the latest access token will be passed to the frontend
     * for future requests.
     * This method checks whether the access token inside the request-header (if any) is the latest saved in our db.
     * If not, it will get the latest access token and add it to the response header.
     * The (expired) access token used for this request will be deleted.
     *
     * @author  Tobias Kemkes
     * @version 1.0
     * @since   2019-03-11
     */

    override fun beforeBodyWrite(body: Any?, returnType: MethodParameter, selectedContentType: MediaType, selectedConverterType: Class<out HttpMessageConverter<*>>, request: ServerHttpRequest, response: ServerHttpResponse): Any? {
        request.headers["Access-Token"]?.first()?.let { accessToken ->

            authenticationRepository.findByGithubAccessToken(accessToken)?.let {
                if (!it.latest) {
                    val latestAccessToken = authenticationRepository.findByCustomerIdAndLatest(it.customerId, true).githubAccessToken
                    authenticationRepository.deleteById(it.id!!)
                    response.headers.add("Latest-Access-Token", latestAccessToken)
                }
            }
        }
        return body
    }
}