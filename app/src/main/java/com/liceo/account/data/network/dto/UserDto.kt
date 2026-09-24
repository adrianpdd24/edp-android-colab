package com.liceo.account.data.network.dto

import kotlinx.serialization.Serializable

// TODO 1a: @Serializable annotation
@Serializable
data class UserDto(
    // TODO 1b: id as String? = null
    val id: String? = null,
    // TODO 1c: nullable fields with null defaults
    val fullname: String? = null,
    val email: String? = null,
    val password: String? = null,
    val birthdate: String? = null
)

// TODO 1d: @Serializable annotation
@Serializable
data class NewUserDto(
    // TODO 1e: plain non-null Strings for creation
    val fullname: String,
    val email: String,
    val password: String,
    val birthdate: String
)