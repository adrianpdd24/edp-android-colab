package com.liceo.liceochat.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.liceo.liceochat.core.AppResult
import com.liceo.liceochat.data.local.ChatDatabase
import com.liceo.liceochat.data.network.NetworkModule
import com.liceo.liceochat.data.repository.ChatRepositoryImpl
import com.liceo.liceochat.domain.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    var uiState: ChatUiState by mutableStateOf(ChatUiState.Loading)
        private set

    var myName: String by mutableStateOf("")
        private set

    var draft: String by mutableStateOf("")
        private set

    fun onNameChange(value: String) { myName = value }
    fun onDraftChange(value: String) { draft = value }

    init { load() }

    fun load() {
        viewModelScope.launch {
            uiState = ChatUiState.Loading
            uiState = when (val result = repository.getMessages()) {
                is AppResult.Success -> {
                    if (result.data.isEmpty()) {
                        ChatUiState.Empty
                    } else {
                        ChatUiState.Ready(result.data)
                    }
                }
                is AppResult.Failure.NoInternet -> ChatUiState.Error("No internet connection.")
                is AppResult.Failure.Timeout -> ChatUiState.Error("The server took too long.")
                is AppResult.Failure -> ChatUiState.Error("Something went wrong.")
            }
        }
    }

    fun send() {
        if (myName.isBlank() || draft.isBlank()) return

        viewModelScope.launch {
            when (repository.sendMessage(myName, draft)) {
                is AppResult.Success -> {
                    draft = ""
                    load()
                }
                is AppResult.Failure -> {
                    uiState = ChatUiState.Error("Could not send. Check your connection.")
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val database = androidx.room.Room.databaseBuilder(
                    application,
                    ChatDatabase::class.java,
                    "chat_database"
                ).build()
                ChatViewModel(
                    ChatRepositoryImpl(
                        NetworkModule.chatApi,
                        database.messageDao()
                    )
                )
            }
        }
    }
}
