-- V2: индексы — «оглавление» для частых запросов, чтобы база не перебирала все строки.

-- Список тренировок пользователя по дате: findAllByOwnerOrderByWorkoutDateDesc
create index idx_workout_session_user_date on workout_session (user_id, workout_date);

-- Подходы одной тренировки и статистика по упражнениям (join по session_id)
create index idx_set_entry_session on set_entry (session_id);
