package com.example.trener_api.repository

import com.example.trener_api.dto.ExerciseDayRow
import com.example.trener_api.dto.ExerciseSummary
import com.example.trener_api.model.AppUser
import com.example.trener_api.model.SetEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SetEntryRepository : JpaRepository<SetEntry, Long> {

    // Все упражнения пользователя: сколько подходов и лучший вес
    @Query(
        "select new com.example.trener_api.dto.ExerciseSummary(e.exercise, count(e), max(e.weight)) " +
            "from SetEntry e join e.session s " +
            "where s.owner = :owner " +
            "group by e.exercise " +
            "order by e.exercise"
    )
    fun exerciseSummaries(@Param("owner") owner: AppUser): List<ExerciseSummary>

    // Одно упражнение пользователя: одна строка на каждую дату тренировки
    @Query(
        "select new com.example.trener_api.dto.ExerciseDayRow(" +
            "s.workoutDate, max(e.weight), sum(e.reps), " +
            "sum(coalesce(e.weight, 0.0) * coalesce(e.reps, 0)), count(e)) " +
            "from SetEntry e join e.session s " +
            "where s.owner = :owner and lower(e.exercise) = lower(:exercise) " +
            "group by s.workoutDate " +
            "order by s.workoutDate"
    )
    fun dailyStats(@Param("owner") owner: AppUser, @Param("exercise") exercise: String): List<ExerciseDayRow>
}
