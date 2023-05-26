package com.epam.recommendation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CryptoNotSupportedException extends RuntimeException {

    public CryptoNotSupportedException() {
        super("Request crypto is not supported currently.");
    }
}
