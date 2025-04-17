package com.example.imagic.external.scryfall.model.dto

data class MTGCardsCollectionRequest (
    val identifiers: List<CardNameIdentifierItem>,
) {
    data class CardNameIdentifierItem(
        val name: String,
    )
}
