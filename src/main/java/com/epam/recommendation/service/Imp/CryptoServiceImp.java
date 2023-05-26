package com.epam.recommendation.service.Imp;

import com.epam.recommendation.model.Crypto;
import com.epam.recommendation.model.CryptoStats;
import com.epam.recommendation.service.CryptoDataSource;
import com.epam.recommendation.service.CryptoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CryptoServiceImp implements CryptoService {

    private final CryptoDataSource cryptoDataSource;

    public CryptoServiceImp(CryptoDataSource cryptoDataSource) {
        this.cryptoDataSource = cryptoDataSource;
    }

    @Override
    public CryptoStats getCrypto(String cryptoType) {
        List<Crypto> cryptoDataList = cryptoDataSource.get(cryptoType);
        return this.getCryptoStats(cryptoType, cryptoDataList);
    }

    /**
     * Gets the crypto stats from the list of values
     * for each day.
     * Max - calculated as the maximum value in the list
     * Min - calculated as the minimum value in the list
     * Oldest - calculated by comparing the timestamp
     * Newest - calculated by comparing the timestamp
     * Normalized - calculated as (max - min) / min
     *
     * @param cryptoType     The type of cryptocurrency.
     * @param cryptoDataList The list of values from which to get the info
     * @return {@link CryptoStats} object containing the calculated value.
     */
    private CryptoStats getCryptoStats(String cryptoType, List<Crypto> cryptoDataList) {
        if (cryptoDataList == null) {
            return null;
        }

        BigDecimal min = cryptoDataList.stream().map(Crypto::getPrice)
                .min(Comparator.naturalOrder()).get();
        BigDecimal max = cryptoDataList.stream().map(Crypto::getPrice)
                .max(Comparator.naturalOrder()).get();
        BigDecimal oldest = cryptoDataList.stream()
                .min(Comparator.comparingLong(Crypto::getTimestamp))
                .get().getPrice();
        BigDecimal newest = cryptoDataList.stream()
                .max(Comparator.comparingLong(Crypto::getTimestamp))
                .get().getPrice();

        BigDecimal normalized = max.subtract(min).divide(min, 4, RoundingMode.HALF_UP);

        return new CryptoStats(cryptoType, min, max, oldest, newest, normalized);
    }


    /**
     * Gets the list of cryptos and their values
     * present in the system sorted by normalized value descending
     *
     * @return list of crypto values.
     */
    @Override
    public List<CryptoStats> getCryptos() {
        List<CryptoStats> cryptoStatsList = new ArrayList<>();
        for (String cryptoType : cryptoDataSource.keys()) {
            List<Crypto> cryptoDataList = cryptoDataSource.get(cryptoType);
            if (cryptoDataList != null && !cryptoDataList.isEmpty()) {
                CryptoStats cryptoStats = this.getCryptoStats(cryptoType, cryptoDataList);
                cryptoStatsList.add(cryptoStats);
            }
        }
        cryptoStatsList.sort(Comparator.comparing(CryptoStats::getNormalized));
        return cryptoStatsList;
    }

    /**
     * Gets the normalized stats for a given date
     * The date is first matched and then the normalized
     * value is calculated. if a new bigger value is found
     * that is then replaced by the currency.
     *
     * @param date The date for which to look for.
     * @return The name of the highest normalized currency.
     */
    @Override
    public String getNormalizedStatsForDate(LocalDate date) {
        String maxCrypto = null;
        double maxNormalizedRange = Double.MIN_NORMAL;

        for (String cryptoType : cryptoDataSource.keys()) {
            // filter out the values for the given date.
            List<Crypto> cryptoDataList = cryptoDataSource.get(cryptoType)
                    .stream()
                    .filter(c -> Instant.ofEpochMilli(c.getTimestamp())
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                            .isEqual(date))
                    .toList();

            if (!cryptoDataList.isEmpty()) {
                BigDecimal min = cryptoDataList.stream().map(Crypto::getPrice)
                        .min(Comparator.naturalOrder()).get();
                BigDecimal max = cryptoDataList.stream().map(Crypto::getPrice)
                        .max(Comparator.naturalOrder()).get();
                double normalizedRange = max.subtract(min).divide(min, 4,
                        RoundingMode.HALF_UP).doubleValue();

                // if the new range is greater replace it.
                if (normalizedRange > maxNormalizedRange) {
                    maxNormalizedRange = normalizedRange;
                    maxCrypto = cryptoType;
                }
            }
        }
        return maxCrypto;
    }
}
