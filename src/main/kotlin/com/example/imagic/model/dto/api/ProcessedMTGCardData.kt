package com.example.imagic.model.dto.api

import java.net.URI

data class ProcessedMTGCardData(
    val cardName: String,
    val pngUri: URI,
)
