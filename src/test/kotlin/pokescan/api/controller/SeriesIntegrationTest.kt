package de.pokescan.api.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
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
class SeriesIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val mapper: ObjectMapper
) {
    data class PageResponse<T>(val content: List<T>, val number: Int, val size: Int, val totalElements: Long)
    data class SeriesDto(val id: String, val name: String?, val releaseYear: Int?, val lang: String?)

    @Test
    fun `GET series basic paging works`() {
        mockMvc.get("/api/series") { param("size", "10") }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.content.length()") { value(lessThanOrEqualTo(10)) }
                jsonPath("$.totalElements") { value(greaterThan(0)) }
            }
    }

    @Test
    fun `GET series q filter`() {
        val resp = mockMvc.get("/api/series") { param("size", "30") }.andReturn()
        val page: PageResponse<SeriesDto> = mapper.readValue(
            resp.response.contentAsString, object : TypeReference<PageResponse<SeriesDto>>() {})
        val token = page.content.firstOrNull { !it.name.isNullOrBlank() }?.name?.split(" ")
            ?.firstOrNull { it.length >= 3 } ?: return

        mockMvc.get("/api/series") {
            param("q", token)
            param("size", "20")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.length()") { value(greaterThan(0)) }
        }
    }

    @Test
    fun `GET series invalid q 400`() {
        mockMvc.get("/api/series") { param("q", "%%%^^") }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.title", containsStringIgnoringCase("validation"))
                jsonPath("$.detail", containsString("must match"))
            }
    }
}
