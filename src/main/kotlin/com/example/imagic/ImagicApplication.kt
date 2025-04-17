package com.example.imagic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
class ImagicApplication

fun main(args: Array<String>) {
    runApplication<ImagicApplication>(*args)
}
