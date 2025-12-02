package main.java.com.terrafutura.cards.effects;

import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;

import java.util.List;

public class ArbitraryInOut implements Effect {
    private final int in, out;
    private boolean hasAssistance = false;
    private final int pollution;


    public ArbitraryInOut(int in, int out, int pollution) {
        this.in = in;
        this.out = out;
        this.pollution = pollution;
    }

    public ArbitraryInOut(int in, int out, boolean hasAssistance, int pollution) {
        this.in = in;
        this.out = out;
        this.hasAssistance = hasAssistance;
        this.pollution = pollution;
    }

    @Override
    public boolean hasAssistance() {
        return hasAssistance;
    }

    @Override
    public boolean check(List<Resource> desiredInput, List<Resource> desiredOutput, int pollution) {
        return desiredInput.size() == in && desiredOutput.size() == out && pollution == this.pollution;
    }

    @Override
    public String state() {
        return "[(any " + in + " resources) -> (any " +
                out + " resources)]";
    }
}
