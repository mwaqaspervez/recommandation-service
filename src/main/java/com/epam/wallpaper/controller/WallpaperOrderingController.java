package com.epam.wallpaper.controller;

import com.epam.wallpaper.model.WallpaperResponse;
import com.epam.wallpaper.service.WallpaperOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WallpaperOrderingController {

    private final WallpaperOrderService wallpaperOrderService;

    public WallpaperOrderingController(WallpaperOrderService wallpaperOrderService) {
        this.wallpaperOrderService = wallpaperOrderService;
    }

    /**
     * Find the normalized value of a crypto provided by the
     * requested date.
     *
     * @return The name of the cryptocurrency with the highest normalized value.
     */
    @GetMapping("/orders")
    public ResponseEntity<List<WallpaperResponse>> getArea() {
        return ResponseEntity.ok()
                .body(this.wallpaperOrderService.getAllAreas());
    }

    @GetMapping("/orders/cubes")
    public ResponseEntity<List<WallpaperResponse>> getCubicShapes() {
        return ResponseEntity.ok()
                .body(this.wallpaperOrderService.getCubicShapes());
    }

    @GetMapping("/orders/duplicates")
    public ResponseEntity<List<WallpaperResponse>> getDuplicates() {
        return ResponseEntity.ok()
                .body(this.wallpaperOrderService.getDuplicates());
    }
}
