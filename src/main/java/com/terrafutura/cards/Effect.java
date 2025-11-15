package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.resources.*;
import java.util.List;

public interface Effect {
    /**
     *  Checks input resources, if all required are present and there is no pollution,
     *  removes them from input and adds correct resources to output, adds pollution
     *  based on effect.
     * @param input Input resources
     * @param output Puts all gained resources here
     * @param pollution Adds gained pollution to variable
     * @return If exchange was successful, returns true, else returns false.
     */
    boolean check(List<Resource> input, List<Resource> output, int pollution);

    /**
     * By the requirements of this implementation, Assistance is not required to implement -
     * not possible on any of the cards.
     * @return False.
     */
    default boolean hasAssistance() {
        return false;
    }

    /**
     * Return the effect description [(INPUT RESOURCES) -> (OUTPUT RESOURCES)]
     * @return string that describes function of effect
     */
    String state();
}