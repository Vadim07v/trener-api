package com.example.trener_api.controller

import com.example.trener_api.dto.ExerciseStats
import com.example.trener_api.dto.ExerciseSummary
import com.example.trener_api.service.StatsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "Статистика", description = "Рекорды и прогресс по упражнениям")
@RestController
@RequestMapping("/api/stats")
class StatsController(
    private val service: StatsService
) {
    @Operation(summary = "Список упражнений", description = "Все упражнения, которые есть в базе: число подходов и лучший вес")
    @GetMapping("/exercises")
    fun exercises(): List<ExerciseSummary> = service.exercises()

    @Operation(summary = "Прогресс по упражнению", description = "Рекорд, прогресс рабочего веса, тоннаж и история по датам")
    @GetMapping("/exercise")
    fun exercise(
        @Parameter(description = "Название упражнения, например Подтягивания", example = "Подтягивания")
        @RequestParam("name") name: String
    ): ExerciseStats = service.exercise(name)
}
