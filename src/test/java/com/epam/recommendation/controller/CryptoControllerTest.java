package com.epam.recommendation.controller;

import com.epam.recommendation.model.CryptoStats;
import com.epam.recommendation.model.HighestNormalizedResponse;
import com.epam.recommendation.service.Imp.CryptoServiceImp;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CryptoController.class)
public class CryptoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    public CryptoServiceImp cryptoService;

    @Test
    public void testGetCryptoStats() throws Exception {
        Mockito.when(cryptoService.getCryptos())
                .thenReturn(List.of(getMockedCryptoStats("BTC")));

        MvcResult mvcResult = mockMvc.perform(get("/api/cryptos"))
                .andExpect(status().isOk())
                .andReturn();

        List<CryptoStats> cryptoStats = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertNotNull(cryptoStats);
        assertFalse(cryptoStats.isEmpty());
    }

    @Test
    public void testGetCryptoStatsByCrypto() throws Exception {
        String crypto = "BTC";
        Mockito.when(this.cryptoService.getCrypto(anyString()))
                .thenReturn(getMockedCryptoStats(crypto));

        MvcResult mvcResult = mockMvc.perform(get("/api/cryptos/" + crypto))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        CryptoStats cryptoStats = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CryptoStats.class
        );

        assertNotNull(cryptoStats);
    }

    @Test
    public void testGetCryptoStatsByInvalidCrypto() throws Exception {
        String crypto = "INVALID";
        mockMvc.perform(get("/api/cryptos/" + crypto))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetNormalizedValueForDate_withInvalidDate() throws Exception {
        LocalDate date = LocalDate.of(2015, 1, 1);
        MvcResult mvcResult = mockMvc.perform(get("/api/cryptos/normalized?date=" + date))
                .andExpect(status().isOk())
                .andReturn();

        HighestNormalizedResponse result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                HighestNormalizedResponse.class);

        assertNotNull(result);
        assertNull(result.getCurrency());
    }

    @Test
    public void testGetNormalizedValueForDate_withValidDate() throws Exception {
        String currency = "BTC";
        Mockito.when(this.cryptoService.getNormalizedStatsForDate(any()))
                .thenReturn(currency);
        LocalDate date = LocalDate.of(2022, 1, 1);
        MvcResult mvcResult = mockMvc.perform(get("/api/cryptos/normalized?date=" + date))
                .andExpect(status().isOk())
                .andReturn();

        HighestNormalizedResponse result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                HighestNormalizedResponse.class);

        assertNotNull(result);
        assertEquals(currency, result.getCurrency());
        assertFalse(result.getCurrency().isEmpty());
    }

    @Test
    public void testGetNormalizedValueForInvalidDate() throws Exception {
        String invalidDate = "invalid";
        mockMvc.perform(get("/api/cryptos/normalized?date" + invalidDate))
                .andExpect(status().isBadRequest());
    }

    private static CryptoStats getMockedCryptoStats(String crypto) {
        return new CryptoStats(crypto, BigDecimal.TEN,
                BigDecimal.valueOf(1000L), BigDecimal.ZERO,
                BigDecimal.TEN, BigDecimal.ONE);
    }
}