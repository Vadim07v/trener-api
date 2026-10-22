package com.example.trener_api.service

import com.example.trener_api.dto.ExerciseStats
import com.example.trener_api.dto.ExerciseSummary
import com.example.trener_api.exception.NotFoundException
import com.example.trener_api.repository.SetEntryRepository
import com.example.trener_api.security.CurrentUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StatsService(
    private val setRepository: SetEntryRepository,
    private val currentUser: CurrentUser,
) {
    @Transactional(readOnly = true)
    fun exercises(): List<ExerciseSummary> = setRepository.exerciseSummaries(currentUser.get())

    @Transactional(readOnly = true)
    fun exercise(name: String): ExerciseStats {
        val rows = setRepository.dailyStats(currentUser.get(), name.trim())
        if (rows.isEmpty()) throw NotFoundException("Упражнение «$name» ещё не записано")

        val best = rows.filter { it.maxWeight != null }.maxByOrNull { it.maxWeight!! }
        val first = rows.first().maxWeight
        val last = rows.last().maxWeight

        return ExerciseStats(
            exercise = name.trim(),
            workouts = rows.size,
            totalSets = rows.sumOf { it.sets },
            bestWeight = best?.maxWeight,
            bestWeightDate = best?.date,
            firstMaxWeight = first,
            lastMaxWeight = last,
            progress = if (first != null && last != null) last - first else null,
            totalVolume = rows.sumOf { it.volume ?: 0.0 },
            history = rows,
        )
    }
}
