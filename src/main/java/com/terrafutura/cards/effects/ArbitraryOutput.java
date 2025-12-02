package main.java.com.terrafutura.cards.effects;

import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Specified input to a specified number of any resources.
 */
public class ArbitraryOutput implements Effect {
    private final List<Resource> from;
    private final int to;
    private boolean hasAssistance = false;
    private final int pollution;

    public ArbitraryOutput(List<Resource> from, int to, int pollution) {
        this.from = new ArrayList<>(from);
        this.to = to;
        this.pollution = pollution;
    }

    public ArbitraryOutput(List<Resource> from, int to, boolean hasAssistance, int pollution) {
        this.from = new ArrayList<>(from);
        this.to = to;
        this.hasAssistance = hasAssistance;
        this.pollution = pollution;
    }

    /**
     *
     * @param input Input resources
     * @param desiredOutput caller specifies which resources they need
     * @param pollution if card is polluted, pollution is non-zero
     * @return If the right input and correct output size is provided, return true.
     */
    @Override
    public boolean check(List<Resource> input, List<Resource> desiredOutput, int pollution) {
        if (new HashSet<>(input).containsAll(from) && pollution == this.pollution) {
            return desiredOutput.size() == to;
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
        s.append(") -> (").append("any ").append(to).append(" resources)]");
        return s.toString();
    }
}
