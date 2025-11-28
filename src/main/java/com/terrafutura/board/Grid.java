package main.java.com.terrafutura.board;

import main.java.com.terrafutura.cards.Card;

import java.util.*;

public class Grid {
    private final Map<GridPosition, Card> cards;
    private Set<GridPosition> activated;
    private List<GridPosition> activationPattern;

    public Grid(Card startingCard) {
        cards = new HashMap<>();
        cards.put(new GridPosition(0,0), startingCard);
        activated = new HashSet<>();
        activationPattern = new ArrayList<>();
    }

    public Optional<Card> getCard(GridPosition coordinate) {
        if (cards.containsKey(coordinate)) {
            return Optional.of(cards.get(coordinate));
        }
        return Optional.empty();
    }

    public boolean canPutCard(GridPosition coordinate) {
        if(coordinate.getX() < - 2 || coordinate.getX() > 2 || coordinate.getY() < -2 || coordinate.getY() > 2){ //wrong position
            return false;
        }

        if(cards.containsKey(coordinate)){ //card already exists
            return false;
        }

        for (GridPosition gridPosition: cards.keySet()) { //for every card in grid
            List<GridPosition> neighbours = new ArrayList<>(Arrays.asList(
                    new GridPosition(gridPosition.getX(), gridPosition.getY() + 1),
                    new GridPosition(gridPosition.getX() + 1, gridPosition.getY()),
                    new GridPosition(gridPosition.getX(), gridPosition.getY() - 1),
                    new GridPosition(gridPosition.getX() - 1, gridPosition.getY())));
            for (GridPosition neighbour: neighbours) { // check all neighbours of that card
                if(neighbour.equals(coordinate)){ //if neighbour is the same as the card we want to put
                    return true;
                    //neighbour's place is not occupied, so we can put our card there
                }
            }
        }
        return false;
    }
    public void putCard(GridPosition coordinate, Card card) {
        if (canPutCard(coordinate)) {
            cards.put(coordinate, card);
        }
    }
    public boolean canBeActivated(GridPosition coordinate) {
        Optional<Card> card = getCard(coordinate);
        if (card.isEmpty()) {
            return false;
        }
        if (activated.contains(coordinate)) {
            return false;
        }
        //what about pollution
        return true;
    }
    public void setActivated(GridPosition coordinate) {
        if (canBeActivated(coordinate)) {
            activated.add(coordinate);
        }
    }
    public void setActivationPattern(List<GridPosition> patteren){
        activationPattern = patteren;
    }
    public void endTurn(){
        activated.clear();
    }
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