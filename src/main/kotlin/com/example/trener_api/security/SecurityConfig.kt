package com.example.trener_api.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

// Правила доступа ко всему API
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // CSRF-защита нужна для сайтов с формами и cookie. У нас API с логином в каждом запросе — отключаем
            .csrf { it.disable() }
            // Сервер не помнит пользователя между запросами: логин и пароль приходят каждый раз
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    // Открыто для всех: регистрация, документация, страница ошибок
                    .requestMatchers("/api/auth/register").permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    // Всё остальное — только после входа
                    .anyRequest().authenticated()
            }
            // HTTP Basic: логин и пароль в заголовке Authorization каждого запроса
            .httpBasic(Customizer.withDefaults())
        return http.build()
    }

    // BCrypt — надёжный способ превратить пароль в хэш. Из хэша пароль обратно не получить
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
