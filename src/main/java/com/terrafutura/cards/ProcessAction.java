package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.resources.Resource;

import java.util.List;
import java.util.Optional;

/**
 * Class for VALIDATING AND EXECUTING normal card activation without assistance
 * CHECKS if activation is possible and EXECUTES changes if valid
 */
public class ProcessAction {
    private final ActionHelper helper = new ActionHelper();

    /**
     * The main entry point for card activation - validates and executes the transaction
     *
     * @param card The card being activated
     * @param grid The game board containing all cards
     * @param inputs List of resources to take from source cards and their positions
     * @param outputs List of resources to add to target cards and their positions
     * @param pollution List of positions where pollution tokens should be placed
     * @return true if activation was successfully executed, false if validation failed
     */
    public boolean activateCard(Card card, Grid grid,
                                List<Pair<Resource, GridPosition>> inputs,
                                List<Pair<Resource, GridPosition>> outputs,
                                List<GridPosition> pollution) {

        // 1. VALIDATION PHASE
        if (!validateActivation(card, grid, inputs, outputs, pollution)) {
            return false;
        }

        // 2. EXECUTION PHASE - only if validation passed
        executeTransaction(inputs, outputs, pollution, grid);
        return true;
    }

    /**
     * PRIVATE VALIDATION METHOD - checks if activation is possible
     */
    private boolean validateActivation(Card card, Grid grid,
                                       List<Pair<Resource, GridPosition>> inputs,
                                       List<Pair<Resource, GridPosition>> outputs,
                                       List<GridPosition> pollution) {

        // Card must be active (not blocked by pollution)
        if (!card.isActive()){
            return false;
        }

        if (card.hasAssistance()) {
            return false;
        }

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

        // Validate the transaction against card's effect rules
        // Try upper effect first, then lower effect if upper fails
        if (!helper.validTransaction(card, inputs, outputs, pollution, true)) { //try upper
            if (!helper.validTransaction(card, inputs, outputs, pollution, false)){ // try lower
                return false; // Both effects rejected the transaction
            }
        }

        return true; // All validation passed
    }

    /**
     * PRIVATE EXECUTION METHOD - performs the actual transaction
     */
    private void executeTransaction(List<Pair<Resource, GridPosition>> inputs,
                                    List<Pair<Resource, GridPosition>> outputs,
                                    List<GridPosition> pollution,
                                    Grid grid) {

        // Remove input resources from source cards
        for (Pair<Resource, GridPosition> input : inputs) {
            Optional<Card> cardOpt = grid.getCard(input.getSecond());
            cardOpt.ifPresent(sourceCard -> sourceCard.removeResource(input.getFirst()));
        }

        // Add output resources to target cards
        for (Pair<Resource, GridPosition> output : outputs) {
            Optional<Card> cardOpt = grid.getCard(output.getSecond());
            cardOpt.ifPresent(sourceCard -> sourceCard.putResources(List.of(output.getFirst())));
        }

        // Add pollution tokens
        for (GridPosition pollutionPos : pollution) {
            Optional<Card> cardOpt = grid.getCard(pollutionPos);
            cardOpt.ifPresent(Card::addPollution);
        }
    }
}