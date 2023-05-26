package com.epam.recommendation.service;

import com.epam.recommendation.model.CryptoStats;

import java.time.LocalDate;
import java.util.List;

public interface CryptoService {

    CryptoStats getCrypto(String cryptoType);

    List<CryptoStats> getCryptos();

    String getNormalizedStatsForDate(LocalDate date);
}
