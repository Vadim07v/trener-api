package com.example.trener_api.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// Заголовок страницы Swagger + кнопка «Authorize» для входа по логину и паролю
@Configuration
class OpenApiConfig {
    @Bean
    fun trenerOpenApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Trener API")
                .version("1.1")
                .description("Дневник тренировок: тренировки, подходы и статистика прогресса по упражнениям")
                .contact(Contact().name("Вадим").url("https://github.com/Vadim07v/trener-api"))
        )
        .components(
            Components().addSecuritySchemes(
                "basicAuth",
                SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")
            )
        )
        .addSecurityItem(SecurityRequirement().addList("basicAuth"))
}
