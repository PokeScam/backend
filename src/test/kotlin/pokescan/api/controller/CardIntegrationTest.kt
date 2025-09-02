package de.pokescan.api.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assumptions.assumeTrue
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
class CardsIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper
) {
    data class PageResponse<T>(val content: List<T>, val number: Int, val size: Int, val totalElements: Long)
    data class CardDto(
        val id: String, val setId: String, val localId: Int, val lang: String,
        val cardId: String, val name: String, val illustrator: String?, val rarity: String?, val category: Int
    )

    @Test
    fun `GET cards basic paging works`() {
        mockMvc.get("/api/cards") { param("size", "5") }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.content.length()") { value(5) }
                jsonPath("$.totalElements") { value(greaterThan(0)) }
                jsonPath("$.number") { value(0) }
            }
    }

    @Test
    fun `GET cards filter by rarity uses only that rarity`() {
        val initial = mockMvc.get("/api/cards") { param("size", "50") }.andReturn()
        val page: PageResponse<CardDto> = objectMapper.readValue(
            initial.response.contentAsString, object : TypeReference<PageResponse<CardDto>>() {})
        val rarity = page.content.firstOrNull { it.rarity != null }?.rarity
        assumeTrue(rarity != null, "no rarity present in test DB")
        mockMvc.get("/api/cards") { param("rarity", rarity!!) ; param("size","25") }
            .andExpect {
                status { isOk() }
                jsonPath("$.content.length()") { value(greaterThan(0)) }
                jsonPath("$.content[*].rarity", everyItem(equalTo(rarity)))
            }
    }

    @Test
    fun `GET cards filter by set`() {
        val sample = mockMvc.get("/api/cards") { param("size","1") }.andReturn()
        val p: PageResponse<CardDto> = objectMapper.readValue(
            sample.response.contentAsString, object : TypeReference<PageResponse<CardDto>>() {})
        val setId = p.content.firstOrNull()?.setId
        assumeTrue(!setId.isNullOrBlank(), "no setId present")
        mockMvc.get("/api/cards") { param("set", setId!!) ; param("size","25") }
            .andExpect {
                status { isOk() }
                jsonPath("$.content[*].setId", everyItem(equalTo(setId)))
            }
    }
}
