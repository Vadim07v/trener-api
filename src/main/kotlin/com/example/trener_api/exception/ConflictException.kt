package com.example.trener_api.exception

// «Такое уже есть» — например, занятый логин → 409 Conflict
class ConflictException(message: String) : RuntimeException(message)
