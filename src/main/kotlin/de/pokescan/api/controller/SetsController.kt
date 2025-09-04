package de.pokescan.api.controller

import de.pokescan.api.dto.SetResponse
import de.pokescan.api.repository.CardSetRepository
import jakarta.validation.constraints.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api")
class SetsController(
    private val sets: CardSetRepository
) {

    @GetMapping("/sets")
    fun listSets(
        @RequestParam(required = false, name = "seriesId")
        @Pattern(regexp = "^[A-Za-z0-9_-]{0,20}$") seriesId: String?,
        @RequestParam(required = false, name = "series")
        @Pattern(regexp = "^[\\p{L}0-9 \\-_'!?.]{0,50}$") series: String?,
        @RequestParam(required = false, name = "q")
        @Pattern(regexp = "^[\\p{L}0-9 \\-_'!?.]{0,50}$") q: String?,
        @RequestParam(defaultValue = "0") @PositiveOrZero page: Int,
        @RequestParam(defaultValue = "50") @Min(1) @Max(200) size: Int
    ): Page<SetResponse> {

        val noFilters = seriesId == null && series == null && q == null
        val pageable = PageRequest.of(page, size)

        val pageSets = if (noFilters) {
            sets.searchSetsWithDefaultOrder(
                seriesId = null,
                series = null,
                q = null,
                pageable = pageable
            )
        } else {
            sets.searchSets(seriesId, series, q, pageable)
        }

        return pageSets.map { cs ->
            SetResponse(
                id = cs.id,
                name = cs.name,
                releaseDate = cs.releaseDate,
                seriesId = cs.series?.seriesId,
                seriesName = cs.series?.name
            )
        }
    }
}
