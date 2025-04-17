package com.example.imagic

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*

object Common {
    val objectMapper = jacksonObjectMapper()

    fun generateRandomUniqueID(): String {
        return UUID.randomUUID().toString()
    }
}
