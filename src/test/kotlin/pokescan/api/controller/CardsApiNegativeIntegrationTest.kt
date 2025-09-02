package de.pokescan.api.controller

import org.hamcrest.Matchers.*
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
                jsonPath("$.detail", containsString("page must be >= 0"))
            }
    }

    // 400 wegen Validation: size 0
    @Test
    fun `size zero 400`() {
        mockMvc.get("/api/cards") { param("size", "0") }
            .andExpect {
                status { isBadRequest() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
                jsonPath("$.detail", containsString("size must be >= 1"))
            }
    }

    // 400 wegen Validation: size > 200 (Rate-Limit Schutz)
    @Test
    fun `size too large 400`() {
        mockMvc.get("/api/cards") { param("size", "1000") }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.detail", containsString("size must be <= 200"))
            }
    }

    // 400 wegen ungültigem set-Pattern
    @Test
    fun `set invalid chars 400`() {
        mockMvc.get("/api/cards") { param("set", "DROP TABLE;") }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.detail", containsString("set must be alphanumeric"))
            }
    }

    // 400 wegen ungültigem q-Pattern
    @Test
    fun `q invalid chars 400`() {
        mockMvc.get("/api/cards") { param("q", "abc%%%^") }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.detail", containsString("q contains invalid characters"))
            }
    }

    // SQLi-ähnliche Eingabe in q -> sollte KEIN Fehler werfen, sondern 200 + (meist) 0 Treffer
    @Test
    fun `q looks like SQL but is harmless 400 due to validation`() {
        mockMvc.get("/api/cards") {
            param("q", "%' OR '1'='1")   // enthält %, ist laut Regex verboten
            param("size", "5")
        }.andExpect {
            status { isBadRequest() }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.title") { value(containsStringIgnoringCase("validation")) }
            jsonPath("$.detail", containsString("q contains invalid characters"))
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
