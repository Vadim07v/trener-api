package com.example.trener_api.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.time.LocalDate

// ===== Что ПРИХОДИТ от клиента =====

data class SetRequest(
    @field:NotBlank(message = "Укажи упражнение")
    val exercise: String,

    @field:Min(value = 1, message = "Номер подхода с 1")
    val setNumber: Int = 1,

    @field:PositiveOrZero(message = "Вес не может быть отрицательным")
    val weight: Double? = null,

    @field:Min(value = 1, message = "Повторов хотя бы 1")
    val reps: Int? = null,

    @field:Pattern(regexp = "easy|ok|hard", message = "Оценка: easy, ok или hard")
    val rate: String? = null,
)

data class SessionRequest(
    val workoutDate: LocalDate,

    @field:NotBlank(message = "Название дня не может быть пустым")
    val dayName: String,

    @field:Min(value = 1, message = "Самочувствие от 1 до 5")
    @field:Max(value = 5, message = "Самочувствие от 1 до 5")
    val feel: Int? = null,

    @field:Positive(message = "Вес тела должен быть больше 0")
    val bodyWeight: Double? = null,

    @field:Size(max = 1000, message = "Заметка до 1000 символов")
    val note: String? = null,

    @field:Valid
    val sets: List<SetRequest> = emptyList(),
)

// ===== Что УХОДИТ клиенту =====

data class SetResponse(
    val id: Long,
    val exercise: String,
    val setNumber: Int,
    val weight: Double?,
    val reps: Int?,
    val rate: String?,
)

data class SessionResponse(
    val id: Long,
    val workoutDate: LocalDate,
    val dayName: String,
    val feel: Int?,
    val bodyWeight: Double?,
    val note: String?,
    val sets: List<SetResponse>,
    val totalVolume: Double, // тоннаж: сумма вес × повторы, в базе не хранится
)
