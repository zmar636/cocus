package com.challenge.cocus.dto

data class Commit(val sha: String)

data class BranchInfoDto(val name: String, val commit: Commit)
