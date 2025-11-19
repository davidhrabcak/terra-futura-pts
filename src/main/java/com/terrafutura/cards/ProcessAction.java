package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.resources.Resource;

import java.util.List;

/**
 * Class for VALIDATING normal card activation without assistance
 * ONLY CHECKS if activation is possible - DOES NOT EXECUTE any changes
 */
public class ProcessAction {
    private ActionHelper helper = new ActionHelper();

    /**
     * VALIDATES if card activation is possible
     * @param card Card to be activated
     * @param grid Game board
     * @param inputs Input resources + positions
     * @param outputs Output resources + positions
     * @param pollution Positions for pollution placement
     * @return true if activation is VALID (can be executed later), false otherwise
     */
    public boolean activateCard(Card card, Grid grid,
                                List<Pair<Resource, GridPosition>> inputs,
                                List<Pair<Resource, GridPosition>> outputs,
                                List<GridPosition> pollution) {

        // 1. Null checks
        if (helper.nullEntry(grid, card, inputs, outputs, pollution)) {
            return false;
        }

        // 2. Validate input resources
        if (!helper.validateInputs(inputs, grid)) {
            return false;
        }

        // 3. Validate output resources
        if (!helper.validateOutputs(outputs, grid)) {
            return false;
        }

        // 4. Validate pollution positions
        if (!helper.validatePollution(pollution, grid)) {
            return false;
        }

        // 5. Validate card transformation
        if (!helper.validTransaction(card, inputs, outputs, pollution)) {
            return false;
        }

        // 6. ProcessAction cannot be assistance card
        if (card.hasAssistance()) {
            return false;
        }

        return true; // Validation successful
    }
}