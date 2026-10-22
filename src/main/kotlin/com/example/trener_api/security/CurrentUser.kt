package com.example.trener_api.security

import com.example.trener_api.exception.NotFoundException
import com.example.trener_api.model.AppUser
import com.example.trener_api.repository.AppUserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

// «Кто сейчас делает запрос?» — Spring Security хранит это в SecurityContextHolder
@Component
class CurrentUser(
    private val users: AppUserRepository
) {
    fun get(): AppUser {
        val username = SecurityContextHolder.getContext().authentication?.name
            ?: throw IllegalStateException("Запрос без входа в систему")
        return users.findByUsername(username)
            ?: throw NotFoundException("Пользователь $username не найден")
    }
}
