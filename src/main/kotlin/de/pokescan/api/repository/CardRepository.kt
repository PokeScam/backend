package de.pokescan.api.repository

import de.pokescan.api.model.Card
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param

interface CardRepository : PagingAndSortingRepository<Card, String> {

    /**
     * Default-Sortierung (wenn KEINE Filter gesetzt sind):
     *  1) Sprache: en -> de -> ja -> andere
     *  2) Set-Release (neueste zuerst) über sets.release_date
     *  3) localId: numerisch aufsteigend
     *
     *  Filter:
     *   - series  -> sets.series_name LIKE %...%
     *   - seriesId-> sets.series_id = ...
     */
    @Query(
        value = """
        SELECT c.*
        FROM cards c
        LEFT JOIN sets s ON s.id = c.set_loc_id
        WHERE (:setLocId IS NULL OR c.set_loc_id = :setLocId)
          AND (:setId    IS NULL OR c.set_id     = :setId)
          AND (:lang     IS NULL OR lower(c.language) = lower(:lang))
          AND (:rarity   IS NULL OR c.rarity LIKE '%' || :rarity || '%')
          AND (:q        IS NULL OR c.name   LIKE '%' || :q      || '%')
          AND (:series   IS NULL OR s.series_name LIKE '%' || :series || '%')
          AND (:seriesId IS NULL OR s.series_id = :seriesId)
        ORDER BY
          CASE lower(c.language)
            WHEN 'en' THEN 0
            WHEN 'de' THEN 1
            WHEN 'ja' THEN 2
            ELSE 3
          END,
          COALESCE(s.release_date, '0000-00-00') DESC,
          CAST(c.local_id AS INTEGER) ASC
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM cards c
        LEFT JOIN sets s ON s.id = c.set_loc_id
        WHERE (:setLocId IS NULL OR c.set_loc_id = :setLocId)
          AND (:setId    IS NULL OR c.set_id     = :setId)
          AND (:lang     IS NULL OR lower(c.language) = lower(:lang))
          AND (:rarity   IS NULL OR c.rarity LIKE '%' || :rarity || '%')
          AND (:q        IS NULL OR c.name   LIKE '%' || :q      || '%')
          AND (:series   IS NULL OR s.series_name LIKE '%' || :series || '%')
          AND (:seriesId IS NULL OR s.series_id = :seriesId)
        """,
        nativeQuery = true
    )
    fun searchWithDefaultOrder(
        @Param("setLocId") setLocId: String?,
        @Param("setId") setId: String?,
        @Param("lang") lang: String?,
        @Param("rarity") rarity: String?,
        @Param("q") q: String?,
        @Param("series") series: String?,
        @Param("seriesId") seriesId: String?,
        pageable: Pageable
    ): Page<Card>

    /**
     * Variante OHNE ORDER BY (für den Fall mit Filtern, aber mit denselben WHERE-Bedingungen).
     */
    @Query(
        value = """
        SELECT c.*
        FROM cards c
        LEFT JOIN sets s ON s.id = c.set_loc_id
        WHERE (:setLocId IS NULL OR c.set_loc_id = :setLocId)
          AND (:setId    IS NULL OR c.set_id     = :setId)
          AND (:lang     IS NULL OR lower(c.language) = lower(:lang))
          AND (:rarity   IS NULL OR c.rarity LIKE '%' || :rarity || '%')
          AND (:q        IS NULL OR c.name   LIKE '%' || :q      || '%')
          AND (:series   IS NULL OR s.series_name LIKE '%' || :series || '%')
          AND (:seriesId IS NULL OR s.series_id = :seriesId)
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM cards c
        LEFT JOIN sets s ON s.id = c.set_loc_id
        WHERE (:setLocId IS NULL OR c.set_loc_id = :setLocId)
          AND (:setId    IS NULL OR c.set_id     = :setId)
          AND (:lang     IS NULL OR lower(c.language) = lower(:lang))
          AND (:rarity   IS NULL OR c.rarity LIKE '%' || :rarity || '%')
          AND (:q        IS NULL OR c.name   LIKE '%' || :q      || '%')
          AND (:series   IS NULL OR s.series_name LIKE '%' || :series || '%')
          AND (:seriesId IS NULL OR s.series_id = :seriesId)
        """,
        nativeQuery = true
    )
    fun search(
        @Param("setLocId") setLocId: String?,
        @Param("setId") setId: String?,
        @Param("lang") lang: String?,
        @Param("rarity") rarity: String?,
        @Param("q") q: String?,
        @Param("series") series: String?,
        @Param("seriesId") seriesId: String?,
        pageable: Pageable
    ): Page<Card>
}
