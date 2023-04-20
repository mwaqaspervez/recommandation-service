package com.epam.recommendation.model;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoStats {

    private String type;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal oldest;
    private BigDecimal newest;
    private BigDecimal normalized;
}
