package de.pokescan.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PokeScanApplication

fun main(args: Array<String>) {
    runApplication<PokeScanApplication>(*args)
}
