package de.pokescan.api.repository
import de.pokescan.api.model.Series
import org.springframework.data.jpa.repository.JpaRepository

interface SeriesRepository : JpaRepository<Series, Long>
