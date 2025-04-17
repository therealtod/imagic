package com.example.imagic.model.db

import com.example.imagic.model.ItemProcessingStatus
import jakarta.persistence.*

@Entity
@Table(name = "requested_mtg_card")
@IdClass(RequestedMTGCardTableId::class)
data class RequestedMTGCardTableRow(
    @Id
    val cardName: String,
    @Id
    @ManyToOne
    @JoinColumn(name = "operationId")
    val operation: MTGCardsOperationTableRow,
    val operationStatus: ItemProcessingStatus,
    val pngURI: String? = null,
)

data class RequestedMTGCardTableId(
    var cardName: String?,
    var operation: MTGCardsOperationTableRow?,
) {
    constructor() : this(null, null)
}
