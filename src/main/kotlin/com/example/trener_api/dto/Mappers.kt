package com.example.trener_api.dto

import com.example.trener_api.model.SetEntry
import com.example.trener_api.model.WorkoutSession

// Запрос → сущность подхода
fun SetRequest.toEntity(session: WorkoutSession) = SetEntry(
    exercise = exercise,
    setNumber = setNumber,
    weight = weight,
    reps = reps,
    rate = rate,
    session = session,
)

// Сущность подхода → ответ
fun SetEntry.toResponse() = SetResponse(
    id = id!!,
    exercise = exercise,
    setNumber = setNumber,
    weight = weight,
    reps = reps,
    rate = rate,
)

// Сущность тренировки → ответ
fun WorkoutSession.toResponse() = SessionResponse(
    id = id!!,
    workoutDate = workoutDate,
    dayName = dayName,
    feel = feel,
    bodyWeight = bodyWeight,
    durationMinutes = durationMinutes,
    note = note,
    sets = sets.sortedBy { it.id }.map { it.toResponse() },
    totalVolume = sets.sumOf { (it.weight ?: 0.0) * (it.reps ?: 0) },
)
