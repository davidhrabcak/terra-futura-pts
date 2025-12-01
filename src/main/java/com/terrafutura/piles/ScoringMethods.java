package main.java.com.terrafutura.piles;

import main.java.com.terrafutura.resources.Resource;
import main.java.com.terrafutura.scoring.Points;
import main.java.com.terrafutura.scoring.ScoringMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScoringMethods {
    private final List<ScoringMethod> scoringMethods;

    public ScoringMethods() { // data class for storing Scoring Method cards
        ScoringMethod s1 = new ScoringMethod(List.of(Resource.Car, Resource.Gear, Resource.Gear), new Points(2));
        ScoringMethod s2 = new ScoringMethod(List.of(Resource.Gear, Resource.Bulb, Resource.Money, Resource.Money), new Points(4));
        scoringMethods = new ArrayList<>();
        scoringMethods.add(s1);
        scoringMethods.add(s2);
        scoringMethods.add(s1);
        scoringMethods.add(s2);
        scoringMethods.add(s1);
        scoringMethods.add(s2);
    }

    public ScoringMethod getRandomScoringMethod() {
        Random r = new Random();
        return scoringMethods.remove(r.nextInt(scoringMethods.size()));
    }
}
