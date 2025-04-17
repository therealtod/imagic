package com.example.imagic.external.scryfall.model.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class MTGCardsCollection(
    val notFound: List<NotFoundItem>,
    val data: List<MTGCardMetadata>,
)

data class NotFoundItem(
    val name: String,
)
