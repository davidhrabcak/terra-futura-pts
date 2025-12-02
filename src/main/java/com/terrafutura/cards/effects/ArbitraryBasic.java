package main.java.com.terrafutura.cards.effects;

import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ArbitraryBasic implements Effect {
    private final int from, pollution;
    private final List<Resource> to;
    private boolean hasAssistance = false;

    public ArbitraryBasic(List<Resource> resources, int pollution) {
        this.pollution = pollution; // testing
        from = 1;
        to = new ArrayList<>(resources);
    }

    public ArbitraryBasic(int from, List<Resource> resources, int pollution) {
        this.from = from;
        this.pollution = pollution;
        to = new ArrayList<>(resources);
    }

    public ArbitraryBasic(int from, List<Resource> resources,int pollution, boolean hasAssistance) {
        this.from = from;
        this.pollution = pollution;
        to = new ArrayList<>(resources);
        this.hasAssistance = hasAssistance;
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        if (input.size() >= from && pollution == this.pollution) {
            return new HashSet<>(to).containsAll(output);
        }
        else return false;
    }

    @Override
    public boolean hasAssistance() {
        return hasAssistance;
    }

    @Override
    public String state() {
        StringBuilder s = new StringBuilder();
        s.append("[(");
        s.append("any ").append(from).append(" resources) -> (");
        for (Resource r : to) {
            if (to.size() > 1) {
                s.append(r).append(" ");
            } else s.append(r);
        }
        s.append(")]");
        return s.toString();
    }
}