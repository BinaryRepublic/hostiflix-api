package com.hostiflix.support

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
class UnprocessableEntityException (exception: String): IllegalArgumentException(exception)