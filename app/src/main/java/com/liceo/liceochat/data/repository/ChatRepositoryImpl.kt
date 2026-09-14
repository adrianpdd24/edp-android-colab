package com.liceo.liceochat.data.repository

import com.liceo.liceochat.core.AppResult
import com.liceo.liceochat.data.local.MessageDao
import com.liceo.liceochat.data.local.toDomain
import com.liceo.liceochat.data.local.toEntity
import com.liceo.liceochat.data.network.ChatApiService
import com.liceo.liceochat.data.network.dto.NewMessageDto
import com.liceo.liceochat.data.network.dto.toDomain
import com.liceo.liceochat.domain.ChatRepository
import com.liceo.liceochat.domain.Message
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ChatRepositoryImpl(
    private val api: ChatApiService,
    private val dao: MessageDao
) : ChatRepository {

    override suspend fun getMessages(): AppResult<List<Message>> {
        val result = safeCall { api.getMessages().toDomain() }
        if (result is AppResult.Success) {
            dao.insertAll(result.data.map { it.toEntity() })
            return result
        }

        val saved = dao.getAll().map { it.toDomain() }
        return if (saved.isNotEmpty()) {
            AppResult.Success(saved)
        } else {
            result
        }
    }

    override suspend fun sendMessage(sender: String, text: String): AppResult<Unit> = safeCall {
        val dto = NewMessageDto(
            sender = sender,
            text = text,
            createdAt = System.currentTimeMillis()
        )
        api.sendMessage(dto)
        Unit
    }

    private suspend inline fun <T> safeCall(crossinline block: suspend () -> T): AppResult<T> =
        try { AppResult.Success(block()) }
        catch (e: UnknownHostException) { AppResult.Failure.NoInternet }
        catch (e: SocketTimeoutException) { AppResult.Failure.Timeout }
        catch (e: IOException) { AppResult.Failure.NoInternet }
        catch (e: Exception) { AppResult.Failure.Unknown(e.message) }
}
