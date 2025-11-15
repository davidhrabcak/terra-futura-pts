package main.java.com.terrafutura.piles;

public class CardSource {
    public Deck deck;
    public int index;

    public CardSource(Deck deck, int index) {
        this.index = index;
        this.deck = deck;
    }
}