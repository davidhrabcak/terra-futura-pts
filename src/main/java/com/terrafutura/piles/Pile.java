package main.java.com.terrafutura.piles;

import main.java.com.terrafutura.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * Represents a pile of cards with visible and hidden portions
 * Manages card distribution and removal according to game rules
 * Visible cards are displayed for player selection, hidden cards form the draw deck
 */
public class Pile {
    private final List<Card> visibleCards;
    private final List<Card> hiddenCards;
    public Pile(List<Card> cards){ //in beginning there should be 4 visible cards
        visibleCards = new ArrayList<>(cards.subList(0, 4));
        hiddenCards = new ArrayList<>(cards.subList(4, cards.size()));
    }

    /**
     * Retrieves a card from visible cards by index (1-based indexing)
     *
     * @param index Position of card (1 = newest, 4 = oldest)
     * @return Optional containing the card if the index is valid, empty otherwise
     */
    public Optional<Card> getCard(int index) {
        if (index < 1 || index >= visibleCards.size()){
            return Optional.empty();
        }
        return Optional.of(visibleCards.get(index - 1)); //indices are from 1 to 4

    }

    /**
     * Removes a card from visible cards and replenishes from hidden cards if available
     * Follows game rules: taken card is replaced with top card from hidden deck
     *
     * @param index Position of card to remove (1-based indexing)
     */
    public void takeCard(int index) {
        if (getCard(index).isPresent()) {
            visibleCards.remove(index - 1); //indices are from 1 to 4
            if(!hiddenCards.isEmpty()){
                visibleCards.add(hiddenCards.removeFirst());
            }
        }
    }

    public void removeLastCard() {
        if(!visibleCards.isEmpty()){
            takeCard(1); //this number will be lowered by one
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