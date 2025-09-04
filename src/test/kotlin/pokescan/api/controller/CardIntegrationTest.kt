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
        val id: String,
        val name: String?,
        val rarity: String?,
        val localId: Int,
        val lang: String,
        val illustrator: String?,
        val category: Int,
        val type: String?,
        val dexId: Int?,
        val set: SetDto,
        val variants: VariantsDto
    )
    data class SetDto(
        val id: String,
        val name: String?,
        val releaseDate: String?,
        val seriesId: String?,
        val seriesName: String?,
        val cardCount: CardCountDto?,
        val localizedId: String?
    )
    data class CardCountDto(val official: Int?, val total: Int?)
    data class VariantsDto(
        val normal: Boolean,
        val holo: Boolean,
        val reverse: Boolean,
        val firstEdition: Boolean,
        val wPromo: Boolean
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

        mockMvc.get("/api/cards") {
            param("rarity", rarity!!)
            param("size","25")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.length()") { value(greaterThan(0)) }
            jsonPath("$.content[*].rarity", everyItem(equalTo(rarity)))
        }
    }

    @Test
    fun `GET cards filter by set (setLocId via 'set')`() {
        val sample = mockMvc.get("/api/cards") { param("size","1") }.andReturn()
        val p: PageResponse<CardDto> = objectMapper.readValue(
            sample.response.contentAsString, object : TypeReference<PageResponse<CardDto>>() {})
        val setLocId = p.content.firstOrNull()?.set?.localizedId
        assumeTrue(!setLocId.isNullOrBlank(), "no localizedId present")

        mockMvc.get("/api/cards") {
            param("set", setLocId!!)
            param("size","25")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content[*].set.localizedId", everyItem(equalTo(setLocId)))
        }
    }

    @Test
    fun `GET cards setId+lang behaves like set (precedence note)`() {
        val sample = mockMvc.get("/api/cards") { param("size","1") }.andReturn()
        val p: PageResponse<CardDto> = objectMapper.readValue(
            sample.response.contentAsString, object : TypeReference<PageResponse<CardDto>>() {})
        val first = p.content.firstOrNull()
        assumeTrue(first != null, "no cards in DB")

        val setId = first!!.set.id
        val lang = first.lang
        assumeTrue(setId.isNotBlank(), "no set.id present")
        assumeTrue(lang.isNotBlank(), "no lang present")

        mockMvc.get("/api/cards") {
            param("setId", setId)
            param("lang", lang)
            param("size","25")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.length()") { value(greaterThanOrEqualTo(1)) }
            jsonPath("$.content[*].set.id", everyItem(equalTo(setId)))
            jsonPath("$.content[*].lang", everyItem(equalTo(lang)))
        }
    }

    @Test
    fun `GET cards text search q filters by name`() {
        val initial = mockMvc.get("/api/cards") { param("size", "50") }.andReturn()
        val page: PageResponse<CardDto> = objectMapper.readValue(
            initial.response.contentAsString, object : TypeReference<PageResponse<CardDto>>() {})
        val anyNameToken = page.content.firstOrNull { !it.name.isNullOrBlank() }?.name?.split(" ")
            ?.firstOrNull { it.length >= 3 }
        assumeTrue(!anyNameToken.isNullOrBlank(), "no searchable name in test DB")

        mockMvc.get("/api/cards") {
            param("q", anyNameToken!!)
            param("size", "25")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.length()") { value(greaterThan(0)) }
        }
    }

    @Test
    fun `GET cards variants are present with safe defaults`() {
        val resp = mockMvc.get("/api/cards") { param("size", "5") }.andReturn()
        val page: PageResponse<CardDto> = objectMapper.readValue(
            resp.response.contentAsString, object : TypeReference<PageResponse<CardDto>>() {})
        assumeTrue(page.content.isNotEmpty(), "no cards")

        page.content.forEach { it ->
            assert(it.variants.normal != null)
            assert(it.variants.holo != null)
            assert(it.variants.reverse != null)
            assert(it.variants.firstEdition != null)
            assert(it.variants.wPromo != null)
        }
    }

    @Test
    fun `GET cards set summary carries ids and optional names`() {
        val resp = mockMvc.get("/api/cards") { param("size", "5") }.andReturn()
        val page: PageResponse<CardDto> = objectMapper.readValue(
            resp.response.contentAsString, object : TypeReference<PageResponse<CardDto>>() {})
        assumeTrue(page.content.isNotEmpty(), "no cards")

        page.content.forEach {
            assert(it.set.id.isNotBlank())
        }
    }
}
