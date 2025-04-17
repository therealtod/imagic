package com.example.imagic.service

import com.example.imagic.Common
import com.example.imagic.external.scryfall.ScryfallConnectionException
import com.example.imagic.external.scryfall.client.ScryfallClient
import com.example.imagic.external.scryfall.model.dto.FailureData
import com.example.imagic.model.ItemProcessingStatus
import com.example.imagic.model.OperationId
import com.example.imagic.model.OperationStatus
import com.example.imagic.model.dto.api.ProcessedMTGCardData
import com.example.imagic.model.dto.api.OperationStatusResponse
import com.example.imagic.model.dto.db.MTGCardsOperation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.net.URI

@Service
class MTGCardsService(
    private val scryfallClient: ScryfallClient,
    private val dataPersistenceService: DataPersistenceService,
) {
    /**
     * Starts the processing of all the cards with names included in the given [cardNames] list
     *
     * @param cardNames The list of card names submitted for processing
     */
    suspend fun processCardNames(cardNames: List<String>): OperationId {
        val operationId = Common.generateRandomUniqueID()
        CoroutineScope(Dispatchers.IO).launch {
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
        val operationRecord = dataPersistenceService.getOperationData(operationId)
        val cardGroups = operationRecord.requestedCards.groupBy { it.pngURI != null }
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
        val status = determineOperationStatus(operationRecord)
        return OperationStatusResponse(
            operationId = operationId,
            status = status,
            results = results,
            failures = failures,
        )
    }

    suspend fun processBatch(operationDBRecord: MTGCardsOperation, cardNames: List<String>) {
        try {
            val metadataMono = scryfallClient.fetchCardsData(cardNames)
            metadataMono.subscribe {
                dataPersistenceService.storeCardsData(
                    operation = operationDBRecord,
                    mtgCardsCollection = it,
                )
            }
        } catch (ex: ScryfallConnectionException) {
            dataPersistenceService.writeFailedBatchData(operation = operationDBRecord, cardNames = cardNames)
        }


    }

    private fun determineOperationStatus(operationRecord: MTGCardsOperation): OperationStatus {
        return if (operationRecord.requestedCards.any { it.operationStatus == ItemProcessingStatus.PENDING })
            OperationStatus.PROCESSING
        else if (operationRecord.requestedCards.all { it.operationStatus == ItemProcessingStatus.FOUND })
            OperationStatus.SUCCESS
        else if (operationRecord.requestedCards.any { it.operationStatus == ItemProcessingStatus.FOUND })
            OperationStatus.PARTIAL_SUCCESS
        else OperationStatus.FAILURE
    }
}
