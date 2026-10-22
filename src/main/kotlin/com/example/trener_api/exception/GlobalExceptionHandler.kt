package com.example.trener_api.exception

import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice   // «перехватчик» ошибок для всех контроллеров
class GlobalExceptionHandler {

    // Не найдено → 404
    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFound(e: NotFoundException) =
        mapOf("error" to (e.message ?: "Не найдено"))

    // Уже существует (занятый логин) → 409
    @ExceptionHandler(ConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun conflict(e: ConflictException) =
        mapOf("error" to (e.message ?: "Уже существует"))

    // Поля не прошли проверку (@Min, @NotBlank…) → 400 + какие поля и что не так
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun invalid(e: MethodArgumentNotValidException) = mapOf(
        "error" to "Ошибка в данных",
        "fields" to e.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "неверное значение")
        }
    )

    // JSON сломан или нет обязательного поля → 400
    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun unreadable(e: HttpMessageNotReadableException) =
        mapOf("error" to "Не получилось прочитать JSON: проверь запятые, кавычки и обязательные поля")

    // Нет обязательного параметра в адресе (например, ?name=…) → 400
    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun missingParam(e: MissingServletRequestParameterException) =
        mapOf("error" to "Не хватает параметра «${e.parameterName}» в адресе")
}
