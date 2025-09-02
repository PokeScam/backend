package de.pokescan.api.dto

data class CardDto(
    val id: String,
    val setId: String,
    val localId: Int,
    val lang: String,
    val cardId: String,
    val name: String,
    val illustrator: String?,
    val rarity: String?,
    val category: Int
)
