package com.example.imagic.model.dto.db

import com.example.imagic.model.OperationId
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
data class MTGCardsOperation(
    @Id
    val operationId: OperationId,
    @OneToMany(mappedBy = "operation", cascade = [CascadeType.ALL], orphanRemoval = true)
    val requestedCards: MutableList<RequestedMTGCard> = mutableListOf(),
)
