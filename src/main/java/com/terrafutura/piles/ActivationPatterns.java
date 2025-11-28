package main.java.com.terrafutura.piles;

import main.java.com.terrafutura.board.ActivationPattern;
import main.java.com.terrafutura.board.Grid;

import java.util.*;

public class ActivationPatterns {
    private final List<ActivationPattern> activationPatterns;

    public ActivationPatterns(Grid g) { // mock data
        Collection<AbstractMap.SimpleEntry<Integer, Integer>> cornerPattern = new ArrayList<>();
        cornerPattern.add(new AbstractMap.SimpleEntry<>(0, 0));
        cornerPattern.add(new AbstractMap.SimpleEntry<>(0, 2));
        cornerPattern.add(new AbstractMap.SimpleEntry<>(2, 0));
        cornerPattern.add(new AbstractMap.SimpleEntry<>(2, 2));

        Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern = new ArrayList<>();
        pattern.add(new AbstractMap.SimpleEntry<>(0, 0));
        pattern.add(new AbstractMap.SimpleEntry<>(0, 1));
        pattern.add(new AbstractMap.SimpleEntry<>(0, 2));
        pattern.add(new AbstractMap.SimpleEntry<>(1, 0));
        pattern.add(new AbstractMap.SimpleEntry<>(2, 0));

        Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern1 = new ArrayList<>();
        pattern.add(new AbstractMap.SimpleEntry<>(1, 1));
        pattern.add(new AbstractMap.SimpleEntry<>(1, 0));
        pattern.add(new AbstractMap.SimpleEntry<>(1, 2));
        pattern.add(new AbstractMap.SimpleEntry<>(0, 1));
        pattern.add(new AbstractMap.SimpleEntry<>(2, 1));

        activationPatterns = new ArrayList<>();
        activationPatterns.add(new ActivationPattern(g, cornerPattern));
        activationPatterns.add(new ActivationPattern(g, cornerPattern));
        activationPatterns.add(new ActivationPattern(g, pattern));
        activationPatterns.add(new ActivationPattern(g, pattern));
        activationPatterns.add(new ActivationPattern(g, pattern1));
        activationPatterns.add(new ActivationPattern(g, pattern1));
    }

    public ActivationPattern getRandomActivationPattern() {
        Random r = new Random();
        return activationPatterns.remove(r.nextInt(activationPatterns.size()));
    }
}
