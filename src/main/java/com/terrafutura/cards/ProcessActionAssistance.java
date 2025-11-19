package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.resources.Resource;

import java.util.List;

/**
 * Class for VALIDATING card activation with assistance effect
 * ONLY CHECKS if assistance activation is possible - NO EXECUTION
 * Special validation rules for starting card effect copying
 */
public class ProcessActionAssistance {
    private ActionHelper helper = new ActionHelper();

    /**
     * VALIDATES if assistance activation is possible
     * @param card Starting card to be activated (must have assistance)
     * @param grid Game board
     * @param assistingPlayer ID of player whose card we're copying
     * @param assistingCard Card whose effect we're copying
     * @param inputs Input resources + positions (payment for transformation)
     * @param outputs Output resources + positions
     * @param pollution Positions for pollution placement (goes to our card)
     * @return true if assistance activation is VALID, false otherwise
     */
    public boolean activateCard(Card card, Grid grid, int assistingPlayer, Card assistingCard,
                                List<Pair<Resource, GridPosition>> inputs,
                                List<Pair<Resource, GridPosition>> outputs,
                                List<GridPosition> pollution) {

        // 1. Assistance-specific VALIDATION checks

        // Validate assisting player (2-4 based on player count)
        if (assistingPlayer < 2 || assistingPlayer > 4 || assistingCard == null) {
            return false;
        }

        // Activated card must be starting card (has assistance)
        if (!card.hasAssistance()) {
            return false;
        }

        // Card we're copying must be activatable
        if (!isCardActivatable(assistingCard, grid)) {
            return false;
        }

        // Must have at least 1 resource in inputs for assisting player reward
        if (helper.extractResources(inputs).isEmpty()) {
            return false;
        }

        // 2. Validate transformation using ASSISTING CARD
        List<Resource> inputResources = helper.extractResources(inputs);
        List<Resource> outputResources = helper.extractResources(outputs);
        if (!assistingCard.check(inputResources, outputResources, pollution.size())) {
            return false; // Assisting card must support this transformation
        }

        // 3. Common validation checks via helper (same as in ProcessAction)
        if (helper.nullEntry(grid, card, inputs, outputs, pollution)) {
            return false;
        }
        if (!helper.validateInputs(inputs, grid)) {
            return false;
        }
        if (!helper.validateOutputs(outputs, grid)) {
            return false;
        }
        if (!helper.validatePollution(pollution, grid)) {
            return false;
        }

        return true; // Assistance validation successful
    }

    /**
     * CHECKS if card is activatable (not locked by pollution, etc.)
     */
    private boolean isCardActivatable(Card card, Grid grid){
        return !card.getResources().isEmpty() && card.canGetResources(card.getResources());
    }
}