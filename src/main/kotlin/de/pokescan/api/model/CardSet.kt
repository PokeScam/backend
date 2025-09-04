package de.pokescan.api.model

import jakarta.persistence.*

@Entity
@Table(name = "sets")
class CardSet(
    @Id
    @Column(name = "id")
    val id: String,

    @Column(name = "name")
    val name: String? = null,

    @Column(name = "release_date")
    val releaseDate: String? = null,

    @Column(name = "series_id")
    val seriesId: String? = null,

    // Beziehung ist optional → safe calls im Code nötig
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", insertable = false, updatable = false)
    val series: Series? = null
)
