package main.java.com.terrafutura.piles;

import main.java.com.terrafutura.cards.Card;

import java.util.*;

public class Pile {
    private final List<Card> pile;
    List<Optional<Card>> visibleCards; // visibleCards are not in pile anymore

    public Pile() { // for testing
        pile = new ArrayList<>();
        visibleCards = new ArrayList<>();
        Collections.shuffle(pile);
        initializePile();
    }

    public Pile(Card ... cards) {
        pile = new ArrayList<>(List.of(cards));
        visibleCards = new ArrayList<>();
        Collections.shuffle(pile);
        initializePile();
    }

    private void initializePile() {
        for (int i = 0; i < 3; i++) {
            visibleCards.addFirst(Optional.of(pile.removeLast()));
        }
    }

    public Optional<Card> getCard(int index) {
        if (index > visibleCards.size()) return Optional.empty();
        return visibleCards.get(index);
    }

    public Optional<Card> takeCard(int index) {
        if (index > visibleCards.size()) return Optional.empty();
        return visibleCards.remove(index);
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