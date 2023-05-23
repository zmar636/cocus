package com.challenge.cocus.exceptions

data class GitHubClientException(override val message: String): RuntimeException(message)
