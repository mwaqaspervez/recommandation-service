package com.epam.recommendation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Getter
@Setter
public class Crypto implements Serializable {

    private long timestamp;
    private String cryptoType;
    private BigDecimal price;
}
