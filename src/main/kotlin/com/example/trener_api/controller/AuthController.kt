package com.example.trener_api.controller

import com.example.trener_api.dto.RegisterRequest
import com.example.trener_api.dto.UserResponse
import com.example.trener_api.security.CurrentUser
import com.example.trener_api.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "Пользователи", description = "Регистрация и проверка входа")
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val auth: AuthService,
    private val currentUser: CurrentUser,
) {
    @Operation(summary = "Регистрация", description = "Открыто для всех. 409, если логин занят")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody req: RegisterRequest): UserResponse = auth.register(req)

    @Operation(summary = "Кто я", description = "Проверка логина и пароля: вернёт твой профиль или 401")
    @GetMapping("/me")
    fun me(): UserResponse = currentUser.get().let { UserResponse(id = it.id!!, username = it.username) }
}
