package de.pokescan.api.controller

import de.pokescan.api.dto.CardDto
import de.pokescan.api.repository.CardRepository
import de.pokescan.api.repository.CardSetRepository
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api")
class CardsController(
    private val sets: CardSetRepository,
    private val cards: CardRepository
) {
    @GetMapping("/cards")
    fun listCards(
        @RequestParam(required = false)
        @Pattern(regexp = "^[A-Za-z0-9_-]{0,20}$", message = "set must be alphanumeric/underscore/hyphen")
        set: String?,

        @RequestParam(required = false)
        @Pattern(regexp = "^[A-Za-z0-9 \\-/]{0,30}$", message = "rarity contains invalid characters")
        rarity: String?,

        @RequestParam(required = false, name = "q")
        @Pattern(regexp = "^[\\p{L}0-9 \\-_'!?.]{0,50}$", message = "q contains invalid characters")
        query: String?,

        @RequestParam(defaultValue = "0")
        @PositiveOrZero(message = "page must be >= 0")
        page: Int,

        @RequestParam(defaultValue = "50")
        @Min(value = 1, message = "size must be >= 1")
        @Max(value = 200, message = "size must be <= 200")
        size: Int
    ): Page<CardDto> =
        cards.search(set, rarity, query, PageRequest.of(page, size))
}
