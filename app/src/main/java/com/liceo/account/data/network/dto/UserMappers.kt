package com.liceo.account.data.network.dto

import com.liceo.account.domain.model.User

fun UserDto.toDomain(): User = User(
    // TODO 3a: Elvis operator for id
    id = id ?: "",
    // TODO 3b: Trim trimmed fullname or default
    fullName = fullname?.trim() ?: "(no name)",
    // TODO 3c: Trim trimmed email or default
    email = email?.trim() ?: "",
    // TODO 3d: Default for missing birthdate
    birthdate = birthdate ?: "(not set)"
)