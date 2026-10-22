package com.example.trener_api.model

import jakarta.persistence.*

// Пользователь приложения.
// Таблица называется app_user, потому что слово user в PostgreSQL зарезервировано.
@Entity
@Table(name = "app_user")
class AppUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    var username: String,

    // Храним НЕ сам пароль, а его хэш (BCrypt). Даже владелец базы не узнает пароль.
    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,
)
