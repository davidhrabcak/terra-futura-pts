package main.java.com.terrafutura.cards.effects;

import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;

import java.util.HashSet;
import java.util.List;

/**
 * Specified input to a specified number of any resources.
 */
public class ArbitraryOutput implements Effect {
    private final List<Resource> from;
    private final int to;

    public ArbitraryOutput(List<Resource> from, int to) {
        this.from = from;
        this.to = to;
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
        if (new HashSet<>(input).containsAll(from) && pollution == 0) {
            return desiredOutput.size() == to;
        }
        return false;
    }

    @Override
    public String state() {
        StringBuilder s = new StringBuilder();
        s.append("[(");
        for (Resource r : from) {
            s.append(r).append(", ");
        }
        s.append(") -> (").append("any ").append(from).append(" resources)]");
        s.append(")]");
        return s.toString();
    }
}
