package com.epam.recommendation.service;

import com.epam.recommendation.model.Crypto;

import java.util.List;

public interface CryptoDataSource {

    List<Crypto> get(String cryptoType);

    List<String> keys();
}
