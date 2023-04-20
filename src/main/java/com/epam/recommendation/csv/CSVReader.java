package com.epam.recommendation.csv;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Component
public class CSVReader {

    /**
     * Reads and return a list of csv records responding to the given filename
     * @param filename The name of the file from which to read from.
     * @return list of array of string containing the file content.
     * @throws Exception in case the file could not be found or read.
     */
    public List<String[]> readFile(String filename) throws Exception {
        ClassPathResource resource = new ClassPathResource(filename);
        InputStream inputStream = resource.getInputStream();
        Reader reader = new InputStreamReader(inputStream);
        try (com.opencsv.CSVReader csvReader = new com.opencsv.CSVReader(reader)) {
            return csvReader.readAll();
        }
    }
}
