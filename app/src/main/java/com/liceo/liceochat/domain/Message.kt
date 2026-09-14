package com.liceo.liceochat.domain

data class Message(
    val id: String,
    val sender: String,
    val text: String,
    val createdAt: Long
)
