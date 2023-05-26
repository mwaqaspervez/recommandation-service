package com.epam.recommendation.Util;

import com.epam.recommendation.model.Room;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class FileReader {

    private final List<Room> result = new ArrayList<>();
    private static final int LENGTH_INDEX = 0;
    private static final int WIDTH_INDEX = 1;
    private static final int HEIGHT_INDEX = 2;

    @PostConstruct
    public void run() {
        String fileContent = readFile();
        String[] lines = fileContent.split(System.lineSeparator());
        for (String line : lines) {
            String[] values = line.split("x");
            result.add(new Room(Integer.parseInt(values[LENGTH_INDEX]),
                    Integer.parseInt(values[WIDTH_INDEX]), Integer.parseInt(values[HEIGHT_INDEX])));
        }
    }

    private String readFile() {
        StringBuilder sb = new StringBuilder();
        ClassPathResource resource = new ClassPathResource("sample-input.txt");
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(resource.getFile()))) {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
        } catch (Exception e) {
            log.error("Error occurred while reading input file ", e);
        }
        return sb.toString();
    }

    public List<Room> getResult() {
        return result;
    }
}
