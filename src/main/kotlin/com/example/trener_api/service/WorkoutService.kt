package com.example.trener_api.service

import com.example.trener_api.dto.*
import com.example.trener_api.exception.NotFoundException
import com.example.trener_api.model.WorkoutSession
import com.example.trener_api.repository.WorkoutSessionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// Сервис = вся логика работы с тренировками.
// Контроллер только принимает HTTP-запрос и передаёт его сюда.
@Service
class WorkoutService(
    private val repository: WorkoutSessionRepository
) {
    private fun findOr404(id: Long): WorkoutSession =
        repository.findById(id).orElseThrow { NotFoundException("Тренировка $id не найдена") }

    @Transactional(readOnly = true)
    fun getAll(): List<SessionResponse> =
        repository.findAllByOrderByWorkoutDateDesc().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getOne(id: Long): SessionResponse =
        findOr404(id).toResponse()

    // @Transactional: всё внутри метода — одна операция в базе.
    // Если что-то упадёт посередине, база откатится и не останется «половины» тренировки.
    @Transactional
    fun create(req: SessionRequest): SessionResponse {
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

    @Transactional
    fun update(id: Long, req: SessionRequest): SessionResponse {
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

    @Transactional
    fun delete(id: Long) {
        repository.delete(findOr404(id))
    }
}
