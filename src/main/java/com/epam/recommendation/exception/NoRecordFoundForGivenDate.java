package com.epam.recommendation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoRecordFoundForGivenDate extends RuntimeException {

    public NoRecordFoundForGivenDate() {
        super("No record found for given date");
    }
}
