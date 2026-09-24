package com.liceo.account

import com.liceo.account.data.network.dto.UserDto
import com.liceo.account.data.network.dto.toDomain
import com.liceo.account.ui.AuthUiState
import com.liceo.account.ui.AuthViewModel
import com.liceo.account.ui.ageFrom
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryTest {

    @Test
    fun testUserDtoToDomainMapper() {
        val dto = UserDto(
            id = "12",
            fullname = " Juan Dela Cruz ",
            email = " juan.test01@liceo.test ",
            password = "secret123",
            birthdate = "2004-05-17"
        )
        val user = dto.toDomain()

        assertEquals("12", user.id)
        assertEquals("Juan Dela Cruz", user.fullName)
        assertEquals("juan.test01@liceo.test", user.email)
        assertEquals("2004-05-17", user.birthdate)
    }

    @Test
    fun testUserDtoMapperNullDefaults() {
        val dto = UserDto()
        val user = dto.toDomain()

        assertEquals("", user.id)
        assertEquals("(no name)", user.fullName)
        assertEquals("", user.email)
        assertEquals("(not set)", user.birthdate)
    }

    @Test
    fun testAuthViewModelValidationBlankLogin() {
        val viewModel = AuthViewModel()
        viewModel.login("", "")

        val state = viewModel.uiState
        assertTrue(state is AuthUiState.Error)
        assertEquals("Please enter your email and password.", (state as AuthUiState.Error).message)
    }

    @Test
    fun testAuthViewModelValidationBlankRegister() {
        val viewModel = AuthViewModel()
        viewModel.register("", "", "", "")

        val state = viewModel.uiState
        assertTrue(state is AuthUiState.Error)
        assertEquals("Please fill in all four fields.", (state as AuthUiState.Error).message)
    }

    @Test
    fun testAuthViewModelValidationInvalidEmailRegister() {
        val viewModel = AuthViewModel()
        viewModel.register("Juan", "invalidemail", "password123", "2004-05-17")

        val state = viewModel.uiState
        assertTrue(state is AuthUiState.Error)
        assertEquals("Please enter a valid email.", (state as AuthUiState.Error).message)
    }

    @Test
    fun testAuthViewModelValidationShortPasswordRegister() {
        val viewModel = AuthViewModel()
        viewModel.register("Juan", "juan@test.com", "123", "2004-05-17")

        val state = viewModel.uiState
        assertTrue(state is AuthUiState.Error)
        assertEquals("Password must be at least 6 characters.", (state as AuthUiState.Error).message)
    }

    @Test
    fun testAuthViewModelValidationInvalidBirthdateRegister() {
        val viewModel = AuthViewModel()
        viewModel.register("Juan", "juan@test.com", "123456", "05/17/2004")

        val state = viewModel.uiState
        assertTrue(state is AuthUiState.Error)
        assertEquals("Birthdate must look like 2004-05-17.", (state as AuthUiState.Error).message)
    }

    @Test
    fun testAgeFromCalculation() {
        val age = ageFrom("2000-01-01")
        assertTrue(age != null && age >= 24)

        val invalidAge = ageFrom("invalid-date")
        assertNull(invalidAge)
    }
}
