package com.example.imagic.model

enum class OperationStatus {
    PROCESSING, // Still processing
    FAILURE, // No cards could be processed
    SUCCESS, // All cards processed successfully
    PARTIAL_SUCCESS; // Only a subset of the cards has been processed successfully
}
