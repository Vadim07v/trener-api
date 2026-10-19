package com.example.trener_api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.*

@Entity
@Table(name = "set_entry")
class SetEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    @field:NotBlank(message = "Укажи упражнение")
    var exercise: String,

    @Column(name = "set_number")
    @field:Min(value = 1, message = "Номер подхода с 1")
    var setNumber: Int = 1,

    @field:PositiveOrZero(message = "Вес не может быть отрицательным")
    var weight: Double? = null,

    @field:Min(value = 1, message = "Повторов хотя бы 1")
    var reps: Int? = null,

    @field:Pattern(regexp = "easy|ok|hard", message = "Оценка: easy, ok или hard")
    var rate: String? = null,

    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonIgnore
    var session: WorkoutSession? = null,
)