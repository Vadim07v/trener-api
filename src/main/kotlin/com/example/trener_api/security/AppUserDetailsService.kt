package com.example.trener_api.security

import com.example.trener_api.repository.AppUserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

// Spring Security спрашивает этот класс: «есть ли такой логин и какой у него хэш пароля?»
// Сравнение пароля с хэшем Spring делает сам
@Service
class AppUserDetailsService(
    private val users: AppUserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = users.findByUsername(username)
            ?: throw UsernameNotFoundException("Пользователь $username не найден")
        return User.withUsername(user.username)
            .password(user.passwordHash)
            .roles("USER")
            .build()
    }
}
