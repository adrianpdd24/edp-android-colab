package com.liceo.account.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liceo.account.core.AppResult
import com.liceo.account.data.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Idle)
        private set

    private val datePattern = Regex("""\d{4}-\d{2}-\d{2}""")

    fun clearMessage() { uiState = AuthUiState.Idle }
    fun logout() { uiState = AuthUiState.Idle }

    private fun messageFor(failure: AppResult.Failure): String = when (failure) {
        AppResult.Failure.NoInternet -> "No internet connection. Please try again."
        AppResult.Failure.Timeout -> "The server was too slow. Please try again."
        AppResult.Failure.WrongLogin -> "Wrong email or password."
        AppResult.Failure.EmailTaken -> "An account with this email already exists."
        is AppResult.Failure.Unknown -> "Something went wrong: ${failure.msg}"
    }

    // TODO 8: Login logic
    fun login(email: String, password: String) {
        // TODO 8a: Input check
        if (email.isBlank() || password.isBlank()) {
            uiState = AuthUiState.Error("Please enter your email and password.")
            return
        }

        // TODO 8b: Set loading state
        uiState = AuthUiState.Loading

        // TODO 8c: Coroutine invocation
        viewModelScope.launch {
            uiState = when (val result = repository.login(email, password)) {
                is AppResult.Success -> AuthUiState.LoggedIn(result.data)
                is AppResult.Failure -> AuthUiState.Error(messageFor(result))
            }
        }
    }

    // TODO 9: Registration logic
    fun register(fullName: String, email: String, password: String, birthdate: String) {
        // TODO 9a: Check blank fields
        if (fullName.isBlank() || email.isBlank() || password.isBlank() || birthdate.isBlank()) {
            uiState = AuthUiState.Error("Please fill in all four fields.")
            return
        }

        // TODO 9b: Validate email format
        if (!email.contains("@")) {
            uiState = AuthUiState.Error("Please enter a valid email.")
            return
        }

        // TODO 9c: Validate password length
        if (password.length < 6) {
            uiState = AuthUiState.Error("Password must be at least 6 characters.")
            return
        }

        // TODO 9d: Validate date format pattern
        if (!datePattern.matches(birthdate.trim())) {
            uiState = AuthUiState.Error("Birthdate must look like 2004-05-17.")
            return
        }

        // TODO 9e: Trigger loading & API call
        uiState = AuthUiState.Loading

        viewModelScope.launch {
            uiState = when (val result = repository.register(fullName, email, password, birthdate)) {
                is AppResult.Success -> AuthUiState.AccountCreated(result.data.fullName)
                is AppResult.Failure -> AuthUiState.Error(messageFor(result))
            }
        }
    }
}