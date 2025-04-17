package com.example.imagic.controller

import com.example.imagic.Common
import com.example.imagic.model.dto.api.CardProcessingRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class MTGCardsControllerIT {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `Should not allow lists of cards bigger than 100 elements`() {
        val cardNames = (1..150).map {
            "card_name_$it"
        }
        val request = CardProcessingRequest(
            cardNames = cardNames,
        )

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/cards/process")
                .content(Common.objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
}