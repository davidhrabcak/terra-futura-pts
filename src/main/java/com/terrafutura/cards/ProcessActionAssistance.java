package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.resources.Resource;

import java.util.List;

/**
 * Placeholder class for assistance card activation functionality
 * According to project requirements: "Simplified rules, no Assistance card effect"
 * This class exists to maintain interface consistency but ALWAYS rejects assistance activations
 * ASSISTANCE FUNCTIONALITY IS INTENTIONALLY DISABLED in this simplified version of Terra Futura.
 * The full game rules include complex assistance mechanics where players can use other players'
 * card effects, but for this implementation we follow the simplified rules specification.
 */
public class ProcessActionAssistance {
    private final ActionHelper helper = new ActionHelper();
    private SelectReward selectReward;

    /**
     * Validates and executes assistance activation including reward selection
     * @param card Starting card to be activated (must have assistance)
     * @param grid Game board
     * @param assistingPlayer ID of the player whose card we're copying
     * @param assistingCard Card whose effect we're copying
     * @param inputs Input resources + positions (payment for transformation)
     * @param outputs Output resources + positions
     * @param pollution Positions for pollution placement (goes to our card)
     * @return true if assistance activation was successfully executed, false otherwise
     */
    public boolean activateCard(Card card, Grid grid, int assistingPlayer, Card assistingCard,
                                List<Pair<Resource, GridPosition>> inputs,
                                List<Pair<Resource, GridPosition>> outputs,
                                List<GridPosition> pollution) {
        // In the full game, this would allow using another player's card effect,
        // but for this implementation we follow the simplified rules specification.
        return false;

//        // 1. VALIDATION PHASE - check if assistance activation is possible
//        if (!validateAssistanceActivation(card, grid, assistingPlayer, assistingCard, inputs, outputs, pollution)) {
//            return false;
//        }
//
//        // 2. EXECUTION PHASE - perform the complete assistance transaction
//        executeAssistanceTransaction(card, grid, assistingPlayer, assistingCard, inputs, outputs, pollution);
//        return true;

    }

    /**
     * Validates all assistance-specific rules and constraints
     */
    private boolean validateAssistanceActivation(Card card, Grid grid, int assistingPlayer, Card assistingCard,
                                                 List<Pair<Resource, GridPosition>> inputs,
                                                 List<Pair<Resource, GridPosition>> outputs,
                                                 List<GridPosition> pollution) {

        // 1. Assistance-specific validation checks
        if (assistingPlayer < 1 || assistingPlayer > 4 || assistingCard == null) {
            return false;
        }

        if (helper.nullEntry(grid, card, inputs, outputs, pollution)) {
            return false;
        }

        if (!card.hasAssistance()) {
            return false;
        }

        // Card must be active (not blocked by pollution)
        //if we can't add anything, we the cards is blocked
        if (!assistingCard.canPutResources(List.of())){
            return false;
        }

        // Must have at least 1 resource in inputs for helping player reward
        List<Resource> inputResources = helper.extractResources(inputs);
        if (inputResources.isEmpty()) {
            return false;
        }

        // 2. Validate transformation using ASSISTING CARD (try both effects)
        List<Resource> outputResources = helper.extractResources(outputs);
        int assistingCardPollution = (int)assistingCard.getResources().stream().filter(r -> r == Resource.Pollution).count();
        if (!assistingCard.check(inputResources, outputResources, assistingCardPollution)) {
            if (!assistingCard.checkLower(inputResources, outputResources, assistingCardPollution)) {
                return false; // Helping card must support this transformation
            }
        }

        // 3. Common validation checks
        if (!helper.validateInputs(inputs, grid)) {
            return false;
        }
        if (!helper.validateOutputs(outputs, grid)) {
            return false;
        }
        if (!helper.validatePollution(pollution, grid)) {
            return false;
        }

        return true;
    }

    /**
     * Executes the complete assistance transaction including reward setup
     */
    private void executeAssistanceTransaction(Card card, Grid grid, int assistingPlayer, Card assistingCard,
                                              List<Pair<Resource, GridPosition>> inputs,
                                              List<Pair<Resource, GridPosition>> outputs,
                                              List<GridPosition> pollution) {

        // PHASE 1: Remove input resources from source cards (payment for transformation)
        for (Pair<Resource, GridPosition> input : inputs) {
            grid.getCard(input.getSecond())
                    .ifPresent(sourceCard -> sourceCard.removeResource(input.getFirst()));
        }

        // PHASE 2: Setup reward selection for assisting player
        // (The actual reward selection happens later via selectReward.selectReward())
        List<Resource> paidResources = helper.extractResources(inputs);
        this.selectReward = new SelectReward();
        selectReward.setReward(assistingPlayer, card, paidResources.toArray(new Resource[0]));

        // PHASE 3: Add output resources to target cards (result of transformation)
        for (Pair<Resource, GridPosition> output : outputs) {
            grid.getCard(output.getSecond())
                    .ifPresent(targetCard -> targetCard.putResources(List.of(output.getFirst())));
        }

        // PHASE 4: Add pollution tokens (go to the starting card being activated)
        for (GridPosition pollutionPos : pollution) {
            grid.getCard(pollutionPos).ifPresent(targetCard -> targetCard.putResources(List.of(Resource.Pollution)));
        }
    }
    public boolean canSelectReward(Resource resource) {
        return selectReward != null && selectReward.canSelectReward(resource);
    }
    public void selectReward(Resource resource) {
        if (selectReward == null) {
            throw new IllegalStateException("No reward available to select");
        }
        selectReward.selectReward(resource);
        selectReward = null;
    }
    public boolean hasRewardAvailable() {
        return selectReward != null;
    }
}