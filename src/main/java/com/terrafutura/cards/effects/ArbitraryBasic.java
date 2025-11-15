package main.java.com.terrafutura.cards.effects;

import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;

import java.util.List;

public class ArbitraryBasic implements Effect {
    private final int from;
    private final List<Resource> to;

    public ArbitraryBasic(int from, List<Resource> resources) {
        this.from = from;
        to = resources;
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        if (input.size() >= from && pollution == 0) {
            if (input.contains(Resource.Pollution)) return false;
            output.addAll(to);
            return true;
        }
        else return false;
    }

    @Override
    public String state() {
        StringBuilder s = new StringBuilder();
        s.append("[(");
        s.append("any ").append(from).append(" resources) -> (");
        for (Resource r : to) {
            s.append(r).append(", ");
        }
        s.append(")]");
        return s.toString();
    }
}