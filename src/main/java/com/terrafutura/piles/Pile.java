package main.java.com.terrafutura.piles;

import main.java.com.terrafutura.cards.Card;

import java.util.Optional;

public class Pile {
    public Optional<Card> getCard(int index) { return Optional.empty(); }
    public void takeCard(int index) {}
    public void removeLastCard() {}
    public String state() { return ""; }
}