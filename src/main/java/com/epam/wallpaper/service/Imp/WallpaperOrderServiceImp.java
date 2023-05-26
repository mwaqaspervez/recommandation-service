package com.epam.wallpaper.service;

import com.epam.wallpaper.Util.FileReader;
import com.epam.wallpaper.model.Room;
import com.epam.wallpaper.model.WallpaperResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WallpaperOrderService {

    private final List<Room> roomsList;

    public WallpaperOrderService(FileReader fileReader) {
        roomsList = fileReader.getResult();
    }

    public List<WallpaperResponse> getAllAreas() {
        return this.roomsList.stream()
                .map(record -> {
                    int area = record.calculateArea();

                    int totalWallpaperRequired = area +
                            calculateSmallest(record.getLength(), record.getWidth(), record.getHeight());

                    return new WallpaperResponse(record.toString(), totalWallpaperRequired, null);
                }).collect(Collectors.toList());
    }

    public List<WallpaperResponse> getCubicShapes() {
        log.info("Sorted Cubic values");
        return this.roomsList.stream()
                // filter those values which has equal sides
                .filter(r -> r.getHeight() == r.getLength() &&
                        r.getLength() == r.getWidth())
                .sorted((o1, o2) -> {
                    // sort them in descending order
                    int area1 = o1.calculateArea();
                    int area2 = o2.calculateArea();
                    return Integer.compare(area2, area1);
                })
                .map(r -> new WallpaperResponse(r.toString(),
                        r.calculateArea(), null))
                .collect(Collectors.toList());
    }

    public List<WallpaperResponse> getDuplicates() {
        return this.roomsList
                .stream()
                // convert to map with key as Room and value as count of their occurrence
                .collect(Collectors.toMap(k -> k, v -> 1, Integer::sum))
                .entrySet()
                .stream()
                // filter values which are appeared more than once.
                .filter(k -> k.getValue() > 1)
                .map(k -> new WallpaperResponse(k.getKey().toString(),
                        k.getKey().calculateArea(), k.getValue()))
                .collect(Collectors.toList());
    }

    private int calculateSmallest(int length, int width, int height) {
        // if the length is smaller than width then return
        // smaller value from length and height
        if (length < width) {
            return Math.min(length, height);
        }
        return Math.min(width, height);
    }

}
