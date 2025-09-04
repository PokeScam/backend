package de.pokescan.api.model

import jakarta.persistence.*

@Entity
@Table(name = "cards")
class Card(
    @Id
    @Column(name = "card_id")
    val cardId: String,

    @Column(name = "set_id")
    val setId: String? = null,

    @Column(name = "set_loc_id")
    val setLocId: String? = null,

    @Column(name = "local_id")
    val localId: Int? = null,

    @Column(name = "language")
    val language: String? = null,

    @Column(name = "name")
    val name: String? = null,

    @Column(name = "illustrator")
    val illustrator: String? = null,

    @Column(name = "rarity")
    val rarity: String? = null,

    @Column(name = "category")
    val category: Int? = null,

    @Column(name = "type")
    val type: String? = null,

    @Column(name = "dex_id")
    val dexId: Int? = null,

    @Column(name = "variants_json")
    val variantsJson: String? = null,

    @Column(name = "set_info_json")
    val setInfoJson: String? = null
)
