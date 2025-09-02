package de.pokescan.api.repository

import de.pokescan.api.dto.CardDto
import de.pokescan.api.model.Card
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CardRepository : JpaRepository<Card, String> {

    @Query("""
        select new de.pokescan.api.dto.CardDto(
            c.cardId,
            case
              when locate('-', c.cardId) > 0
                then substring(c.cardId, 1, locate('-', c.cardId) - 1)
              else ''
            end,
            coalesce(c.localId, 0),
            coalesce(c.lang, ''),
            c.cardId,
            coalesce(c.name, '(unknown)'),
            c.illustrator,
            c.rarity,
            coalesce(c.category, 0)
        )
        from Card c
        where (:setId is null or locate(concat(:setId, '-'), c.cardId) = 1)
          and (:rarity is null or c.rarity = :rarity)
          and (:q is null or lower(coalesce(c.name, '')) like lower(concat('%', :q, '%')))
        order by c.cardId
    """)
    fun search(
        @Param("setId") setId: String?,
        @Param("rarity") rarity: String?,
        @Param("q") q: String?,
        pageable: Pageable
    ): Page<CardDto>
}
