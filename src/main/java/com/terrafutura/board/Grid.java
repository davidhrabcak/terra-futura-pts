package main.java.com.terrafutura.board;

import main.java.com.terrafutura.cards.Card;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Optional;

public class Grid implements InterfaceActivateGrid {
    public Optional<Card> putCard(GridPosition coordinate, Card card) { return Optional.empty(); }
    public boolean canPutCard(GridPosition coordinate) { return false; }

    @Override
    public void setActivationPattern(Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern) {

    }
}