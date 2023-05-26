package com.epam.recommendation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class Room implements AreaCalculator {

    private int length;
    private int width;
    private int height;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return length == room.length && width == room.width && height == room.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, width, height);
    }

    @Override
    public int calculateArea() {
        // area is calculated as
        // 2 * l * w + 2 * w * h + 2 * h * l.
        return (2 * this.length * this.width) +
                (2 * this.width * this.height) +
                (2 * this.height * this.length);
    }
}
