package com.example.imagic.model.dto.db

import com.example.imagic.model.ItemProcessingStatus
import jakarta.persistence.*

@Entity
@Table(name = "requested_mtg_card")
@IdClass(RequestedMTGCardId::class)
data class RequestedMTGCard(
    @Id
    val cardName: String,
    @Id
    @ManyToOne
    @JoinColumn(name = "operationId")
    val operation: MTGCardsOperation,
    val operationStatus: ItemProcessingStatus,
    val pngURI: String? = null,
)

data class RequestedMTGCardId(
    var cardName: String?,
    var operation: MTGCardsOperation?,
) {
    constructor(): this(null, null)
}
