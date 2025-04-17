package com.example.imagic.model.dto.api

import jakarta.validation.constraints.Size

data class CardProcessingRequest(
    @field:Size(
        min = 1,
        max = 100,
        message = "The number of card names submitted for processing must be between 1 and 100"
    )
    val cardNames: List<String>,
)
