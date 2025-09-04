package de.pokescan.api.dto

data class Variants(
    val normal: Boolean = false,
    val holo: Boolean = false,
    val reverse: Boolean = false,
    val firstEdition: Boolean = false,
    val wPromo: Boolean = false
)

data class CardCount(val official: Int?, val total: Int?)

data class SetSummary(
    val id: String,
    val name: String?,
    val releaseDate: String?,
    val cardCount: CardCount?,
    val seriesId: String?,
    val seriesName: String?,
    val localizedId: String?
)

data class CardResponse(
    val id: String,
    val name: String?,
    val rarity: String?,
    val localId: Int,
    val lang: String,
    val illustrator: String?,
    val category: Int,
    val type: String?,
    val dexId: Int?,
    val set: SetSummary,
    val variants: Variants
)
