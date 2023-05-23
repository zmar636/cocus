package com.challenge.cocus.dto

data class Owner(val login: String)

data class RepositoryInfoDto(
    val name: String,
    val owner: Owner,
    val fork: Boolean,
    val branches_url: String)
