package main.java.com.terrafutura.board;

import main.java.com.terrafutura.cards.Card;

import java.util.*;

/**
 * Represents a player's grid where cards are placed and activated.
 * The grid maintains a 5x5 area but must form a compact 3x3 territory by the end of the game.
 */
public class Grid implements InterfaceActivateGrid{
    //Map of cards placed on their grid positions
    private final Map<GridPosition, Card> cards;
    //Set of positions that have been activated in the current turn
    private final Set<GridPosition> activated;
    private final List<GridPosition> activationPattern;
    private GridPosition lastAddedCardPosition;

    /**
     * Constructs a new grid with the starting card at position (0,0)
     * @param startingCard the initial card placed on the grid
     */
    public Grid(Card startingCard) {
        cards = new HashMap<>();
        cards.put(new GridPosition(0,0), startingCard);
        activated = new HashSet<>();
        activationPattern = new ArrayList<>();
        lastAddedCardPosition = new GridPosition(0,0);
    }

    /**
     * Retrieves the card at the specified coordinate, if present
     * @param coordinate the grid position to check
     * @return an Optional containing the card if present, empty otherwise
     */
    public Optional<Card> getCard(GridPosition coordinate) {
        if (cards.containsKey(coordinate)) {
            return Optional.of(cards.get(coordinate));
        }
        return Optional.empty();
    }

    /**
     * Checks if a card can be placed at the specified coordinate
     * @param coordinate the grid position where the card should be placed
     * @return true if the card can be placed, false otherwise
     */
    public boolean canPutCard(GridPosition coordinate){
        // Check if position is within the 5x5 grid boundaries
        if(coordinate.getX() < - 2 || coordinate.getX() > 2 || coordinate.getY() < -2 || coordinate.getY() > 2){
            return false;
        }

        // Check if the position is already occupied by another card
        if(cards.containsKey(coordinate)){
            return false;
        }
        // Count how many cards are already in the same row and column
        int cardsInRow = 0;
        int cardsInColumn = 0;

        for (GridPosition gridPosition: cards.keySet()) {
            if(gridPosition.getX() == coordinate.getX()){
                cardsInColumn++;
            }
            if(gridPosition.getY() == coordinate.getY()){
                cardsInRow++;
            }
        }
        // Check maximum cards per row/column constraint (max 3)
        if (cardsInRow >= 3 || cardsInColumn >= 3) {
            return false;
        }
        // Check if the resulting grid would still fit in a 3x3 bounding box
        if (!cards.isEmpty()){
            int minX = coordinate.getX();
            int maxX = coordinate.getX();
            int minY = coordinate.getY();
            int maxY = coordinate.getY();
            for (GridPosition pos : cards.keySet()) {
                minX = Math.min(minX, pos.getX());
                maxX = Math.max(maxX, pos.getX());
                minY = Math.min(minY, pos.getY());
                maxY = Math.max(maxY, pos.getY());
            }
            // The grid must form a compact 3x3 area (difference between min and max must be <= 2)
            if (maxX - minX >= 3 || maxY - minY >= 3) {
                return false;
            }
        }
        return true;
    }

    /**
     * Places a card at the specified coordinate if allowed
     * @param coordinate the grid position where to place the card
     * @param card the card to be placed
     */
    public void putCard(GridPosition coordinate, Card card) {
        if (canPutCard(coordinate)) {
            cards.put(coordinate, card);
            lastAddedCardPosition = coordinate;
        }else {
            throw new IllegalArgumentException("Cannot place card at " + coordinate);
        }
    }

    /**
     * Checks if a card at the specified coordinate can be activated
     * @param coordinate the grid position to check
     * @return true if the card can be activated, false otherwise
     */
    public boolean canBeActivated(GridPosition coordinate) {
        Optional<Card> cardOpt = getCard(coordinate);
        if (cardOpt.isEmpty()) {
            return false;
        }
        if (activated.contains(coordinate)) { //already activated
            return false;
        }
        Card card = cardOpt.get();
        //we cannot add anything, the card is blocked
        if (!card.canPutResources(List.of())) {
            return false;
        }
        if(coordinate.getX() != lastAddedCardPosition.getX() && coordinate.getY() != lastAddedCardPosition.getY()){
            return false;
        }
        return true;
    }

    /**
     * Marks a card at the specified coordinate as activated
     * @param coordinate the grid position to activate
     */
    public void setActivated(GridPosition coordinate) {
        if (canBeActivated(coordinate)) {
            activated.add(coordinate);
        }
    }

    /**
     * Sets the activation pattern for the final activation phase
     * @param pattern collection of coordinate pairs representing the activation pattern
     */
    @Override
    public void setActivationPattern(Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern) {
        activationPattern.clear();
        for (AbstractMap.SimpleEntry<Integer, Integer> entry : pattern) {
            activationPattern.add(new GridPosition(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Ends the current turn by clearing all activated positions
     */
    public void endTurn(){
        activated.clear();
    }

    /**
     * Returns a string representation of the grid's current state
     * @return string describing occupied positions, activated positions, and activation pattern
     */
    public String state(){StringBuilder sb = new StringBuilder();
        sb.append("Grid {occupied: [");
        boolean first = true;
        for (GridPosition pos : cards.keySet()) {
            if (!first) sb.append(", ");
            sb.append("(").append(pos.getX()).append(",").append(pos.getY()).append(")");
            first = false;
        }
        sb.append("], activated: [");
        first = true;
        for (GridPosition pos : activated) {
            if (!first) sb.append(", ");
            sb.append("(").append(pos.getX()).append(",").append(pos.getY()).append(")");
            first = false;
        }
        sb.append("], pattern: [");
        first = true;
        for (GridPosition pos : activationPattern) {
            if (!first) sb.append(", ");
            sb.append("(").append(pos.getX()).append(",").append(pos.getY()).append(")");
            first = false;
        }
        sb.append("]}");
        return sb.toString();
    }
}