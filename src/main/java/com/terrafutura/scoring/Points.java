package main.java.com.terrafutura.scoring;

public class Points {

    private int value;

    public Points(int points) {
        this.value = points;
    }

    public void add(Points points) {
        this.value += points.getValue();
    }

    public void subtract(Points points) {
        this.value -= points.getValue();
    }

    public int getValue() {
        return value;
    }
}