package com.epam.recommendation.controller;

import com.epam.recommendation.exception.CryptoNotSupportedException;
import com.epam.recommendation.model.CryptoStats;
import com.epam.recommendation.model.HighestNormalizedResponse;
import com.epam.recommendation.service.CryptoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")

public class CryptoController {

    private final List<String> supportedCurrencies;
    private final CryptoService cryptoService;

    public CryptoController(@Value("${app.currencies}") List<String> supportedCurrencies,
                            CryptoService cryptoService) {
        this.supportedCurrencies = supportedCurrencies;
        this.cryptoService = cryptoService;
    }

    /**
     * Gets the cryptos present in the system
     * GET -> /api/cryptos
     *
     * @return list of cryptos and their information present in
     * the system.
     */
    @GetMapping("/cryptos")
    public ResponseEntity<List<CryptoStats>> getCryptoStats() {
        return ResponseEntity.ok().body(this.cryptoService.getCryptos());
    }

    /**
     * Gets the information about the given crypto
     * present in the system.
     * Throws an exception if the crypto does not exist in the system.
     *
     * @param crypto the name of the cryptocurrency.
     * @return the information about the crypto in the system.
     */
    @GetMapping("/cryptos/{crypto}")
    public ResponseEntity<CryptoStats> getCryptoStats(@PathVariable("crypto") String crypto) {

        if (!supportedCurrencies.contains(crypto.toUpperCase())) {
            throw new CryptoNotSupportedException();
        }

        return ResponseEntity.ok().body(this.cryptoService.getCrypto(crypto));
    }

    /**
     * Find the normalized value of a crypto provided by the
     * requested date.
     *
     * @param date The date for which the values are requested.
     * @return The name of the cryptocurrency with the highest normalized value.
     */
    @GetMapping("/cryptos/normalized")
    public ResponseEntity<HighestNormalizedResponse> getHighestNormalizedValueForDate(
            @RequestParam(value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ResponseEntity.ok()
                .body(new HighestNormalizedResponse(
                        this.cryptoService.getNormalizedStatsForDate(date)));
    }
}
