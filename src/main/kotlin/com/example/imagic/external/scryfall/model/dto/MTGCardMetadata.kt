package com.example.imagic.external.scryfall.model.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class MTGCardMetadata(
    val id: String,
    val name: String,
    val imageUris: Map<String, String>,
)
