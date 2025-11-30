package main.java.com.terrafutura.cards.effects;

import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static main.java.com.terrafutura.resources.Resource.Money;

public class ArbitraryBasic implements Effect {
    private final int from;
    private final List<Resource> to;

    public ArbitraryBasic(List<Resource> to) {
        from = 1;
        this.to = new ArrayList<>(to);
    }

    public ArbitraryBasic() {
        from = 1;
        to = List.of(Money);
    }

    public ArbitraryBasic(int from, List<Resource> resources) {
        this.from = from;
        to = new ArrayList<>(resources);
    }

    public ArbitraryBasic(int from, List<Resource> resources, boolean hasAssistance) {
        this.from = from;
        to = new ArrayList<>(resources);
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        if (input.size() >= from && pollution == 0) {
            return new HashSet<>(to).containsAll(output);
        }
        else return false;
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