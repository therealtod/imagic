package com.example.imagic.controller

import com.example.imagic.model.dto.api.CardProcessingSubmissionResponse
import com.example.imagic.model.OperationId
import com.example.imagic.model.dto.api.CardProcessingRequest
import com.example.imagic.model.dto.api.OperationStatusResponse
import com.example.imagic.service.MTGCardsService
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/cards")
@Validated
class MTGCardsController(
    private val mtgCardsService: MTGCardsService,
) {
    @PostMapping("/process")
    suspend fun submitCardsForProcessing(
        @Valid
        @RequestBody
        cardProcessingRequest: CardProcessingRequest
    ): CardProcessingSubmissionResponse {
        val operationId = mtgCardsService.processCardNames(cardProcessingRequest.cardNames)
        return CardProcessingSubmissionResponse(
            operationId = operationId
        )
    }

    @GetMapping("/{operationId}")
    fun getOperationStatus(
        @PathVariable
        operationId: OperationId,
    ): OperationStatusResponse {
        return mtgCardsService.getOperationStatus(operationId)
    }
}
