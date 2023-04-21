package com.epam.recommendation.service.Imp;

import com.epam.recommendation.csv.CSVReader;
import com.epam.recommendation.model.Crypto;
import com.epam.recommendation.service.CryptoDataSource;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CryptoDataSourceImp implements CryptoDataSource {
    private final HashOperations<String, String, List<Crypto>> cache;
    private final List<String> currencies;
    private final CSVReader csvReader;
    private static final int TIMESTAMP_INDEX = 0;
    private static final int SYMBOL_INDEX = 1;
    private static final int PRICE_INDEX = 2;
    private static final String HASH_KEY = "Crypto";

    public CryptoDataSourceImp(@Value("${app.currencies}") List<String> currencies,
                               CSVReader csvReader, RedisTemplate<String, List<Crypto>> redisTemplate) {
        this.currencies = currencies;
        this.csvReader = csvReader;
        this.cache = redisTemplate.opsForHash();
    }

    /**
     * Reads and loads the data into the map
     * from the csv file present in the project.
     * @throws Exception in case if the system is not able to read
     * the csv files.
     */
    @PostConstruct
    public void loadData() throws Exception {
        for (String currency : currencies) {
            List<String[]> lines = this.csvReader.readFile(currency + "_values.csv");

            List<Crypto> cryptoList = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                String[] values = lines.get(i);
                cryptoList.add(new Crypto(Long.parseLong(values[TIMESTAMP_INDEX]),
                        values[SYMBOL_INDEX], BigDecimal.valueOf(Double.parseDouble(values[PRICE_INDEX]))));
            }
            cache.put(HASH_KEY, currency, cryptoList);
        }
    }

    /**
     * Gets the list of values against the provided crypto type.
     * @param cryptoType The key for which the values are required.
     * @return list of crypto object containing the crypto information.
     */
    @Override
    public List<Crypto> get(String cryptoType) {
        return cache.get(HASH_KEY, cryptoType);
    }

    /**
     * Returns a list of keys for the crypto
     * present in the system.
     * @return list of string keys for crypto.
     */
    @Override
    public List<String> keys() {
        return cache.keys(HASH_KEY).stream().toList();
    }
}
