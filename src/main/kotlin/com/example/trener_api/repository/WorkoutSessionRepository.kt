package com.example.trener_api.repository

import com.example.trener_api.model.AppUser
import com.example.trener_api.model.WorkoutSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface WorkoutSessionRepository : JpaRepository<WorkoutSession, Long> {

    // Тренировки только этого пользователя, новые сверху
    fun findAllByOwnerOrderByWorkoutDateDesc(owner: AppUser): List<WorkoutSession>

    // Тренировка по id, но только если она принадлежит этому пользователю
    fun findByIdAndOwner(id: Long, owner: AppUser): WorkoutSession?

    // Старые тренировки (созданные до появления пользователей) отдать первому пользователю
    @Modifying(clearAutomatically = true)
    @Query("update WorkoutSession s set s.owner = :owner where s.owner is null")
    fun assignOrphansTo(@Param("owner") owner: AppUser): Int
}
