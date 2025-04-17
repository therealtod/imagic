package com.example.imagic.service

import com.example.imagic.Common
import com.example.imagic.external.scryfall.ScryfallConnectionException
import com.example.imagic.external.scryfall.client.ScryfallClient
import com.example.imagic.external.scryfall.model.dto.FailureData
import com.example.imagic.model.ItemProcessingStatus
import com.example.imagic.model.MTGCardsOperation
import com.example.imagic.model.OperationId
import com.example.imagic.model.OperationStatus
import com.example.imagic.model.db.MTGCardsOperationTableRow
import com.example.imagic.model.dto.api.OperationStatusResponse
import com.example.imagic.model.dto.api.ProcessedMTGCardData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.net.URI

@Service
class MTGCardsService(
    private val scryfallClient: ScryfallClient,
    private val dataPersistenceService: DataPersistenceService,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * Starts the processing of all the cards with names included in the given [cardNames] list
     *
     * @param cardNames The list of card names submitted for processing
     */
    suspend fun processCardNames(cardNames: List<String>): OperationId {
        val operationId = Common.generateRandomUniqueID()
        coroutineScope.launch {
            val operationDBRecord = dataPersistenceService.storeOperationInitialData(
                operationId = operationId,
                cardNames = cardNames,
            )
            val batches = cardNames.chunked(75)
            batches.forEach { batch ->
                processBatch(operationDBRecord, batch)
            }
        }

        return operationId
    }

    fun getOperationStatus(operationId: OperationId): OperationStatusResponse {
        val operation = dataPersistenceService.getOperationData(operationId)
        val cardGroups = operation.cards.groupBy { it.pngURI != null }
        val results = cardGroups[true]
            .orEmpty()
            .map {
                ProcessedMTGCardData(
                    cardName = it.cardName,
                    pngUri = URI.create(it.pngURI),
                )
            }
        val failures = cardGroups[false]
            .orEmpty()
            .map {
                FailureData(
                    cardName = it.cardName,
                    error = it.operationStatus.message,
                )
            }
        val status = determineOperationStatus(operation)
        return OperationStatusResponse(
            operationId = operationId,
            status = status,
            results = results,
            failures = failures,
        )
    }

    suspend fun processBatch(operationDBRecord: MTGCardsOperationTableRow, cardNames: List<String>) {
        try {
            val metadataMono = scryfallClient.fetchCardsData(cardNames)
            metadataMono.subscribe {
                coroutineScope.launch {
                    dataPersistenceService.storeCardsData(
                        operation = operationDBRecord,
                        mtgCardsCollection = it,
                    )
                }
            }
        } catch (ex: ScryfallConnectionException) {
            dataPersistenceService.writeFailedBatchData(operation = operationDBRecord, cardNames = cardNames)
        }


    }

    private fun determineOperationStatus(operation: MTGCardsOperation): OperationStatus {
        return if (operation.cards.any { it.operationStatus == ItemProcessingStatus.PENDING })
            OperationStatus.PROCESSING
        else if (operation.cards.all { it.operationStatus == ItemProcessingStatus.FOUND })
            OperationStatus.SUCCESS
        else if (operation.cards.any { it.operationStatus == ItemProcessingStatus.FOUND })
            OperationStatus.PARTIAL_SUCCESS
        else OperationStatus.FAILURE
    }
}
