package com.example.imagic.repository

import com.example.imagic.model.OperationId
import com.example.imagic.model.dto.db.MTGCardsOperation
import org.springframework.data.jpa.repository.JpaRepository

interface MTGCardsOperationRepository : JpaRepository <MTGCardsOperation, OperationId>
