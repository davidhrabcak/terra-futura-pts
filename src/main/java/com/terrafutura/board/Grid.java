package main.java.com.terrafutura.board;

import main.java.com.terrafutura.cards.Card;

import java.util.List;
import java.util.Optional;

public class Grid {
    public Optional<Card> getCard(GridPosition coordinate) { return Optional.empty(); }
    public boolean canPutCard(GridPosition coordinate) { return false; }
    public void putCard(GridPosition coordinate, Card card) {}
    public boolean canBeActivated(GridPosition coordinate) { return false; }
    public void setActivated(GridPosition coordinate) {}
    public void setActivationPattern(List<GridPosition> patteren){}
    public void endTurn(){}
    public String state(){return "";}

}