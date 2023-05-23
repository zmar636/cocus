package com.challenge.cocus.exceptions

data class GitHubClientRepositoriesNotFoundException(override val message: String): RuntimeException(message)
