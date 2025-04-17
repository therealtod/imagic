package com.example.imagic.model

enum class ItemProcessingStatus(val message: String) {
    NOT_FOUND("The card could not be retrieved via the Scryfall API"),
    NO_PNG_IMAGE("The card was retrieved on Scryfall but it was missing the PNG image URI"),
    FOUND("Card data successfully retrieved"),
    NETWORK_ERROR("The communication with the Scryfall API has failed"),
    PENDING("The operation is still running"); // Operation ongoing, inherit status from the parent operation
}
