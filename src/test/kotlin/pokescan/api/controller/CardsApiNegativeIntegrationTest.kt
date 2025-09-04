package de.pokescan.api.controller

import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.containsStringIgnoringCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CardsApiNegativeIntegrationTest(
    @Autowired private val mockMvc: MockMvc
) {
    // 400 wegen Validation: page < 0
    @Test
    fun `page negative 400`() {
        mockMvc.get("/api/cards") { param("page", "-1") }
            .andExpect {
                status { isBadRequest() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
                jsonPath("$.title") { value(containsStringIgnoringCase("validation")) }
                // Standard-Message von @PositiveOrZero:
                jsonPath("$.detail", containsString("greater than or equal to 0"))
                jsonPath("$.detail", containsString("page"))
            }
    }

    // 400 wegen Validation: size 0
    @Test
    fun `size zero 400`() {
        mockMvc.get("/api/cards") { param("size", "0") }
            .andExpect {
                status { isBadRequest() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
                jsonPath("$.detail", containsString("greater than or equal to 1"))
                jsonPath("$.detail", containsString("size"))
            }
    }

    // 400 wegen Validation: size > 200 (Rate-Limit Schutz)
    @Test
    fun `size too large 400`() {
        mockMvc.get("/api/cards") { param("size", "1000") }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.detail", containsString("less than or equal to 200"))
                jsonPath("$.detail", containsString("size"))
            }
    }

    // 400 wegen ungültigem set-Pattern
    @Test
    fun `set invalid chars 400`() {
        mockMvc.get("/api/cards") { param("set", "DROP TABLE;") }
            .andExpect {
                status { isBadRequest() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
                // ConstraintViolationException-Text enthält meist "must match" + Param-Namen (setLocId)
                jsonPath("$.detail", containsString("must match"))
            }
    }

    // 400 wegen ungültigem q-Pattern
    @Test
    fun `q invalid chars 400`() {
        mockMvc.get("/api/cards") { param("q", "abc%%%^") }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.detail", containsString("must match"))
            }
    }

    // SQLi-ähnliche Eingabe in q -> 400 wegen Regex
    @Test
    fun `q looks like SQL but is harmless 400 due to validation`() {
        mockMvc.get("/api/cards") {
            param("q", "%' OR '1'='1")
            param("size", "5")
        }.andExpect {
            status { isBadRequest() }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.title") { value(containsStringIgnoringCase("validation")) }
            jsonPath("$.detail", containsString("must match"))
        }
    }

    // hoher page-Index -> 200 + leere Seite erlaubt
    @Test
    fun `page out of range 200 empty`() {
        mockMvc.get("/api/cards") {
            param("page", "999999")
            param("size", "50")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.length()") { value(0) }
        }
    }

    // filter auf nicht existente rarity -> 200 + leer
    @Test
    fun `non-existing rarity 200 empty`() {
        mockMvc.get("/api/cards") {
            param("rarity", "ThisDoesNotExist123")
            param("size", "50")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.length()") { value(0) }
        }
    }

    @Test
    fun `page not a number 400`() {
        mockMvc.get("/api/cards") { param("page", "abc") }
            .andExpect {
                status { isBadRequest() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
                jsonPath("$.title") { value(containsStringIgnoringCase("validation")) }
                jsonPath("$.detail", containsString("invalid type for 'page'"))
            }
    }

    @Test
    fun `size not a number 400`() {
        mockMvc.get("/api/cards") { param("size", "foo") }
            .andExpect {
                status { isBadRequest() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
                jsonPath("$.title") { value(containsStringIgnoringCase("validation")) }
                jsonPath("$.detail", containsString("invalid type for 'size'"))
            }
    }
}
