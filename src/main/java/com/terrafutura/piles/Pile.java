package main.java.com.terrafutura.piles;

import main.java.com.terrafutura.cards.Card;

import java.util.Optional;

public class Pile {
    public Optional<Card> getCard(int index) { return Optional.empty(); }
    public Optional<Card> takeCard(int index) {return Optional.empty(); }
    public boolean removeLastCard() {}
    public String state() { return ""; }
}