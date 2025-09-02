package de.pokescan.api.model

import jakarta.persistence.*

@Entity
@Table(name = "series")
class Series(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "series_id", unique = true, nullable = false)
    val seriesId: String,
    @Column(nullable = false)
    val name: String,
    @Column(name = "release_year")
    val releaseYear: Int?
)