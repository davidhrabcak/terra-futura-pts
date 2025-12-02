package main.java.com.terrafutura.scoring;

import main.java.com.terrafutura.resources.Resource;
import java.util.*;

/**
 * Represents a scoring rule chosen by the player at the end of the game.
 * The scoring method defines a resource combination that grants
 * a fixed amount of points for every complete set of required resources.
 */
public class ScoringMethod {

    /**
     * Required resources to form one scoring combination
     */
    private final List<Resource> resources;

    /**
     * Points awarded for one complete combination
     */
    private final Points pointsPerCombination;

    /**
     * Final total points won after using this method
     */
    private Points calculatedTotal = new Points(0);

    /**
     * Score value for each resource type according to game rules
     */
    private final Map<Resource, Integer> RESOURCE_POINTS = Map.of(
            Resource.Green, 1,
            Resource.Red, 1,
            Resource.Yellow, 1,
            Resource.Bulb, 5,
            Resource.Gear, 5,
            Resource.Car, 6,
            Resource.Pollution, -1
    );

    /**
     * Creates a specific scoring method.
     *
     * @param resources list of resources that form one scoring combination
     * @param pointsPerCombination points gained per full matching combination
     */
    public ScoringMethod(List<Resource> resources, Points pointsPerCombination) {
        this.resources = new ArrayList<>(resources);
        this.pointsPerCombination = pointsPerCombination;
    }

    /**
     * Calculates and stores the final total score after using this method.
     * Should be called after the final activation phase.
     *
     * @param playerResources list of all resources a player has on their grid
     */
    public void selectThisMethodAndCalculate(List<Resource> playerResources) {
        int combinationPoints = calculateCombinationPoints(playerResources);
        int resourcePoints = calculateResourcePoints(playerResources);

        calculatedTotal = new Points(combinationPoints + resourcePoints);
    }

    //calculate points obtained by this scoring method
    private int calculateCombinationPoints(List<Resource> playerResources) {
        // Count occurrences of each resource the player owns
        Map<Resource, Integer> counts = count(playerResources);
        // Count occurrences needed for each resource in the scoring method
        Map<Resource, Integer> needed = count(resources);

        // Determine how many full combinations the player can form
        int combinations = Integer.MAX_VALUE;

        for (Map.Entry<Resource, Integer> entry : needed.entrySet()) {
            int available = counts.getOrDefault(entry.getKey(), 0);
            int required = entry.getValue();
            int possible = available / required;
            combinations = Math.min(combinations, possible);
        }

        if (combinations == Integer.MAX_VALUE) combinations = 0;
        return combinations * pointsPerCombination.getValue();
    }

    //calculate points for resources on non-blocked cards in the player's grid
    private int calculateResourcePoints(List<Resource> resources) {
        int sum = 0;
        for (Resource r : resources) {
            sum += RESOURCE_POINTS.getOrDefault(r, 0);
        }
        return sum;
    }

    private Map<Resource, Integer> count(List<Resource> list) {
        Map<Resource, Integer> map = new HashMap<>();
        for (Resource r : list) {
            map.put(r, map.getOrDefault(r, 0) + 1);
        }
        return map;
    }

    /**
     * @return the final total score
     */
    public Points getCalculatedTotal() {
        return calculatedTotal;
    }

    /**
     * @return readable representation of the chosen scoring method with player's points awarded using this method
     */
    public String state() {
        return "ScoringMethod{resources=" + resources +
                ", pointsPerCombination=" + pointsPerCombination +
                ", total=" + calculatedTotal + "}";
    }

    @Override
    public String toString() {
        return state();
    }
}