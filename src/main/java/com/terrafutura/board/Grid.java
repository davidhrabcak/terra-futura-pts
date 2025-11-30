package main.java.com.terrafutura.board;

import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.cards.MoveCard;
import main.java.com.terrafutura.resources.Resource;

import java.util.*;

public class Grid implements InterfaceActivateGrid{
    private final HashMap<GridPosition, Optional<Card>> grid;
    private final Set<GridPosition> activated;
    private ActivationPattern activationPattern;
    private boolean onTurn;

    public Grid() {
        grid = new HashMap<>(9);
        activated = new HashSet<>(9);

    }

    public void beginTurn() {
        onTurn = true;
    }

    public void putCard(GridPosition coordinate, Card card) {
        if (onTurn) {
            grid.putIfAbsent(coordinate, Optional.of(card));
        }
    }

    public boolean canPutCard(GridPosition coordinate) {
        if (!onTurn) return false;
        if (getCard(new GridPosition(coordinate.getX(), coordinate.getY()+1)).isPresent()
            || getCard(new GridPosition(coordinate.getX()+1, coordinate.getY())).isPresent()
            || getCard(new GridPosition(coordinate.getX()-1, coordinate.getY())).isPresent()
            || getCard(new GridPosition(coordinate.getX(), coordinate.getY()-1)).isPresent()) {

            Optional<Card> res = grid.getOrDefault(coordinate, Optional.empty());
            return res.isEmpty();
        } else return false;
    }

    public Optional<Card> getCard(GridPosition coordinate) {
        if (!onTurn) {
            System.err.println("Player not on turn");
            return Optional.empty();
        }
        return grid.getOrDefault(coordinate, Optional.empty());
    }

    public boolean canBeActivated(GridPosition coordinate) {
        if (!onTurn) return false;
        return !activated.contains(coordinate);
    }

    public void setActivated(GridPosition coordinate) {
        if (onTurn) {
            activated.add(coordinate);
        }
    }

    @Override
    public void setActivationPattern(Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern) {
        activationPattern = new ActivationPattern(this, pattern);
    }

    public void endTurn() {
        onTurn = false;
    }

    public String state() {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                GridPosition pos = new GridPosition(x, y);
                Optional<Card> opt = grid.getOrDefault(pos, Optional.empty());

                String cell = opt.map(Card::state).orElse(".");
                sb.append(cell);

                if (x < 2) sb.append(" ");
            }
            if (y < 2) sb.append("\n");
        }

        return sb.toString();
    }
}