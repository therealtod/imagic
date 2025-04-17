package com.example.imagic.service

import com.example.imagic.external.scryfall.model.dto.MTGCardsCollection
import com.example.imagic.model.OperationId
import com.example.imagic.model.dto.db.MTGCardsOperation
import com.example.imagic.model.dto.db.MTGDBDataFactory
import com.example.imagic.repository.MTGCardsOperationRepository
import com.example.imagic.repository.RequestedMTGCardRepository
import jakarta.transaction.Transactional
import kotlinx.coroutines.delay
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class DataPersistenceService(
    private val operationRepository: MTGCardsOperationRepository,
    private val requestedMTGCardRepository: RequestedMTGCardRepository,
) {
    @Transactional
    suspend fun storeOperationInitialData(operationId: OperationId, cardNames: List<String>): MTGCardsOperation {
        val operationDBRecord = MTGCardsOperation(
            operationId = operationId,
        )

        val cardsDBRecords = cardNames.map {
            MTGDBDataFactory.createInitialCardOperationDBData(
                operation = operationDBRecord,
                cardName = it,
            )
        }
        operationDBRecord.requestedCards.addAll(cardsDBRecords)
        operationRepository.save(operationDBRecord)

        return operationDBRecord
    }

    @Transactional
    fun storeCardsData(operation: MTGCardsOperation, mtgCardsCollection: MTGCardsCollection) {
        val successfullyRetrievedCardsRecords = mtgCardsCollection
            .data
            .map {
                MTGDBDataFactory.createCardOperationDBData(
                    operation = operation,
                    scryfallData = it,
                )
            }
        val cardsNotFoundRecords = mtgCardsCollection
            .notFound
            .map {
                MTGDBDataFactory.createNotFoundCardDBData(
                    operation = operation,
                    cardName = it.name,
                )
            }
        requestedMTGCardRepository.saveAll(successfullyRetrievedCardsRecords + cardsNotFoundRecords)
    }

    @Transactional
    fun writeFailedBatchData(operation: MTGCardsOperation, cardNames: List<String>): MTGCardsOperation {
        val cardsDBRecords = cardNames.map {
            MTGDBDataFactory.createFailedScryfallApiRequestData(operation, cardName = it)
        }
        requestedMTGCardRepository.saveAll(cardsDBRecords)

        return operationRepository.getReferenceById(operation.operationId)
    }

    fun getOperationData(operationId: OperationId): MTGCardsOperation {
        return operationRepository.getReferenceById(operationId)
        // TODO take care of non existing IDS
    }
}
