package main.java.com.terrafutura.cards.effects;

import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;

import java.util.List;

public class ArbitraryInOut implements Effect {
    private final int in, out;

    public ArbitraryInOut(int in, int out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public boolean check(List<Resource> desiredInput, List<Resource> desiredOutput, int pollution) {
        return desiredInput.size() == in && desiredOutput.size() == out && pollution == 0;
    }

    @Override
    public String state() {
        StringBuilder s = new StringBuilder();
        s.append("[(any ").append(in).append(" resources) -> (any ")
                .append(out).append(" resources)]");
        return s.toString();
    }
}
