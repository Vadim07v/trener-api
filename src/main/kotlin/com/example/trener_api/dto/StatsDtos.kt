package com.example.trener_api.dto

import java.time.LocalDate

// Одна строка из базы: что было в этом упражнении в один день
data class ExerciseDayRow(
    val date: LocalDate,
    val maxWeight: Double?,   // самый тяжёлый подход за день
    val totalReps: Long?,     // сколько всего повторов
    val volume: Double?,      // тоннаж: сумма вес × повторы
    val sets: Long,           // сколько подходов
)

// Краткая сводка для списка упражнений
data class ExerciseSummary(
    val exercise: String,
    val sets: Long,
    val bestWeight: Double?,
)

// Полная статистика по одному упражнению
data class ExerciseStats(
    val exercise: String,
    val workouts: Int,             // в скольких тренировках делал
    val totalSets: Long,
    val bestWeight: Double?,       // личный рекорд по весу
    val bestWeightDate: LocalDate?,
    val firstMaxWeight: Double?,   // максимум в первой тренировке
    val lastMaxWeight: Double?,    // максимум в последней тренировке
    val progress: Double?,         // насколько вырос рабочий вес, кг
    val totalVolume: Double,       // общий тоннаж за всё время
    val history: List<ExerciseDayRow>,
)
