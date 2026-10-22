package com.example.trener_api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Укажи логин")
    @field:Size(min = 3, max = 50, message = "Логин от 3 до 50 символов")
    @field:Pattern(regexp = "[A-Za-z0-9_.-]+", message = "Логин: латинские буквы, цифры, точка, дефис, подчёркивание")
    val username: String,

    @field:Size(min = 6, max = 100, message = "Пароль от 6 до 100 символов")
    val password: String,
)

// В ответе пароля нет — никогда не отдаём его наружу, даже хэш
data class UserResponse(
    val id: Long,
    val username: String,
)
