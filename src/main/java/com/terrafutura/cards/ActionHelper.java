package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Helper class containing common validation logic for ProcessAction and ProcessActionAssistance
 * Provides validation methods only - no state changes performed
 */
public class ActionHelper {


    /**
     * Checks if any required parameters are null before proceeding with validation
     * @return true if any parameter is null, false if all parameters are valid objects
     */
    public boolean nullEntry(Grid grid, Card card, List<Pair<Resource, GridPosition>> inputs,
                             List<Pair<Resource, GridPosition>> outputs, List<GridPosition> pollution) {
        return grid == null || card == null || inputs == null || outputs == null || pollution == null;
    }

    /**
     * Validates if a grid position is within the allowed 5x5 game board range (-2 to +2)
     * @return true if position is outside valid range, false if the position is valid
     */
    private boolean invalidPosition(GridPosition gridPosition){
        return gridPosition.getX() < -2 || gridPosition.getX() > 2 || gridPosition.getY() < -2 || gridPosition.getY() > 2;
    }

    /**
     * Validates all input resources - checks positions and resource availability
     * @return false if the position is invalid, has no card, or card cannot provide the resource
     */
    public boolean validateInputs(List<Pair<Resource, GridPosition>> inputs, Grid grid) {
        for (Pair<Resource, GridPosition> input : inputs) {
            Resource resource = input.getFirst();
            GridPosition position = input.getSecond();

            // Check if the position is valid and contains a card (canPutCard=true means empty position)
            if (invalidPosition(position) || grid.canPutCard(position)){
                return false; // Invalid position or no card exists at position
            }

            Optional<Card> cardOpt = grid.getCard(position);
            if (cardOpt.isEmpty() || !cardOpt.get().canGetResources(List.of(resource))) {
                return false; // Card doesn't exist or cannot provide the required resource
            }
        }
        return true;
    }

    /**
     * Validates all output resources - checks positions and card capacity
     * @return false if the position is invalid, has no card, or card cannot accept the resource
     */
    public boolean validateOutputs(List<Pair<Resource, GridPosition>> outputs, Grid grid) {
        for (Pair<Resource, GridPosition> output : outputs) {
            Resource resource = output.getFirst();
            GridPosition position = output.getSecond();

            // Check if the position is valid and contains a card
            if (invalidPosition(position) || grid.canPutCard(position)){
                return false; // Invalid position or no card exists at position
            }
            Optional<Card> cardOpt = grid.getCard(position);
            if (cardOpt.isEmpty() || !cardOpt.get().canPutResources(List.of(resource))) {
                return false; // Card doesn't exist or cannot accept the resource
            }
        }
        return true;
    }

    /**
     * Validates pollution positions - checks if positions exist and cards can accept pollution
     * @return false if the position is invalid, has no card, or card cannot accept more pollution
     */
    public boolean validatePollution(List<GridPosition> pollution, Grid grid) {
        for (GridPosition position : pollution) {
            // Check if the position is valid and contains a card (canPutCard=true means empty position)
            if (invalidPosition(position) || grid.canPutCard(position)){
                return false; // Invalid position or no card exists at position
            }
            Optional<Card> cardOpt = grid.getCard(position);
            if (cardOpt.isEmpty() || !cardOpt.get().canAddPollution()) {
                return false; // Card doesn't exist or cannot accept more pollution
            }
        }
        return true;
    }

    /**
     * Validates transformation using card.check() method
     * Verifies if the card supports the transformation but DOES NOT execute it
     * Tests both upper and lower effects to find a valid transaction pattern
     * @param upper If true, validates against the upper effect; if false, uses a lower effect
     * @return true if the card's effect approves the proposed resource transformation
     */
    public boolean validTransaction(Card card, List<Pair<Resource, GridPosition>> inputs,
                                    List<Pair<Resource, GridPosition>> outputs, List<GridPosition> pollution, boolean upper) {
        List<Resource> inputResources = extractResources(inputs);
        List<Resource> outputResources = extractResources(outputs);
        if (upper) {
            return card.check(inputResources, outputResources, pollution.size());
        }else {
            return card.checkLower(inputResources, outputResources, pollution.size());
        }
    }

    /**
     * Extracts Resource objects from a list of Resource+Position pairs
     * @return List containing only the Resource objects from the input pairs
     */
    public List<Resource> extractResources(List<Pair<Resource, GridPosition>> pairs) {
        List<Resource> resources = new ArrayList<>();
        for (Pair<Resource, GridPosition> pair : pairs) {
            resources.add(pair.getFirst());
        }
        return resources;
    }
}