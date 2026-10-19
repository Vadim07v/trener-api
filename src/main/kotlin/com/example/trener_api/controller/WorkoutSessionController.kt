package com.example.trener_api.controller

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
    // Вспомогательная функция: найти или выбросить 404
    private fun findOr404(id: Long): WorkoutSession =
        repository.findById(id).orElseThrow { NotFoundException("Тренировка $id не найдена") }

    // GET /api/sessions — все тренировки
    @GetMapping
    fun getAll(): List<WorkoutSession> =
        repository.findAllByOrderByWorkoutDateDesc()

    // GET /api/sessions/2 — одна тренировка
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): WorkoutSession = findOr404(id)

    // POST /api/sessions — создать
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody session: WorkoutSession): WorkoutSession {
        session.sets.forEach { it.session = session }
        return repository.save(session)
    }

    // PUT /api/sessions/2 — заменить тренировку целиком (вместе с подходами)
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody body: WorkoutSession): WorkoutSession {
        val session = findOr404(id)
        session.workoutDate = body.workoutDate
        session.dayName = body.dayName
        session.feel = body.feel
        session.bodyWeight = body.bodyWeight
        session.note = body.note
        session.sets.clear()                 // старые подходы удалятся (orphanRemoval)
        body.sets.forEach {
            it.id = null                     // подходы записываем заново
            it.session = session
            session.sets.add(it)
        }
        return repository.save(session)
    }

    // DELETE /api/sessions/2 — удалить (вместе с подходами)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        repository.delete(findOr404(id))
    }
}