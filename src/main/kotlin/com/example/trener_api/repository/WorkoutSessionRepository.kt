package com.example.trener_api.repository

import com.example.trener_api.model.WorkoutSession
import org.springframework.data.jpa.repository.JpaRepository

interface WorkoutSessionRepository : JpaRepository<WorkoutSession, Long> {
    // Spring сам поймёт по названию: «найти все, отсортировать по дате, новые сверху»
    fun findAllByOrderByWorkoutDateDesc(): List<WorkoutSession>
}