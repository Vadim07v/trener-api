package com.example.trener_api.controller

import com.example.trener_api.dto.*
import com.example.trener_api.exception.NotFoundException
import com.example.trener_api.model.WorkoutSession
import com.example.trener_api.repository.WorkoutSessionRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sessions")
class WorkoutSessionController(
    private val repository: WorkoutSessionRepository
) {
    private fun findOr404(id: Long): WorkoutSession =
        repository.findById(id).orElseThrow { NotFoundException("Тренировка $id не найдена") }

    // GET /api/sessions — все тренировки, новые сверху
    @GetMapping
    fun getAll(): List<SessionResponse> =
        repository.findAllByOrderByWorkoutDateDesc().map { it.toResponse() }

    // GET /api/sessions/2 — одна тренировка
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): SessionResponse =
        findOr404(id).toResponse()

    // POST /api/sessions — создать
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: SessionRequest): SessionResponse {
        val session = WorkoutSession(
            workoutDate = req.workoutDate,
            dayName = req.dayName,
            feel = req.feel,
            bodyWeight = req.bodyWeight,
            note = req.note,
        )
        req.sets.forEach { session.sets.add(it.toEntity(session)) }
        return repository.saveAndFlush(session).toResponse()
    }

    // PUT /api/sessions/2 — заменить тренировку целиком (вместе с подходами)
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody req: SessionRequest): SessionResponse {
        val session = findOr404(id)
        session.workoutDate = req.workoutDate
        session.dayName = req.dayName
        session.feel = req.feel
        session.bodyWeight = req.bodyWeight
        session.note = req.note
        session.sets.clear()
        req.sets.forEach { session.sets.add(it.toEntity(session)) }
        return repository.saveAndFlush(session).toResponse()
    }

    // DELETE /api/sessions/2 — удалить (вместе с подходами)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        repository.delete(findOr404(id))
    }
}
