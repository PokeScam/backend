package de.pokescan.api.model

import jakarta.persistence.*

@Entity
@Table(name = "card")
class Card(

    @Id
    @Column(name = "cardID")
    val cardId: String,

    @Column(name = "localID")
    val localId: Int? = null,

    @Column(length = 2)
    val lang: String? = null,

    val name: String? = null,
    val illustrator: String? = null,
    val rarity: String? = null,
    val category: Int? = null,

    @Column(name = "type")
    val type: String? = null,
    @Column(name = "dexID")
    val dexId: String? = null,
    @Column(name = "variants")
    val variants: String? = null
) {
    @Transient
    fun setIdFromCardId(): String =
        (cardId).substringBefore('-', missingDelimiterValue = "")
}
