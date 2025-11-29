package test.java.piles;

import main.java.com.terrafutura.piles.Pile;
import main.java.com.terrafutura.cards.Card;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import main.java.com.terrafutura.cards.effects.ArbitraryInOut;

import javax.swing.text.html.Option;

class CardFake extends Card {
    private final String name;

    public CardFake(String name) {
        super(1, Optional.of(new ArbitraryInOut(1, 1)), Optional.empty());
        this.name = name;
    }

    @Override
    public String state() {
        return "[" + name + "]";
    }
}


public class PileTest {

    private CardFake c(String name) {
        return new CardFake(name);
    }

    @Test
    public void testInitializationTakesThreeCards() {
        Pile pile = new Pile(123, c("A"), c("B"), c("C"), c("D"), c("E"));

        assertNotEquals(Optional.empty(), pile.takeCard(3));
    }

    @Test
    public void testVisibleCardsOrderAfterInitialization() {
        Card A = c("A");
        Card B = c("B");
        Card C = c("C");
        Card D = c("D");
        Card E = c("E");

        Pile pile = new Pile(777, A, B, C, D, E);

        Optional<Card> first = pile.getCard(0);
        Optional<Card> second = pile.getCard(1);
        Optional<Card> third = pile.getCard(2);

        assertTrue(first.isPresent());
        assertTrue(second.isPresent());
        assertTrue(third.isPresent());

        // Check they are distinct
        assertNotEquals(first.get(), second.get());
        assertNotEquals(second.get(), third.get());
    }

    @Test
    public void testGetCardValidIndex() {
        Pile pile = new Pile(1, c("A"), c("B"), c("C"), c("D"));

        Optional<Card> c = pile.getCard(0);
        assertTrue(c.isPresent());
        assertTrue(c.get().state().startsWith("["));
    }

    @Test
    public void testGetCardInvalidIndex() {
        Pile pile = new Pile(1, c("A"), c("B"), c("C"), c("D"));

        assertEquals(Optional.empty(), pile.getCard(999));
        assertEquals(Optional.empty(), pile.getCard(4));
    }

    @Test
    public void testTakeCardRemovesFromVisible() {
        Pile pile = new Pile(2, c("A"), c("B"), c("C"), c("D"));

        Optional<Card> taken = pile.takeCard(1);

        assertTrue(taken.isPresent());
        assertEquals(Optional.empty(), pile.getCard(3));
    }

    @Test
    public void testRemoveLastCardFailsWhenPileEmpty() {
        Pile pile = new Pile(99, c("A"), c("B"), c("C"), c("D"));

        assertNotEquals(Optional.empty(), pile.getCard(3));
        assertFalse(pile.removeLastCard());
    }

    @Test
    public void testStateConcatenatesVisibleCardStates() {
        Pile pile = new Pile(5, c("A"), c("B"), c("C"), c("D"));

        String state = pile.state();

        assertTrue(state.startsWith("["));
        assertEquals(4, state.chars().filter(ch -> ch == '[').count());
    }
}
