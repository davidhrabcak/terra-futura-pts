package test.java;

import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.cards.effects.ArbitraryInOut;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Optional;

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

public class GridTest {

    private CardFake c(String n) { return new CardFake(n); }

    @Test
    public void testGridOperationsOutsideOfTurn() {
        Grid grid = new Grid();

        grid.beginTurn();
        grid.putCard(new GridPosition(1, 1), c("Card"));
        grid.endTurn();


        assertFalse(grid.canPutCard(new GridPosition(0,0)));
        assertFalse(grid.canBeActivated(new GridPosition(1, 1)));
        assertNull(grid.getActivationPattern());
        assertEquals(Optional.empty(), grid.getCard(new GridPosition(1, 1)));
    }

    @Test
    public void testPutCardPutsCardOnRightPosition() {
        Grid grid = new Grid();

        grid.beginTurn();
        grid.putCard(new GridPosition(0, 0), c("Card"));
        assertTrue(grid.getCard(new GridPosition(0, 0)).isPresent());
        assertEquals("[Card]", grid.getCard(new GridPosition(0, 0)).get().state());
    }

    @Test
    public void testPutCardCantPutCardOnOccupiedPosition() {
        Grid grid = new Grid();

        grid.beginTurn();
        grid.putCard(new GridPosition(0, 0), c("Card"));
        assertNotEquals(Optional.empty(), grid.getCard(new GridPosition(0, 0)));
        grid.putCard(new GridPosition(0,0), c("Different Card"));
        assertTrue(grid.getCard(new GridPosition(0, 0)).isPresent());
        assertEquals("[Card]", grid.getCard(new GridPosition(0, 0)).get().state());
    }

    @Test
    public void testCanPutCard() {
        Grid grid = new Grid();

        grid.beginTurn();
        grid.putCard(new GridPosition(0, 0), c("Card"));
        assertTrue(grid.canPutCard(new GridPosition(0, 1)));
        assertFalse(grid.canPutCard(new GridPosition(0, 0)));
        assertFalse(grid.canPutCard(new GridPosition(1, 1)));
    }

    @Test
    public void testGetCard() {
        Grid grid = new Grid();

        grid.beginTurn();
        grid.putCard(new GridPosition(0, 0), c("Card"));

        assertEquals(Optional.empty(), grid.getCard(new GridPosition(1, 1)));
        assertTrue(grid.getCard(new GridPosition(0, 0)).isPresent());
        assertEquals("[Card]", grid.getCard(new GridPosition(0, 0)).get().state());
    }

    @Test
    public void testCanBeActivated() {
        Grid grid = new Grid();

        grid.beginTurn();
        grid.putCard(new GridPosition(0, 0), c("Card"));

        assertTrue(grid.canBeActivated(new GridPosition(0, 0)));
        grid.setActivated(new GridPosition(0, 0));

        assertFalse(grid.canBeActivated(new GridPosition(0, 0)));
    }

    @Test
    public void testEmptyGridState() {
        Grid grid = new Grid();

        String expected =
                ". . .\n" +
                ". . .\n" +
                ". . .";

        assertEquals(expected, grid.state());
    }

    @Test
    public void testSingleCardAtCenter() {
        Grid grid = new Grid();
        grid.beginTurn();
        grid.putCard(new GridPosition(0, 0), c("A"));

        String expected =
                "[A]";

        assertEquals(expected, grid.state());
    }

    @Test
    public void testTwoCardsInSameRow() {
        Grid grid = new Grid();
        grid.beginTurn();
        grid.putCard(new GridPosition(0, 0), c("A"));
        grid.putCard(new GridPosition(1, 0), c("B"));

        String expected =
                "[A] [B]";

        assertEquals(expected, grid.state());
    }

    @Test
    public void test2x2Block() {
        Grid grid = new Grid();
        grid.beginTurn();
        grid.putCard(new GridPosition(0, 0), c("A"));
        grid.putCard(new GridPosition(1, 0), c("B"));
        grid.putCard(new GridPosition(0, 1), c("C"));
        grid.putCard(new GridPosition(1, 1), c("D"));

        String expected =
                "[A] [B]\n" +
                        "[C] [D]";

        assertEquals(expected, grid.state());
    }

    @Test
    public void testBoundingBoxShrinksAroundCards() {
        Grid grid = new Grid();
        grid.beginTurn();
        grid.putCard(new GridPosition(1, 1), c("A"));
        grid.putCard(new GridPosition(1, 2), c("B"));

        String expected =
                "[A]\n" +
                        "[B]";

        assertEquals(expected, grid.state());
    }

    @Test
    public void testCardsAtNegativeCoordinates() {
        Grid grid = new Grid();
        grid.beginTurn();
        grid.putCard(new GridPosition(-2, -1), c("X"));
        grid.putCard(new GridPosition(-1, -1), c("Y"));

        String expected =
                "[X] [Y]";

        assertEquals(expected, grid.state());
    }
}
