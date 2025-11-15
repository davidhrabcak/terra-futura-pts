package main.java.com.terrafutura.scoring;

import com.sun.jdi.IntegerType;

public class Points {
    public int points;

    public Points(int points) {
        this.points = points;
    }

    public void add(Points points) {
        this.points += points.points;
    }
}