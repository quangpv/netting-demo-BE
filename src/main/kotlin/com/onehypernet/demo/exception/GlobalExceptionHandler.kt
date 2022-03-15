package com.onehypernet.demo.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException


@ControllerAdvice
open class GlobalExceptionHandler {
    companion object {
        private val sLogger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)!!
    }

    @ExceptionHandler
    fun handleAll(e: Exception): ResponseEntity<ErrorBody> {
        when (e) {
            is AccessDeniedException -> return createEntity(e.message, HttpStatus.FORBIDDEN)
            is ResponseStatusException -> return createEntity(e.reason ?: e.message, e.status)
        }

        val annotation = e.javaClass.getAnnotation(ResponseStatus::class.java)

        if (annotation != null) return createEntity(
            annotation.reason.ifBlank { e.message },
            annotation.value,
            (e as? ErrorPayloadOwner)?.payload
        )
        if (e !is CheckedException) sLogger.error(e.message, e)
        return createEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun createEntity(reason: String?, status: HttpStatus, payload: Any? = null): ResponseEntity<ErrorBody> {
        return ResponseEntity(ErrorBody(reason ?: "Unknown", payload), status)
    }

}