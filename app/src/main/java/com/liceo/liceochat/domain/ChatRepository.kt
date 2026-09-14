package com.liceo.liceochat.domain

import com.liceo.liceochat.core.AppResult

interface ChatRepository {
    suspend fun getMessages(): AppResult<List<Message>>
    suspend fun sendMessage(sender: String, text: String): AppResult<Unit>
}
