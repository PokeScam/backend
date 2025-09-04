package de.pokescan.api.dto

data class SeriesResponse(
    val id: String,
    val name: String?,
    val releaseYear: Int?,
    val lang: String?
)
