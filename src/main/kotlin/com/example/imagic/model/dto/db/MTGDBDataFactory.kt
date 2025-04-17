package com.example.imagic.model.dto.db

import com.example.imagic.external.scryfall.model.dto.MTGCardMetadata
import com.example.imagic.model.ItemProcessingStatus

object MTGDBDataFactory {
    fun createInitialCardOperationDBData(operation: MTGCardsOperation, cardName: String): RequestedMTGCard  {
        return RequestedMTGCard(
            cardName = cardName,
            operation = operation,
            operationStatus = ItemProcessingStatus.PENDING,
        )
    }

    fun createCardOperationDBData(
        operation: MTGCardsOperation,
        scryfallData: MTGCardMetadata,
        ): RequestedMTGCard {
        val pngUri = scryfallData.imageUris["png"]
        return RequestedMTGCard(
            cardName = scryfallData.name,
            operation = operation,
            operationStatus = if (pngUri != null) ItemProcessingStatus.FOUND else ItemProcessingStatus.NO_PNG_IMAGE,
            pngURI = pngUri,
        )
    }

    fun createNotFoundCardDBData(
        operation: MTGCardsOperation,
        cardName: String,
    ): RequestedMTGCard {
        return RequestedMTGCard(
            cardName = cardName,
            operation = operation,
            operationStatus = ItemProcessingStatus.NOT_FOUND,
            pngURI = null,
        )
    }

    fun createFailedScryfallApiRequestData(
        operation: MTGCardsOperation,
        cardName: String,
    ): RequestedMTGCard {
        return RequestedMTGCard(
            cardName =cardName,
            operation = operation,
            operationStatus = ItemProcessingStatus.NETWORK_ERROR,
        )
    }
}
