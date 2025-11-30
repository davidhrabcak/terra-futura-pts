package main.java.com.terrafutura.board;

public class GridPosition {
    private int x, y;

    public GridPosition(int x, int y) {
        if (x <= 2 && x >= -2) {
            this.x = x;
        }
        if (y <= 2 && y >= -2) {
            this.y = y;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public int[] getCoordinates() {
        return new int[]{x, y};
    }
}