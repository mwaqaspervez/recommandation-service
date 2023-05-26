package com.epam.recommendation.service.Imp;

import com.epam.recommendation.model.Crypto;
import com.epam.recommendation.model.CryptoStats;
import com.epam.recommendation.service.CryptoDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class CryptoServiceImpTest {

    @Mock
    private CryptoDataSource cryptoDataSource;

    private CryptoServiceImp cryptoService;


    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        cryptoService = new CryptoServiceImp(cryptoDataSource);
    }

    @Test
    public void testGetNormalizedStatsForDate_withMaxNormalizedRange() {
        List<Crypto> btcData = new ArrayList<>();
        long now = Instant.now().toEpochMilli();
        btcData.add(new Crypto(now, "BTC", BigDecimal.valueOf(10000)));
        btcData.add(new Crypto(now, "BTC", BigDecimal.valueOf(12000)));
        btcData.add(new Crypto(now, "BTC", BigDecimal.valueOf(8000)));
        when(cryptoDataSource.get("BTC")).thenReturn(btcData);

        List<Crypto> ethData = new ArrayList<>();
        ethData.add(new Crypto(now, "ETH", BigDecimal.valueOf(2100)));
        ethData.add(new Crypto(now, "ETH", BigDecimal.valueOf(2400)));
        ethData.add(new Crypto(now, "ETH", BigDecimal.valueOf(1200)));
        when(cryptoDataSource.get("ETH")).thenReturn(ethData);
        when(cryptoDataSource.keys()).thenReturn(List.of("BTC", "ETH"));

        LocalDate date = LocalDate.now();
        assertEquals("ETH", cryptoService.getNormalizedStatsForDate(date));
    }

    @Test
    public void testGetNormalizedStatsForDate_withNoCryptoData() {
        String crypto1 = "BTC";
        LocalDate date = LocalDate.of(2022, 1, 1);
        when(cryptoDataSource.get(crypto1)).thenReturn(null);

        String result = cryptoService.getNormalizedStatsForDate(date);
        assertNull(result);
    }

    @Test
    public void testGetNormalizedStatsForDate_withEmptyCryptoDataList() {
        String crypto1 = "BTC";
        LocalDate date = LocalDate.of(2022, 1, 1);
        List<Crypto> cryptoList = List.of();
        when(cryptoDataSource.get(crypto1)).thenReturn(cryptoList);

        String result = cryptoService.getNormalizedStatsForDate(date);
        assertNull(result);
    }


    @Test
    public void testGetCryptosReturnsEmptyListWhenDataSourceIsEmpty() {
        when(cryptoDataSource.keys()).thenReturn(List.of("BTC", "ETH"));
        List<CryptoStats> result = cryptoService.getCryptos();
        // Then an empty list should be returned
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCryptosReturnsCorrectStatsForSingleCrypto() {
        // Given a cryptoDataSource with a single crypto type and data
        List<Crypto> cryptoData = new ArrayList<>();
        cryptoData.add(new Crypto(Instant.parse("2023-01-01T00:00:00Z").toEpochMilli(), "BTC", new BigDecimal("100")));
        cryptoData.add(new Crypto(Instant.parse("2023-01-02T00:00:00Z").toEpochMilli(), "BTC", new BigDecimal("110")));
        cryptoData.add(new Crypto(Instant.parse("2023-01-03T00:00:00Z").toEpochMilli(), "BTC", new BigDecimal("120")));
        when(cryptoDataSource.get("BTC")).thenReturn(cryptoData);
        when(cryptoDataSource.keys()).thenReturn(List.of("BTC"));

        List<CryptoStats> result = cryptoService.getCryptos();

        // Then the result should contain a single CryptoStats object with the correct stats
        assertEquals(1, result.size());
        CryptoStats cryptoStats = result.get(0);
        assertEquals("BTC", cryptoStats.getType());
        assertEquals(new BigDecimal("100"), cryptoStats.getMin());
        assertEquals(new BigDecimal("120"), cryptoStats.getMax());
        assertEquals(new BigDecimal("100"), cryptoStats.getOldest());
        assertEquals(new BigDecimal("120"), cryptoStats.getNewest());
        assertEquals(new BigDecimal("0.2000"), cryptoStats.getNormalized());
    }

    @Test
    public void testGetCryptosReturnsCorrectStatsForMultipleCryptos() {
        // Given a cryptoDataSource with multiple crypto types and data
        List<Crypto> btcData = new ArrayList<>();
        btcData.add(new Crypto(Instant.parse("2023-01-01T00:00:00Z").toEpochMilli(), "BTC", new BigDecimal("100")));
        btcData.add(new Crypto(Instant.parse("2023-01-02T00:00:00Z").toEpochMilli(), "BTC", new BigDecimal("110")));
        btcData.add(new Crypto(Instant.parse("2023-01-03T00:00:00Z").toEpochMilli(), "BTC", new BigDecimal("120")));
        when(cryptoDataSource.get("BTC")).thenReturn(btcData);

        List<Crypto> ethData = new ArrayList<>();
        ethData.add(new Crypto(Instant.parse("2023-01-01T00:00:00Z").toEpochMilli(), "ETH", new BigDecimal("40")));
        ethData.add(new Crypto(Instant.parse("2023-01-02T00:00:00Z").toEpochMilli(), "ETH", new BigDecimal("55")));
        ethData.add(new Crypto(Instant.parse("2023-01-03T00:00:00Z").toEpochMilli(), "ETH", new BigDecimal("60")));
        when(cryptoDataSource.get("ETH")).thenReturn(ethData);
        when(cryptoDataSource.keys()).thenReturn(List.of("BTC","ETH"));

        List<CryptoStats> list = cryptoService.getCryptos();

        assertNotNull(list);
        assertTrue(list.size() > 0);
        assertEquals(list.get(0).getType(), "BTC");
        assertEquals(list.get(0).getMin().intValue(), 100);
    }

    @Test
    public void testGetCrypto_withValidCryptoType_returnsCorrectCryptoStats() {
        List<Crypto> cryptoDataList = Arrays.asList(
                new Crypto(Instant.now().minus(Duration.ofDays(7)).toEpochMilli(), "BTC", new BigDecimal("50000")),
                new Crypto(Instant.now().minus(Duration.ofDays(6)).toEpochMilli(), "BTC", new BigDecimal("52000")),
                new Crypto(Instant.now().minus(Duration.ofDays(5)).toEpochMilli(), "BTC", new BigDecimal("53000")),
                new Crypto(Instant.now().minus(Duration.ofDays(4)).toEpochMilli(), "BTC", new BigDecimal("55000")),
                new Crypto(Instant.now().minus(Duration.ofDays(3)).toEpochMilli(), "BTC", new BigDecimal("54000"))
        );
        when(cryptoDataSource.get("BTC")).thenReturn(cryptoDataList);

        CryptoStats result = cryptoService.getCrypto("BTC");

        assertNotNull(result);
        assertEquals("BTC", result.getType());
        assertEquals(new BigDecimal("50000"), result.getMin());
        assertEquals(new BigDecimal("55000"), result.getMax());
        assertEquals(new BigDecimal("50000"), result.getOldest());
        assertEquals(new BigDecimal("54000"), result.getNewest());
        assertEquals(new BigDecimal("0.1000"), result.getNormalized());
    }
}