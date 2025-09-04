package de.pokescan.api.controller

import de.pokescan.api.dto.SeriesResponse
import de.pokescan.api.repository.SeriesRepository
import jakarta.validation.constraints.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api")
class SeriesController(
    private val seriesRepo: SeriesRepository
) {

    @GetMapping("/series")
    fun listSeries(
        @RequestParam(required = false, name = "q")
        @Pattern(regexp = "^[\\p{L}0-9 \\-_'!?.]{0,50}$") q: String?,
        @RequestParam(defaultValue = "0") @PositiveOrZero page: Int,
        @RequestParam(defaultValue = "50") @Min(1) @Max(200) size: Int
    ): Page<SeriesResponse> =
        // falls search(q: String, pageable) erwartet wird:
        seriesRepo.search(q.orEmpty(), PageRequest.of(page, size))
            .map { s ->
                SeriesResponse(
                    id = s.seriesId,
                    name = s.name,               // jetzt nullable im DTO
                    releaseYear = s.releaseYear,
                    lang = s.lang
                )
            }
}
