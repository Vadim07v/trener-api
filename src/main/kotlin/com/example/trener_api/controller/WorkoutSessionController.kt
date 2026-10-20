package com.example.trener_api.controller

import com.example.trener_api.dto.SessionRequest
import com.example.trener_api.dto.SessionResponse
import com.example.trener_api.service.WorkoutService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

// Контроллер «тонкий»: адреса, проверка входных данных и вызов сервиса.
// @Tag и @Operation — подписи для страницы Swagger
@Tag(name = "Тренировки", description = "Создание, просмотр, изменение и удаление тренировок")
@RestController
@RequestMapping("/api/sessions")
class WorkoutSessionController(
    private val service: WorkoutService
) {
    @Operation(summary = "Все тренировки", description = "Новые сверху. У каждой — подходы и тоннаж")
    @GetMapping
    fun getAll(): List<SessionResponse> = service.getAll()

    @Operation(summary = "Одна тренировка по id", description = "404, если такой нет")
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): SessionResponse = service.getOne(id)

    @Operation(summary = "Создать тренировку", description = "Вместе с подходами. 400, если данные не прошли проверку")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: SessionRequest): SessionResponse = service.create(req)

    @Operation(summary = "Изменить тренировку", description = "Заменяет тренировку целиком, включая все подходы")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody req: SessionRequest): SessionResponse =
        service.update(id, req)

    @Operation(summary = "Удалить тренировку", description = "Удаляет вместе с подходами")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = service.delete(id)
}
