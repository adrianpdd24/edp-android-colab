package com.liceo.account.domain.model

data class User(
    // TODO 2a: id as String
    val id: String,
    // TODO 2b: domain fields (no password included for security)
    val fullName: String,
    val email: String,
    val birthdate: String
)