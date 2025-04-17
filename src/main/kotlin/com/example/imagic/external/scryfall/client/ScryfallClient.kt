package com.example.imagic.external.scryfall.client

import com.example.imagic.external.scryfall.ScryfallConnectionException
import com.example.imagic.external.scryfall.model.dto.MTGCardMetadata
import com.example.imagic.external.scryfall.model.dto.MTGCardsCollection
import com.example.imagic.external.scryfall.model.dto.MTGCardsCollectionRequest
import io.netty.handler.timeout.TimeoutException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.onErrorResume
import reactor.util.retry.Retry
import java.time.Duration

@Component
class ScryfallClient(private val webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder
        .baseUrl("https://api.scryfall.com")
        .defaultHeaders {
            it.accept = listOf(MediaType.APPLICATION_JSON)
            it.contentType = MediaType.APPLICATION_JSON
            it[HttpHeaders.USER_AGENT] = "IMagicApp/0.0.1"
        }
        .build()

    fun fetchCardsData(cardNames: List<String>): Mono<MTGCardsCollection> {
        val body = MTGCardsCollectionRequest(
            identifiers = cardNames.map {
                MTGCardsCollectionRequest.CardNameIdentifierItem(name = it)
            }
        )

        return webClient
            .post()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/cards/collection")
                    .build()
            }
            .body(BodyInserters.fromValue(body))
            .retrieve()
            .onStatus({ status -> status.is4xxClientError || status.is5xxServerError }, { response ->
                response.bodyToMono<String>()
                    .defaultIfEmpty("No error details")
                    .flatMap { errorBody ->
                        Mono.error(
                            ScryfallConnectionException(
                                "API call failed with status ${response.statusCode()} and body: $errorBody",
                                response.statusCode()
                            )
                        )
                    }
            })
            .bodyToMono<MTGCardsCollection>()
            .timeout(Duration.ofSeconds(30))
            .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
            .onErrorResume { error ->
                when (error) {
                    is WebClientResponseException -> {
                        Mono.error(ScryfallConnectionException("API responded with error: ${error.statusCode}", error.statusCode))
                    }
                    is TimeoutException -> {
                        Mono.error(ScryfallConnectionException("API request timed out", HttpStatus.REQUEST_TIMEOUT))
                    }
                    else -> {
                        Mono.error(ScryfallConnectionException("Failed to call API: ${error.message}", HttpStatus.INTERNAL_SERVER_ERROR))
                    }
                }
            }
    }
}
