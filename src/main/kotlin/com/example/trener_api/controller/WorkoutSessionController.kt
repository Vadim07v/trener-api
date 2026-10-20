package com.example.trener_api.controller

import com.example.trener_api.dto.SessionRequest
import com.example.trener_api.dto.SessionResponse
import com.example.trener_api.service.WorkoutService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

// Контроллер стал «тонким»: адреса, проверка входных данных и вызов сервиса.
@RestController
@RequestMapping("/api/sessions")
class WorkoutSessionController(
    private val service: WorkoutService
) {
    @GetMapping
    fun getAll(): List<SessionResponse> = service.getAll()

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): SessionResponse = service.getOne(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: SessionRequest): SessionResponse = service.create(req)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody req: SessionRequest): SessionResponse =
        service.update(id, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = service.delete(id)
}
