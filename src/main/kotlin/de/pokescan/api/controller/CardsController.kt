package de.pokescan.api.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.pokescan.api.dto.*
import de.pokescan.api.repository.CardRepository
import jakarta.validation.constraints.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api")
class CardsController(
    private val cards: CardRepository
) {
    private val mapper = jacksonObjectMapper()

    @GetMapping("/cards")
    fun listCards(
        // Set mit Sprache (z. B. xy7-en). Wenn gesetzt, hat Priorität gegenüber setId+lang.
        @RequestParam(required = false, name = "set")
        @Pattern(regexp = "^[A-Za-z0-9_-]{0,20}$") setLocId: String?,

        // Raw-Set-ID (z. B. xy7)
        @RequestParam(required = false, name = "setId")
        @Pattern(regexp = "^[A-Za-z0-9_-]{0,20}$") setId: String?,

        // Sprache (en/de/ja …)
        @RequestParam(required = false, name = "lang")
        @Pattern(regexp = "^[A-Za-z]{2,5}$") lang: String?,

        @RequestParam(required = false, name = "rarity")
        @Pattern(regexp = "^[A-Za-z0-9 \\-/]{0,30}$") rarity: String?,

        // frei suchbar im Karten-Namen
        @RequestParam(required = false, name = "q")
        @Pattern(regexp = "^[\\p{L}0-9 \\-_'!?.]{0,50}$") query: String?,

        // Series-Name oder -ID als Zusatzfilter
        @RequestParam(required = false, name = "series")
        @Pattern(regexp = "^[\\p{L}0-9 \\-_'!?.]{0,50}$") series: String?,
        @RequestParam(required = false, name = "seriesId")
        @Pattern(regexp = "^[A-Za-z0-9_-]{0,20}$") seriesId: String?,

        @RequestParam(defaultValue = "0") @PositiveOrZero page: Int,
        @RequestParam(defaultValue = "50") @Min(1) @Max(200) size: Int
    ): Page<CardResponse> {

        val noFilters =
            setLocId == null &&
                    setId == null &&
                    lang == null &&
                    rarity == null &&
                    query == null &&
                    series == null &&
                    seriesId == null

        val pageable = PageRequest.of(page, size)

        val pageCards = if (noFilters) {

            cards.searchWithDefaultOrder(
                setLocId = null,
                setId = null,
                lang = null,
                rarity = null,
                q = null,
                series = null,
                seriesId = null,
                pageable = pageable
            )
        } else {
            cards.search(
                setLocId = setLocId,
                setId = setId,
                lang = lang,
                rarity = rarity,
                q = query,
                series = series,
                seriesId = seriesId,
                pageable = pageable
            )
        }

        return pageCards.map { c ->
            val resolvedSetId = c.setId ?: c.cardId.substringBefore("-", "")
            val localizedId = c.setLocId ?: run {
                val l = c.language?.lowercase().orEmpty().ifBlank { "xx" }
                if (resolvedSetId.isNotBlank()) "$resolvedSetId-$l" else null
            }

            val setNode = c.setInfoJson?.let { runCatching { mapper.readTree(it) }.getOrNull() }
            val setName = setNode?.get("name")?.asText()
            val cardCountNode = setNode?.get("cardCount")
            val counts = cardCountNode?.let {
                CardCount(
                    official = it.get("official")?.asInt(),
                    total = it.get("total")?.asInt()
                )
            }

            val variantsNode = c.variantsJson?.let { runCatching { mapper.readTree(it) }.getOrNull() }
            val variants = Variants(
                normal = variantsNode?.get("normal")?.asBoolean() ?: false,
                holo = variantsNode?.get("holo")?.asBoolean() ?: false,
                reverse = variantsNode?.get("reverse")?.asBoolean() ?: false,
                firstEdition = variantsNode?.get("firstEdition")?.asBoolean() ?: false,
                wPromo = variantsNode?.get("wPromo")?.asBoolean() ?: false
            )

            CardResponse(
                id = c.cardId,
                name = c.name,
                rarity = c.rarity,
                localId = c.localId ?: 0,
                lang = c.language.orEmpty(),
                illustrator = c.illustrator,
                category = c.category ?: 0,
                type = c.type,
                dexId = c.dexId,
                set = SetSummary(
                    id = resolvedSetId,
                    name = setName,
                    releaseDate = null,
                    cardCount = counts,
                    seriesId = null,
                    seriesName = null,
                    localizedId = localizedId
                ),
                variants = variants
            )
        }
    }
}
