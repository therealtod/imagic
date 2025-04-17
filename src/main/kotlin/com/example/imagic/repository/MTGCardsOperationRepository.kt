package com.example.imagic.repository

import com.example.imagic.model.OperationId
import com.example.imagic.model.db.MTGCardsOperationTableRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MTGCardsOperationRepository : JpaRepository<MTGCardsOperationTableRow, OperationId> {
    @Query(
        """
        SELECT o FROM MTGCardsOperationTableRow o 
        LEFT JOIN FETCH o.requestedCards 
        WHERE o.operationId = :operationId
    """
    )
    fun findByIdWithCards(operationId: OperationId): MTGCardsOperationTableRow?
}
