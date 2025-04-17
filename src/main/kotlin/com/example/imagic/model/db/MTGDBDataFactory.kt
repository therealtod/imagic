package com.example.imagic.model.db

import com.example.imagic.external.scryfall.model.dto.MTGCardMetadata
import com.example.imagic.model.ItemProcessingStatus

object MTGDBDataFactory {
    fun createInitialCardOperationDBData(
        operation: MTGCardsOperationTableRow,
        cardName: String
    ): RequestedMTGCardTableRow {
        return RequestedMTGCardTableRow(
            cardName = cardName,
            operation = operation,
            operationStatus = ItemProcessingStatus.PENDING,
        )
    }

    fun createCardOperationDBData(
        operation: MTGCardsOperationTableRow,
        scryfallData: MTGCardMetadata,
    ): RequestedMTGCardTableRow {
        val pngUri = scryfallData.imageUris["png"]
        return RequestedMTGCardTableRow(
            cardName = scryfallData.name,
            operation = operation,
            operationStatus = if (pngUri != null) ItemProcessingStatus.FOUND else ItemProcessingStatus.NO_PNG_IMAGE,
            pngURI = pngUri,
        )
    }

    fun createNotFoundCardDBData(
        operation: MTGCardsOperationTableRow,
        cardName: String,
    ): RequestedMTGCardTableRow {
        return RequestedMTGCardTableRow(
            cardName = cardName,
            operation = operation,
            operationStatus = ItemProcessingStatus.NOT_FOUND,
            pngURI = null,
        )
    }

    fun createFailedScryfallApiRequestData(
        operation: MTGCardsOperationTableRow,
        cardName: String,
    ): RequestedMTGCardTableRow {
        return RequestedMTGCardTableRow(
            cardName = cardName,
            operation = operation,
            operationStatus = ItemProcessingStatus.NETWORK_ERROR,
        )
    }
}
