package com.example.trener_api.repository

import com.example.trener_api.model.AppUser
import org.springframework.data.jpa.repository.JpaRepository

interface AppUserRepository : JpaRepository<AppUser, Long> {
    // Kotlin: «AppUser?» — может вернуть null, если такого логина нет
    fun findByUsername(username: String): AppUser?
    fun existsByUsername(username: String): Boolean
}
