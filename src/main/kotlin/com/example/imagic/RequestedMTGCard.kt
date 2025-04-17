package com.example.imagic

import com.example.imagic.model.ItemProcessingStatus
import com.example.imagic.model.db.RequestedMTGCardTableRow

data class RequestedMTGCard(
    val cardName: String,
    val operationStatus: ItemProcessingStatus,
    val pngURI: String? = null,
) {
    constructor(card: RequestedMTGCardTableRow) : this(
        cardName = card.cardName,
        operationStatus = card.operationStatus,
        pngURI = card.pngURI,
    )
}
