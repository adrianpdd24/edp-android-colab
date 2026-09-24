package com.liceo.account.data

import com.liceo.account.core.AppResult
import com.liceo.account.data.network.NetworkModule
import com.liceo.account.data.network.UserApiService
import com.liceo.account.data.network.dto.NewUserDto
import com.liceo.account.data.network.dto.UserDto
import com.liceo.account.data.network.dto.toDomain
import com.liceo.account.domain.model.User
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class UserRepository(
    private val api: UserApiService = NetworkModule.userApi
) {
    private suspend fun findUsers(email: String): List<UserDto> =
        try {
            api.findByEmail(email)
        } catch (e: HttpException) {
            if (e.code() == 404) emptyList() else throw e
        }

    private inline fun <T> safeCall(block: () -> AppResult<T>): AppResult<T> =
        try {
            block()
        } catch (e: UnknownHostException) {
            AppResult.Failure.NoInternet
        } catch (e: SocketTimeoutException) {
            AppResult.Failure.Timeout
        } catch (e: HttpException) {
            AppResult.Failure.Unknown("Server error ${e.code()}")
        } catch (e: SerializationException) {
            AppResult.Failure.Unknown("The server sent data we could not read.")
        } catch (e: IOException) {
            AppResult.Failure.NoInternet
        }

    // TODO 6: Login implementation
    suspend fun login(email: String, password: String): AppResult<User> = safeCall {
        // TODO 6a: Search fuzzy matches
        val matches = findUsers(email.trim())

        // TODO 6b: Strict exact check for email and password
        val found = matches.firstOrNull {
            it.email.equals(email.trim(), ignoreCase = true) && it.password == password
        }

        // TODO 6c: Map result
        if (found == null) AppResult.Failure.WrongLogin else AppResult.Success(found.toDomain())
    }

    // TODO 7: Registration implementation
    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        birthdate: String
    ): AppResult<User> = safeCall {
        // TODO 7a: Check duplicate exact email
        val taken = findUsers(email.trim()).any {
            it.email.equals(email.trim(), ignoreCase = true)
        }

        // TODO 7b & 7c: Branch logic
        if (taken) {
            AppResult.Failure.EmailTaken
        } else {
            val saved = api.createUser(
                NewUserDto(
                    fullname = fullName.trim(),
                    email = email.trim(),
                    password = password,
                    birthdate = birthdate.trim()
                )
            )
            AppResult.Success(saved.toDomain())
        }
    }
}