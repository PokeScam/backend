package de.pokescan.api.repository
import de.pokescan.api.model.CardSet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CardSetRepository : JpaRepository<CardSet, String> {
    @Query("""
        select s from CardSet s
        where (:seriesId is null or s.series.seriesId = :seriesId)
          and (:lang is null or s.lang = :lang)
    """)
    fun search(seriesId: String?, lang: String?, pageable: Pageable): Page<CardSet>
}
