package de.pokescan.api.repository

import de.pokescan.api.model.CardSet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CardSetRepository : JpaRepository<CardSet, String> {

    /**
     * Standard-Suche (für den Fall mit Filtern).
     * (deine bisherige Sortierung)
     */
    @Query(
        """
        select cs
        from CardSet cs
        left join cs.series s
        where (:seriesId is null or cs.seriesId = :seriesId)
          and (:series   is null or lower(coalesce(s.name,'')) like lower(concat('%', :series, '%')))
          and (:q        is null or lower(coalesce(cs.name,'')) like lower(concat('%', :q, '%')))
        order by
          coalesce(s.releaseYear, 999999) asc,
          case when cs.releaseDate is null then 1 else 0 end asc,
          cs.releaseDate asc,
          cs.id asc
        """
    )
    fun searchSets(
        @Param("seriesId") seriesId: String?,
        @Param("series") series: String?,
        @Param("q") q: String?,
        pageable: Pageable
    ): Page<CardSet>

    /**
     * Default-Sortierung (wenn KEINE Filter gesetzt sind):
     *  - Sets nach Release-Datum: neueste zuerst, Nulls nach hinten
     */
    @Query(
        """
        select cs
        from CardSet cs
        left join cs.series s
        where (:seriesId is null or cs.seriesId = :seriesId)
          and (:series   is null or lower(coalesce(s.name,'')) like lower(concat('%', :series, '%')))
          and (:q        is null or lower(coalesce(cs.name,'')) like lower(concat('%', :q, '%')))
        order by
          cs.releaseDate desc nulls last,
          cs.id asc
        """
    )
    fun searchSetsWithDefaultOrder(
        @Param("seriesId") seriesId: String?,
        @Param("series") series: String?,
        @Param("q") q: String?,
        pageable: Pageable
    ): Page<CardSet>
}
