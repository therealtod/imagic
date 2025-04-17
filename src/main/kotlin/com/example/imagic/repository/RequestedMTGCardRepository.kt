package com.example.imagic.repository

import com.example.imagic.model.OperationId
import com.example.imagic.model.dto.db.RequestedMTGCard
import org.springframework.data.jpa.repository.JpaRepository

interface RequestedMTGCardRepository : JpaRepository<RequestedMTGCard, String>
