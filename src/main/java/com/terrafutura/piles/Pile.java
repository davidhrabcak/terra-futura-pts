package main.java.com.terrafutura.piles;

import main.java.com.terrafutura.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Pile {
    private final List<Card> visibleCards;
    private final List<Card> hiddenCards;
    public Pile(List<Card> cards){ //in beginning there should be 4 visible cards
        visibleCards = new ArrayList<>(cards.subList(0, 4));
        hiddenCards = new ArrayList<>(cards.subList(3, cards.size()));
    }
    public Optional<Card> getCard(int index) {
        if (index < 0 || index >= visibleCards.size()){
            return Optional.empty();
        }
        return Optional.of(visibleCards.get(index));

    }
    public void takeCard(int index) {
        if (getCard(index).isPresent()) {
            visibleCards.remove(index);
            if(!hiddenCards.isEmpty()){
                visibleCards.add(hiddenCards.removeFirst());
            }
        }
    }

    public void removeLastCard() {
        if(!visibleCards.isEmpty()){
            takeCard(0);
        }
    }
    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("visible cards:[");
        for (Card c : visibleCards) {
            sb.append(c.state()).append(", ");
        }
        sb.append("], hidden cards: ").append(hiddenCards.size());
        return sb.toString();
    }
}