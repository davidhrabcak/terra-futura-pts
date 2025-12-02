package main.java.com.terrafutura.board;

import main.java.com.terrafutura.cards.Card;

import java.util.List;
import java.util.Optional;

public class Grid {
    private Card startingCard;

    public Grid(Card startingCard){
        this.startingCard = startingCard;
    }

    public void putCard(GridPosition coordinate, Card card) { }
    public boolean canPutCard(GridPosition coordinate) { return false; }
    public Optional<Card> getCard(GridPosition position) {
        return Optional.empty();
    }
    public boolean canBeActivated(GridPosition coordinate) { return false; }
    public void setActivated(GridPosition coordinate) {}
    public void setActivationPattern(List<GridPosition> pattern) {}
    public void endTurn() {}
    public String state() { return ""; }

}