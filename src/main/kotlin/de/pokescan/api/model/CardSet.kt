package de.pokescan.api.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity @Table(name = "card_set")
class CardSet(
    @Id
    val id: String,
    @Column(nullable = false)
    val name: String,
    @Column(name = "release_date")
    val releaseDate: LocalDate?,
    @ManyToOne(optional = false) @JoinColumn(name = "series_fk")
    val series: Series,
    val abbreviation: String?,
    @Column(nullable = false, length = 2)
    val lang: String,
    @Column(name = "count_total") val countTotal: Int = 0,
    @Column(name = "count_official") val countOfficial: Int = 0,
    @Column(name = "count_first_ed") val countFirstEd: Int = 0,
    @Column(name = "count_holo") val countHolo: Int = 0,
    @Column(name = "count_normal") val countNormal: Int = 0,
    @Column(name = "count_reverse") val countReverse: Int = 0
)
