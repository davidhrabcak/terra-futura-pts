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


    /**
     * Constructs a Pile from a list of cards
     * First 4 cards become visible, in order: [oldest, ..., newest]
     */
    public Pile(List<Card> cards){ //in beginning there should be 4 visible cards
        int visible = Math.min(4, cards.size());
        visibleCards = new ArrayList<>(cards.subList(0, visible));
        hiddenCards = new ArrayList<>(cards.subList(visible, cards.size()));
    }

    /**
     * Retrieves a card from visible cards by index (1-based indexing)
     *
     * @param index Position of card (0 = card from hidden cards, 1 = newest, 4 = oldest)
     * @return Optional containing the card if the index is valid, empty otherwise
     */
    public Optional<Card> getCard(int index) {
        if (index == 0 && !hiddenCards.isEmpty()) {
            return Optional.of(hiddenCards.removeFirst());
        }

        if (index < 1 || index > visibleCards.size()) {
            return Optional.empty();
        }

        int actualIndex = visibleCards.size() - index;
        return Optional.of(visibleCards.get(actualIndex));

    }

    /**
     * Removes a card from visible cards and replenishes from hidden cards if available
     * or takes card from top of hidden deck if possible.
     * Follows game rules: taken card is replaced with the top card from the hidden deck
     *
     * @param index Position of card to remove (1-based indexing)
     */
    public void takeCard(int index) {
        if (getCard(index).isPresent()) {
            if (index == 0) {
                hiddenCards.removeFirst();
            }
            else{
                int actualIndex = visibleCards.size() - index;
                visibleCards.remove(actualIndex);
                if (!hiddenCards.isEmpty()) {
                    visibleCards.addLast(hiddenCards.removeFirst());
                }
            }
        }else {
            throw new IllegalArgumentException("Card index out of bounds");
        }
    }
    /**
     * Removes the oldest card (index 4 in a pile, index 0 in a list) - discard action
     */
    public void removeLastCard() {
        if(!visibleCards.isEmpty()){
            takeCard(4);
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