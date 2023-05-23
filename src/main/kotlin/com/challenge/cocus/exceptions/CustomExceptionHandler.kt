package com.challenge.cocus.exceptions

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class CustomExceptionHandler {
    private val logger = LoggerFactory.getLogger(CustomExceptionHandler::class.java)

    @ExceptionHandler(GitHubClientException::class)
    fun repositoriesException(exception: GitHubClientException): ResponseEntity<ErrorInfo> {
        val errorInfo = ErrorInfo(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.message)
        logger.error(exception.message, exception)
        return ResponseEntity.internalServerError().body(errorInfo)
    }


    @ExceptionHandler(GitHubClientRepositoriesNotFoundException::class)
    fun repositoriesNotFoundException(
        exception: GitHubClientRepositoriesNotFoundException): ResponseEntity<ErrorInfo> {
        val errorInfo = ErrorInfo(HttpStatus.NOT_FOUND.value(), exception.message)
        logger.error(exception.message, exception)
        return ResponseEntity.badRequest().body(errorInfo)
    }

    @ExceptionHandler(NotSupportedMediaTypeException::class)
    fun notSupportedMediaTypeException(
        exception: NotSupportedMediaTypeException): ResponseEntity<ErrorInfo> {
        val errorInfo = ErrorInfo(HttpStatus.NOT_ACCEPTABLE.value(), exception.message)
        logger.error(exception.message, exception)
        return ResponseEntity.badRequest().body(errorInfo)
    }
}
