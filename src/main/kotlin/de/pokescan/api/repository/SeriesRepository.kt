package de.pokescan.api.repository

import de.pokescan.api.model.Series
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SeriesRepository : JpaRepository<Series, String> {

    @Query(
        """
        select s
        from Series s
        where (:q is null or lower(coalesce(s.name,'')) like lower(concat('%', :q, '%')))
        order by coalesce(s.releaseYear, 999999) asc, s.seriesId asc
        """
    )
    fun search(@Param("q") q: String?, pageable: Pageable): Page<Series>
}
