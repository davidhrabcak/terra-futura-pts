package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class containing common validation logic for ProcessAction and ProcessActionAssistance
 * Provides validation methods only - no state changes performed
 */
public class ActionHelper {

    /**
     * CHECKS if any of the required parameters is null
     */
    public boolean nullEntry(Grid grid, Card card, List<Pair<Resource, GridPosition>> inputs,
                             List<Pair<Resource, GridPosition>> outputs, List<GridPosition> pollution) {
        return grid == null || card == null || inputs == null || outputs == null || pollution == null;
    }

    /**
     * VALIDATES all input resources - checks positions and resource availability
     */
    public boolean validateInputs(List<Pair<Resource, GridPosition>> inputs, Grid grid) {
        for (Pair<Resource, GridPosition> input : inputs) {
            Resource resource = input.getFirst();
            GridPosition position = input.getSecond();
            if (!grid.canPutCard(position)) {
                return false; // Invalid position
            }
            Card card = grid.getCard(position);
            if (card == null || !card.canGetResources(List.of(resource))) {
                return false; // Cannot get resources
            }
        }
        return true;
    }

    /**
     * VALIDATES all output resources - checks positions and card capacity
     */
    public boolean validateOutputs(List<Pair<Resource, GridPosition>> outputs, Grid grid) {
        for (Pair<Resource, GridPosition> output : outputs) {
            Resource resource = output.getFirst();
            GridPosition position = output.getSecond();
            if (!grid.canPutCard(position)) {
                return false; // Invalid position
            }
            Card card = grid.getCard(position);
            if (card == null || !card.canPutResources(List.of(resource))) {
                return false; // Cannot add resources
            }
        }
        return true;
    }

    /**
     * VALIDATES pollution positions - checks if positions exist and have cards
     */
    public boolean validatePollution(List<GridPosition> pollution, Grid grid) {
        for (GridPosition position : pollution) {
            if (!grid.canPutCard(position)) {
                return false; // Invalid position
            }
            if (grid.getCard(position) == null) {
                return false; // No card at position
            }
        }
        return true;
    }

    /**
     * VALIDATES transformation using card.check() method
     * VERIFIES if card supports the transformation, but DOES NOT execute it
     */
    public boolean validTransaction(Card card, List<Pair<Resource, GridPosition>> inputs,
                                    List<Pair<Resource, GridPosition>> outputs, List<GridPosition> pollution) {
        List<Resource> inputResources = extractResources(inputs);
        List<Resource> outputResources = extractResources(outputs);
        return card.check(inputResources, outputResources, pollution.size());
    }

    /**
     * Extracts Resources from List<Pair<Resource, GridPosition>>
     * Used for input transformation for card.check()
     */
    public List<Resource> extractResources(List<Pair<Resource, GridPosition>> pairs) {
        List<Resource> resources = new ArrayList<>();
        for (Pair<Resource, GridPosition> pair : pairs) {
            resources.add(pair.getFirst());
        }
        return resources;
    }
}