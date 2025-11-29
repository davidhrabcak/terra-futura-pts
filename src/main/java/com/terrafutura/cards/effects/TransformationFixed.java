package main.java.com.terrafutura.cards.effects;

import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static main.java.com.terrafutura.resources.Resource.Car;
import static main.java.com.terrafutura.resources.Resource.Money;

public class TransformationFixed implements Effect {
    private List<Resource> from;
    private List<Resource> to;

    public TransformationFixed() {
        from = List.of(Money);
        to = List.of(Car);
    }

    public TransformationFixed(List<Resource> from, List<Resource> to, int pollution) {
        this.from = new ArrayList<>(from);
        this.to = new ArrayList<>(to);
    }

    public TransformationFixed(List<Resource> from, List<Resource> to, int pollution, boolean hasAssistance) {
        this.from = new ArrayList<>(from);
        this.to = new ArrayList<>(to);
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        if (new HashSet<>(input).containsAll(from) && pollution == 0) {
            return new HashSet<>(to).containsAll(output);
        }
        return false;
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