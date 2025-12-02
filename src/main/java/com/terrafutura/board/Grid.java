package main.java.com.terrafutura.board;

import main.java.com.terrafutura.cards.Card;

import java.util.*;

public class Grid implements InterfaceActivateGrid {
    private final HashMap<GridPosition, Optional<Card>> grid;
    private final Set<GridPosition> activated;
    private ActivationPattern activationPattern;
    private boolean onTurn;
    private boolean empty = true;

    public Grid() {
        grid = new HashMap<>(9);
        activated = new HashSet<>(9);

    }

    public void beginTurn() {
        onTurn = true;
    }

    public ActivationPattern getActivationPattern() {
        if (onTurn) {
            return activationPattern;
        } else return null;
    }

    public void putCard(GridPosition coordinate, Card card) {
        if (onTurn && canPutCard(coordinate)) {
            grid.putIfAbsent(coordinate, Optional.of(card));
        }
    }

    public boolean canPutCard(GridPosition coordinate) {
        if (!onTurn) return false;
        if (empty) {
            empty = false;
            return true;
        }
        if (getCard(new GridPosition(coordinate.getX(), coordinate.getY() + 1)).isPresent()
                || getCard(new GridPosition(coordinate.getX() + 1, coordinate.getY())).isPresent()
                || getCard(new GridPosition(coordinate.getX() - 1, coordinate.getY())).isPresent()
                || getCard(new GridPosition(coordinate.getX(), coordinate.getY() - 1)).isPresent()) {

            Optional<Card> res = grid.getOrDefault(coordinate, Optional.empty());
            return res.isEmpty();
        } else return false;
    }

    public Optional<Card> getCard(GridPosition coordinate) {
        if (!onTurn) {
            return Optional.empty();
        }
        if (coordinate.getX() <= 2 || coordinate.getX() >= -2 || coordinate.getY() <= 2 || coordinate.getY() >= -2) {
            return grid.getOrDefault(coordinate, Optional.empty());
        }
        return Optional.empty();
    }

    public boolean canBeActivated(GridPosition coordinate) {
        if (!onTurn) return false;
        return !activated.contains(coordinate);
    }

    public void setActivated(GridPosition coordinate) { // for testing
        if (onTurn) {
            activated.add(coordinate);
        }
    }

    @Override
    public void setActivationPattern(Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern) {
        activationPattern = new ActivationPattern(this, pattern);
        activationPattern.select();
    }

    public void endTurn() {
        onTurn = false;
    }

    public String state() {
        Set<GridPosition> positions = grid.keySet();

        if (positions.isEmpty()) { // no cards
            return ". . .\n. . .\n. . .";
        }

        // bounding box of placed cards
        int minX = positions.stream().mapToInt(GridPosition::getX).min().orElse(0);
        int maxX = positions.stream().mapToInt(GridPosition::getX).max().orElse(0);
        int minY = positions.stream().mapToInt(GridPosition::getY).min().orElse(0);
        int maxY = positions.stream().mapToInt(GridPosition::getY).max().orElse(0);

        // bounding box smaller than 3×3
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;

        /* this check should also be in canPutCard, but I didn't find a good way to implement it...
         so the implementation relies on the GameObserver to print the grid after every grid operation, else
         the player could create a grid that is not allowed by the rules. Also, if a player does an invalid
         card placement, there is currently no way to undo it and he basically lost. */
        if (width > 3 || height > 3) {
            throw new IllegalStateException("More than a 3×3 area cannot be displayed");
        }

        StringBuilder sb = new StringBuilder();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                GridPosition pos = new GridPosition(x, y);
                Optional<Card> opt = grid.getOrDefault(pos, Optional.empty());
                sb.append(opt.map(Card::state).orElse("."));
                if (x < maxX) sb.append(" ");
            }
            if (y < maxY) sb.append("\n");
        }

        return sb.toString();
    }
}