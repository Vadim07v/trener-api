package com.example.trener_api.model

import jakarta.persistence.*
import java.time.LocalDate

// Правила проверки (@NotBlank, @Min…) теперь живут в DTO (SessionRequest),
// поэтому из сущности они убраны: сущность описывает только таблицу.
@Entity
@Table(name = "workout_session")
class WorkoutSession(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "workout_date", nullable = false)
    var workoutDate: LocalDate,

    @Column(name = "day_name", nullable = false)
    var dayName: String,

    var feel: Int? = null,

    @Column(name = "body_weight")
    var bodyWeight: Double? = null,

    @Column(length = 1000)
    var note: String? = null,

    @OneToMany(mappedBy = "session", cascade = [CascadeType.ALL], orphanRemoval = true)
    var sets: MutableList<SetEntry> = mutableListOf(),

    // Чья это тренировка. Много тренировок → один пользователь
    @ManyToOne
    @JoinColumn(name = "user_id")
    var owner: AppUser? = null,

    @Column(name = "duration_minutes")
    var durationMinutes: Int? = null,
)
