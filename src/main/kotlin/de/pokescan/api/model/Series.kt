package de.pokescan.api.model

import jakarta.persistence.*

@Entity
@Table(name = "series")
class Series(
    @Id
    @Column(name = "series_id")
    val seriesId: String,

    @Column(name = "name")
    val name: String? = null,

    @Column(name = "release_year")
    val releaseYear: Int? = null,

    // optional – nur falls vorhanden; sonst gern entfernen
    @Column(name = "lang")
    val lang: String? = null
)
