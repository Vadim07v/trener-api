package com.example.trener_api.service

import com.example.trener_api.dto.RegisterRequest
import com.example.trener_api.dto.UserResponse
import com.example.trener_api.exception.ConflictException
import com.example.trener_api.model.AppUser
import com.example.trener_api.repository.AppUserRepository
import com.example.trener_api.repository.WorkoutSessionRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val users: AppUserRepository,
    private val sessions: WorkoutSessionRepository,
    private val encoder: PasswordEncoder,
) {
    @Transactional
    fun register(req: RegisterRequest): UserResponse {
        val username = req.username.trim()
        if (users.existsByUsername(username)) {
            throw ConflictException("Логин «$username» уже занят")
        }
        val isFirstUser = users.count() == 0L

        val hash = requireNotNull(encoder.encode(req.password)) { "Не удалось захэшировать пароль" }
        val user = users.save(AppUser(username = username, passwordHash = hash))

        // Первый зарегистрированный получает тренировки, созданные до появления пользователей
        if (isFirstUser) sessions.assignOrphansTo(user)

        return UserResponse(id = user.id!!, username = user.username)
    }
}
