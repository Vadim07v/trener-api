package com.example.trener_api.model

import jakarta.persistence.*
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.time.LocalDate

@Entity
@Table(name = "workout_session")
class WorkoutSession(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "workout_date", nullable = false)
    var workoutDate: LocalDate,

    @Column(name = "day_name", nullable = false)
    @field:NotBlank(message = "Название дня не может быть пустым")
    var dayName: String,

    @field:Min(value = 1, message = "Самочувствие от 1 до 5")
    @field:Max(value = 5, message = "Самочувствие от 1 до 5")
    var feel: Int? = null,

    @Column(name = "body_weight")
    @field:Positive(message = "Вес тела должен быть больше 0")
    var bodyWeight: Double? = null,

    @Column(length = 1000)
    var note: String? = null,

    @OneToMany(mappedBy = "session", cascade = [CascadeType.ALL], orphanRemoval = true)
    @field:Valid
    var sets: MutableList<SetEntry> = mutableListOf(),
)