package com.example.imagic.external.scryfall

import org.springframework.http.HttpStatusCode

class ScryfallConnectionException(message: String, statusCode: HttpStatusCode): RuntimeException(message)
