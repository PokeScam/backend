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
class SetsIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val mapper: ObjectMapper
) {
    data class PageResponse<T>(val content: List<T>, val number: Int, val size: Int, val totalElements: Long)
    data class SetDto(val id: String, val name: String?, val releaseDate: String?, val seriesId: String?, val seriesName: String?)

    @Test
    fun `GET sets basic paging works`() {
        mockMvc.get("/api/sets") { param("size", "10") }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.content.length()") { value(lessThanOrEqualTo(10)) }
                jsonPath("$.totalElements") { value(greaterThan(0)) }
            }
    }

    @Test
    fun `GET sets filter by q`() {
        val initial = mockMvc.get("/api/sets") { param("size", "30") }.andReturn()
        val page: PageResponse<SetDto> = mapper.readValue(initial.response.contentAsString, object : TypeReference<PageResponse<SetDto>>() {})
        val token = page.content.firstOrNull { !it.name.isNullOrBlank() }?.name?.split(" ")?.firstOrNull { it.length >= 3 }
        assumeTrue(!token.isNullOrBlank(), "no set name in test DB")

        mockMvc.get("/api/sets") {
            param("q", token!!)
            param("size","20")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.length()") { value(greaterThan(0)) }
        }
    }

    @Test
    fun `GET sets filter by seriesId if available`() {
        val initial = mockMvc.get("/api/sets") { param("size", "50") }.andReturn()
        val page: PageResponse<SetDto> = mapper.readValue(initial.response.contentAsString, object : TypeReference<PageResponse<SetDto>>() {})
        val anySeriesId = page.content.firstOrNull { !it.seriesId.isNullOrBlank() }?.seriesId
        assumeTrue(!anySeriesId.isNullOrBlank(), "no seriesId on any set")

        mockMvc.get("/api/sets") {
            param("seriesId", anySeriesId!!)
            param("size","50")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content[*].seriesId", everyItem(equalTo(anySeriesId)))
        }
    }

    @Test
    fun `GET sets invalid seriesId pattern 400`() {
        mockMvc.get("/api/sets") { param("seriesId", "bad;DROP") }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.title", containsStringIgnoringCase("validation"))
            }
    }
}
