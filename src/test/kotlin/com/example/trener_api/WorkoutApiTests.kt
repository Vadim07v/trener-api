package com.example.trener_api

import com.example.trener_api.model.AppUser
import com.example.trener_api.repository.AppUserRepository
import com.jayway.jsonpath.JsonPath
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

// @SpringBootTest — поднимает всё приложение целиком (как при запуске ▶), но с базой H2.
// @Transactional — после КАЖДОГО теста всё, что он записал, откатывается.
// @WithMockUser — каждый тест по умолчанию выполняется «от имени» пользователя tester.
@SpringBootTest
@Transactional
@WithMockUser(username = "tester")
class WorkoutApiTests {

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var users: AppUserRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    // MockMvc — «Postman внутри теста»: отправляет запросы в приложение без настоящей сети
    lateinit var mvc: MockMvc

    @BeforeEach
    fun setUp() {
        // springSecurity() — включаем проверку логина и в тестах, как в настоящем приложении
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()
        // Пользователи должны существовать в базе: сервис ищет «хозяина» тренировки по логину
        users.save(AppUser(username = "tester", passwordHash = encoder.encode("secret123")!!))
        users.save(AppUser(username = "other", passwordHash = encoder.encode("secret123")!!))
    }

    // ---------- Помощники ----------

    // JSON тренировки. Параметры со значениями по умолчанию — меняем только то, что нужно в тесте
    private fun sessionJson(date: String = "2026-10-19", feel: Int = 5, sets: String = "[]") =
        """{"workoutDate":"$date","dayName":"Test day","feel":$feel,"bodyWeight":69,"sets":$sets}"""

    private fun squat(weight: Int, reps: Int, setNumber: Int = 1) =
        """{"exercise":"Squat","setNumber":$setNumber,"weight":$weight,"reps":$reps,"rate":"ok"}"""

    private fun createRequest(json: String): ResultActions =
        mvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON).content(json))

    // Создать тренировку и вернуть её id
    private fun createAndGetId(json: String): Long {
        val body = createRequest(json).andReturn().response.contentAsString
        return JsonPath.read<Int>(body, "$.id").toLong()
    }

    private fun registerRequest(username: String, password: String = "secret123"): ResultActions =
        mvc.perform(
            post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""{"username":"$username","password":"$password"}""")
        )

    // ---------- Тренировки ----------

    @Test
    fun `создание тренировки возвращает 201 и считает тоннаж`() {
        createRequest(sessionJson(sets = "[${squat(50, 5)}, ${squat(50, 5, setNumber = 2)}]"))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.sets.length()").value(2))
            .andExpect(jsonPath("$.totalVolume").value(500.0)) // 50×5 + 50×5
    }

    @Test
    fun `несуществующая тренировка даёт 404`() {
        mvc.perform(get("/api/sessions/999999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value(containsString("999999")))
    }

    @Test
    fun `самочувствие 7 даёт 400 с ошибкой по полю feel`() {
        createRequest(sessionJson(feel = 7))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fields.feel").exists())
    }

    @Test
    fun `сломанный JSON даёт 400`() {
        createRequest("""{"workoutDate":"2026-10-19" "dayName":"Test"}""") // нет запятой
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `PUT заменяет тренировку вместе с подходами`() {
        val id = createAndGetId(sessionJson(sets = "[${squat(50, 5)}]"))

        mvc.perform(
            put("/api/sessions/$id").contentType(MediaType.APPLICATION_JSON)
                .content(sessionJson(feel = 3, sets = "[]"))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.feel").value(3))
            .andExpect(jsonPath("$.sets.length()").value(0))
    }

    @Test
    fun `после удаления тренировка не находится`() {
        val id = createAndGetId(sessionJson())

        mvc.perform(delete("/api/sessions/$id")).andExpect(status().isNoContent)
        mvc.perform(get("/api/sessions/$id")).andExpect(status().isNotFound)
    }

    // ---------- Статистика ----------

    @Test
    fun `статистика показывает рекорд и прогресс`() {
        createRequest(sessionJson(date = "2026-10-01", sets = "[${squat(50, 5)}]"))
        createRequest(sessionJson(date = "2026-10-08", sets = "[${squat(60, 5)}]"))

        mvc.perform(get("/api/stats/exercise").param("name", "squat")) // регистр не важен
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.workouts").value(2))
            .andExpect(jsonPath("$.bestWeight").value(60.0))
            .andExpect(jsonPath("$.progress").value(10.0)) // 60 − 50
            .andExpect(jsonPath("$.totalVolume").value(550.0)) // 50×5 + 60×5
    }

    @Test
    fun `статистика по незаписанному упражнению даёт 404`() {
        mvc.perform(get("/api/stats/exercise").param("name", "Nothing"))
            .andExpect(status().isNotFound)
    }

    // ---------- Безопасность ----------

    @Test
    @WithAnonymousUser // этот тест — «без входа»
    fun `без логина API отвечает 401`() {
        mvc.perform(get("/api/sessions"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `чужую тренировку не видно — 404`() {
        val id = createAndGetId(sessionJson()) // создал tester

        // with(user("other")) — этот один запрос делает другой пользователь
        mvc.perform(get("/api/sessions/$id").with(user("other")))
            .andExpect(status().isNotFound)
        mvc.perform(get("/api/sessions").with(user("other")))
            .andExpect(jsonPath("$.length()").value(0))
        mvc.perform(get("/api/stats/exercises").with(user("other")))
            .andExpect(jsonPath("$.length()").value(0))
    }

    @Test
    @WithAnonymousUser
    fun `регистрация и вход по логину и паролю`() {
        registerRequest("vadim")
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.username").value("vadim"))
            .andExpect(jsonPath("$.password").doesNotExist()) // пароль наружу не отдаём

        // httpBasic(...) — то же, что вкладка Authorization → Basic Auth в Postman
        mvc.perform(get("/api/auth/me").with(httpBasic("vadim", "secret123")))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("vadim"))

        mvc.perform(get("/api/auth/me").with(httpBasic("vadim", "wrong-pass")))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithAnonymousUser
    fun `занятый логин даёт 409, короткий пароль — 400`() {
        registerRequest("tester").andExpect(status().isConflict)
        registerRequest("newbie", password = "123")
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fields.password").exists())
    }
}
