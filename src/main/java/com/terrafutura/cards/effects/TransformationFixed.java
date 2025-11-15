package main.java.com.terrafutura.cards.effects;

import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;

import java.util.HashSet;
import java.util.List;

public class TransformationFixed implements Effect {
    private List<Resource> from;
    private List<Resource> to;

    public TransformationFixed(List<Resource> from, List<Resource> to, int pollution, boolean hasAssistance) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        if (new HashSet<>(input).containsAll(from) && pollution == 0) {
            output.addAll(to);
            return true;
        }
        else return false;
    }

    @Override
    public String state() {
        StringBuilder s = new StringBuilder();
        s.append("[(");
        for (Resource r : from) {
            s.append(r).append(", ");
        }
        s.append(") -> (");
        for (Resource r : to) {
            s.append(s).append(", ");
        }
        s.append(")]");
        return s.toString();
    }
}