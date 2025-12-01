package main.java.com.terrafutura.board;

import java.util.AbstractMap;
import java.util.Objects;

public class GridPosition {
    private int x, y;

    public GridPosition(int x, int y) {
        if (x <= 2 && x >= -2) {
            this.x = x;
        }
        if (y <= 2 && y >= -2) {
            this.y = y;
        }
        if (x < -2 || x > 2 || y < -2 || y > 2) {
            throw new IllegalArgumentException("Coordinates out of bounds.");
        }
    }

    public GridPosition(AbstractMap.SimpleEntry<Integer, Integer> c) {
        if (c.getKey() <= 2 && c.getKey() >= -2) {
            this.x = c.getKey();
        }
        if (c.getValue() <= 2 && c.getValue() >= -2) {
            this.y = c.getValue();
        }
        if (c.getKey() < -2 || c.getKey() > 2 || c.getValue() < -2 || c.getValue() > 2) {
            throw new IllegalArgumentException("Coordinates out of bounds.");
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridPosition that)) return false;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}