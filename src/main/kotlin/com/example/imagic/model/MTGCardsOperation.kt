package com.example.imagic.model

import com.example.imagic.RequestedMTGCard
import com.example.imagic.model.db.MTGCardsOperationTableRow

data class MTGCardsOperation(
    val operationId: OperationId,
    val cards: List<RequestedMTGCard>
) {
    constructor(operation: MTGCardsOperationTableRow) : this(
        operationId = operation.operationId,
        cards = operation.requestedCards.map { RequestedMTGCard(it) }
    )
}
