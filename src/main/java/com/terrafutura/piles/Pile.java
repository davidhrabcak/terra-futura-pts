package main.java.com.terrafutura.piles;

import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.resources.Resource;

import java.util.*;

public class Pile {
    private final List<Card> pile;
    List<Optional<Card>> visibleCards; // visibleCards are not in pile

    // actual constructor that would be used, there would also be a data class
    // that held all the cards in both decks
    public Pile(long seed) {
        Random r = new Random(seed);
        pile = new ArrayList<>();
        pile.add(new Card(List.of(Resource.Car), 0));
        pile.add(new Card(List.of(Resource.Gear), 0));
        pile.add(new Card(List.of(Resource.Money), 0));
        visibleCards = new ArrayList<>();
        Collections.shuffle(pile, r);
        initializePile();
    }

    public Pile() { //used for testing
        pile = new ArrayList<>(); // added two cards just for testing
        visibleCards = new ArrayList<>();
        initializePile();
    }

    public Pile(long seed, Card ... cards) {
        pile = new ArrayList<>(List.of(cards));
        Random r = new Random(seed);
        visibleCards = new ArrayList<>();
        Collections.shuffle(pile, r);
        initializePile();
    }

    private void initializePile() {
        for (int i = 0; i < 4; i++) {
            if (!pile.isEmpty()) {
                visibleCards.addFirst(Optional.of(pile.removeLast()));
            }
        }
    }

    public Optional<Card> getCard(int index) {
        if (index > visibleCards.size()-1) return Optional.empty();
        return visibleCards.get(index);
    }

    public Optional<Card> takeCard(int index) {
        if (index > visibleCards.size()-1 ) return Optional.empty();
        Optional<Card> removed = visibleCards.remove(index);
        if (pile.isEmpty()) visibleCards.add(Optional.empty());
        else {
            visibleCards.addFirst(Optional.of(pile.removeLast()));
        }
        return removed;
    }

    public boolean removeLastCard() {
        if (!visibleCards.isEmpty() && !pile.isEmpty()) {
            visibleCards.removeLast();
            visibleCards.addFirst(Optional.of(pile.removeLast()));
            return true;
        }
        return false;
    }

    public String state() {
        StringBuilder s = new StringBuilder();
        for (Optional<Card> c : visibleCards) {
            c.ifPresent(card -> s.append(card.state()));
        }
        return s.toString();
    }
}