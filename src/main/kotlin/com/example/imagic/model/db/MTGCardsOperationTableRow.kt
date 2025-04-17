package com.example.imagic.model.db

import com.example.imagic.model.OperationId
import jakarta.persistence.*

@Entity
@Table(name = "MTGCardOperation")
data class MTGCardsOperationTableRow(
    @Id
    val operationId: OperationId,
    @OneToMany(mappedBy = "operation", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val requestedCards: MutableList<RequestedMTGCardTableRow> = mutableListOf(),
)
