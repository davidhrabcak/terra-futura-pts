package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.resources.Resource;

import java.util.*;

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
        // Group pollution by card where it should go
        Map<Card, Integer> pollutionPerCard = new HashMap<>();

        for (GridPosition position : pollution) {
            if (invalidPosition(position) || grid.canPutCard(position)) {
                return false;
            }
            Optional<Card> cardOpt = grid.getCard(position);
            if (cardOpt.isEmpty()) {
                return false;
            }
            Card card = cardOpt.get();

            // count how much pollution should go to each card
            pollutionPerCard.put(card, pollutionPerCard.getOrDefault(card, 0) + 1);
        }

        // check if each card can accept the pollution
        for (Map.Entry<Card, Integer> entry : pollutionPerCard.entrySet()) {
            Card card = entry.getKey();
            int pollutionCount = entry.getValue();

            // list of pollution tokens to add
            List<Resource> pollutionToAdd = Collections.nCopies(pollutionCount, Resource.Pollution);

            // chceck if the card can accept all the pollution meant to go to it
            if (!card.canPutResources(pollutionToAdd)) {
                return false;
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
                                    List<Pair<Resource, GridPosition>> outputs, boolean upper) {
        int currentPollution = (int)card.getResources().stream().filter(r -> r == Resource.Pollution).count();
        List<Resource> inputResources = extractResources(inputs);
        List<Resource> outputResources = extractResources(outputs);
        if (upper) {
            return card.check(inputResources, outputResources, currentPollution);
        }else {
            return card.checkLower(inputResources, outputResources, currentPollution);
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