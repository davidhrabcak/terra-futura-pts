package main.java.com.terrafutura.board;

import main.java.com.terrafutura.cards.Card;

import java.util.List;
import java.util.Optional;

public class Grid {
    public Optional<Card> putCard(GridPosition coordinate, Card card) { return Optional.empty(); }
    public boolean canPutCard(GridPosition coordinate) { return false; }

    public Optional<Card> getCard(GridPosition position) {
        return Optional.empty();
    }
}