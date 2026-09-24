package com.liceo.account.core

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    sealed interface Failure : AppResult<Nothing> {
        data object NoInternet : Failure
        data object Timeout : Failure
        data object WrongLogin : Failure
        data object EmailTaken : Failure
        data class Unknown(val msg: String?) : Failure
    }
}