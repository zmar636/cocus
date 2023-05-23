package com.challenge.cocus.exceptions

data class NotSupportedMediaTypeException(override val message: String): RuntimeException(message)
