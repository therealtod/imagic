package com.example.imagic.model.dto.api

import com.example.imagic.external.scryfall.model.dto.FailureData
import com.example.imagic.model.OperationId
import com.example.imagic.model.OperationStatus

data class OperationStatusResponse(
    val operationId: OperationId,
    val status: OperationStatus,
    val results: List<ProcessedMTGCardData>,
    val failures: List<FailureData>,
)
