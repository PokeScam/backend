package de.pokescan.api.dto

data class SetResponse(
    val id: String,
    val name: String?,
    val releaseDate: String?,
    val seriesId: String?,
    val seriesName: String?
)