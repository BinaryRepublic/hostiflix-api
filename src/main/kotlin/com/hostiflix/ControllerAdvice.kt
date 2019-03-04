package com.hostiflix

import com.hostiflix.repository.AuthenticationRepository
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import org.springframework.web.bind.annotation.ControllerAdvice

@ControllerAdvice
class ControllerAdvice (
    private val authenticationRepository: AuthenticationRepository
) : ResponseBodyAdvice<Any> {

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }

    override fun beforeBodyWrite(body: Any?, returnType: MethodParameter, selectedContentType: MediaType, selectedConverterType: Class<out HttpMessageConverter<*>>, request: ServerHttpRequest, response: ServerHttpResponse): Any? {
        request.headers["AccessToken"]?.first()?.let { accessToken ->

            authenticationRepository.findById(accessToken).ifPresent {
                if (!it.latest) {
                    val latestAccessToken = authenticationRepository.findByCustomerIdAndLatest(it.customerId, true).githubAccessToken
                    authenticationRepository.deleteById(it.githubAccessToken)
                    response.headers.add("LatestAccessToken", latestAccessToken)
                }
            }
        }
        return body
    }
}
