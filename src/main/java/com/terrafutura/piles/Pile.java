package main.java.com.terrafutura.piles;

import main.java.com.terrafutura.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Pile {
    private List<Card> visibleCards;
    private List<Card> hiddenCards;
    public Pile(List<Card> cards){ //in beginning there should be 4 visible cards
        visibleCards = new ArrayList<>(cards.subList(0, 3));
        hiddenCards = new ArrayList<>(cards.subList(3, cards.size()));
    }
    public Optional<Card> getCard(int index) {
        if (index < 0 || index >= visibleCards.size()){
            return Optional.empty();
        }
        return Optional.empty(); }
    public void takeCard(int index) {}
    public void removeLastCard() {}
    public String state() { return ""; }
}