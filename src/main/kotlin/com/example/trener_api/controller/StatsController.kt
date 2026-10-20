package com.example.trener_api.controller

import com.example.trener_api.dto.ExerciseStats
import com.example.trener_api.dto.ExerciseSummary
import com.example.trener_api.service.StatsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/stats")
class StatsController(
    private val service: StatsService
) {
    // GET /api/stats/exercises — список всех упражнений
    @GetMapping("/exercises")
    fun exercises(): List<ExerciseSummary> = service.exercises()

    // GET /api/stats/exercise?name=Подтягивания — прогресс по одному упражнению
    @GetMapping("/exercise")
    fun exercise(@RequestParam("name") name: String): ExerciseStats = service.exercise(name)
}
