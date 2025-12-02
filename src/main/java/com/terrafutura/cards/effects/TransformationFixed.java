package main.java.com.terrafutura.cards.effects;

import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TransformationFixed implements Effect {
    private final List<Resource> from;
    private final List<Resource> to;
    private boolean hasAssistance = false;
    private final int pollution;

    public TransformationFixed(List<Resource> from, List<Resource> to, int pollution) {
        this.from = new ArrayList<>(from);
        this.to = new ArrayList<>(to);
        this.pollution = pollution;
    }

    public TransformationFixed(List<Resource> from, List<Resource> to, int pollution, boolean hasAssistance, int pollution1) {
        this.from = new ArrayList<>(from);
        this.to = new ArrayList<>(to);
        this.hasAssistance = hasAssistance;
        this.pollution = pollution1;
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        if (new HashSet<>(input).containsAll(from) && pollution == this.pollution) {
            return new HashSet<>(to).containsAll(output);
        }
        return false;
    }

    @Override
    public boolean hasAssistance() {
        return hasAssistance;
    }

    @Override
    public String state() {
        StringBuilder s = new StringBuilder();
        s.append("[(");
        for (Resource r : from) {
            s.append(r).append(" ");
        }
        s.append(") -> (");
        for (Resource r: to) {
            s.append(r).append(" ");
        }
        s.append(")]");
        return s.toString();
    }
}