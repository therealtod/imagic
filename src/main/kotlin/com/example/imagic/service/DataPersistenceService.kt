package com.example.imagic.service

import com.example.imagic.exception.OperationNotFoundException
import com.example.imagic.external.scryfall.model.dto.MTGCardsCollection
import com.example.imagic.model.MTGCardsOperation
import com.example.imagic.model.OperationId
import com.example.imagic.model.db.MTGCardsOperationTableRow
import com.example.imagic.model.db.MTGDBDataFactory
import com.example.imagic.repository.MTGCardsOperationRepository
import com.example.imagic.repository.RequestedMTGCardRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DataPersistenceService(
    private val operationRepository: MTGCardsOperationRepository,
    private val requestedMTGCardRepository: RequestedMTGCardRepository,
) {
    @Transactional
    suspend fun storeOperationInitialData(
        operationId: OperationId,
        cardNames: List<String>
    ): MTGCardsOperationTableRow {
        val operationDBRecord = MTGCardsOperationTableRow(
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
    suspend fun storeCardsData(operation: MTGCardsOperationTableRow, mtgCardsCollection: MTGCardsCollection) {
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
    suspend fun writeFailedBatchData(
        operation: MTGCardsOperationTableRow,
        cardNames: List<String>
    ): MTGCardsOperationTableRow {
        val cardsDBRecords = cardNames.map {
            MTGDBDataFactory.createFailedScryfallApiRequestData(operation, cardName = it)
        }
        requestedMTGCardRepository.saveAll(cardsDBRecords)

        return operationRepository.getReferenceById(operation.operationId)
    }

    @Transactional(readOnly = true)
    fun getOperationData(operationId: OperationId): MTGCardsOperation {
        return operationRepository.findByIdWithCards(operationId)
            ?.let { MTGCardsOperation(it) }
            ?: throw OperationNotFoundException("Could not find a operation with id $operationId in the database")
    }
}
