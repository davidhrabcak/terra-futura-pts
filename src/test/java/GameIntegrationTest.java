package test.java;

import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.*;
import main.java.com.terrafutura.game.Game;
import main.java.com.terrafutura.game.GameObserver;
import main.java.com.terrafutura.game.GameState;
import main.java.com.terrafutura.piles.Pile;
import main.java.com.terrafutura.piles.CardSource;
import main.java.com.terrafutura.piles.Deck;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameIntegrationTest {
    // myslim ze tento integracny test pokryva vacsinu relevantnych netestovanych interakcii,
    // teda dalsi podla mna nebol treba

    private static class TestCard extends Card {
        private final String name;
        TestCard(String n) {
            super(List.of(), 0);
            name = n; }
        @Override public String state() { return "[" + name + "]"; }
    }


    private static class TestObserver extends GameObserver {
        public int notified = 0;
        TestObserver() { super(Map.of(0, _ -> {})); }
        @Override
        public void notifyAll(Map<Integer,String> m) { notified++; }
    }

    private void forceState(Game g, GameState st) {
        try {
            var f = Game.class.getDeclaredField("state");
            f.setAccessible(true);
            f.set(g, st);
        } catch (Exception ignored) {}
    }

    @Test
    public void testInitializeGame() {
        Game g = new Game(2, 0, List.of(new TestObserver()), 123);

        assertEquals(2, g.players.size());
        assertEquals(0, g.getOnTurn());
        assertEquals(1, g.getTurnNumber());
        System.out.println("Game: game initializes correctly");
    }


    @Test
    public void testTakeCardMovesFromPileToGrid() {
        TestObserver obs = new TestObserver();

        Game g = new Game(2, 0, List.of(obs), 999);

        TestCard c1 = new TestCard("A");
        TestCard c2 = new TestCard("B");
        TestCard c3 = new TestCard("C");
        TestCard c4 = new TestCard("D");
        TestCard c5 = new TestCard("E");

        Pile predictable = new Pile(1, c1, c2, c3, c4, c5);

        try {
            var f = Game.class.getDeclaredField("i");
            f.setAccessible(true);
            f.set(g, predictable);
        } catch (Exception e) {
            fail("failed");
        }


        forceState(g, GameState.TakeCardNoCardDiscarded);

        g.players.getFirst().g.beginTurn();

        g.players.getFirst().g.putCard(new GridPosition(0, 0), new TestCard("Root"));

        boolean ok = g.takeCard(0, new CardSource(Deck.I, 0), new GridPosition(1,0));
        assertTrue(ok);

        assertTrue(g.players.getFirst().g.getCard(new GridPosition(1,0)).isPresent());
        assertEquals("[D]", g.players.getFirst().g.getCard(new GridPosition(1,0)).get().state());
    }

    @Test
    public void testTakeCardStateProgression() {
        Game g = new Game(2, 0, List.of(new TestObserver()), 10);

        forceState(g, GameState.TakeCardNoCardDiscarded);
        g.players.getFirst().g.beginTurn();

        boolean ok = g.takeCard(0, new CardSource(Deck.I, 0), new GridPosition(0,0));

        assertTrue(ok);
        assertEquals(GameState.ActivateCard, g.getState());

        Game g1 = new Game(2, 0, List.of(new TestObserver()), 10);
        forceState(g1, GameState.TakeCardCardDiscarded);
        g1.players.getFirst().g.beginTurn();

        boolean correct = g1.takeCard(0, new CardSource(Deck.I, 0), new GridPosition(1, 0));

        assertTrue(correct);
        assertEquals(GameState.ActivateCard, g1.getState());
        System.out.println("Game: taking a card changes state correctly");
    }

    @Test
    public void testIllegalTakeCard() {
        Game g = new Game(2, 0, List.of(new TestObserver()), 10);

        // wrong state
        forceState(g, GameState.SelectScoringMethod);
        assertFalse(g.takeCard(0, new CardSource(Deck.I, 1), new GridPosition(0,0)));

        // wrong player
        forceState(g, GameState.TakeCardNoCardDiscarded);
        assertFalse(g.takeCard(1, new CardSource(Deck.I, 1), new GridPosition(0,0)));

        // wrong card index
        forceState(g, GameState.TakeCardNoCardDiscarded);
        assertFalse(g.takeCard(1, new CardSource(Deck.I, 4), new GridPosition(1, 0)));
        System.out.println("Game: takeCard cannot be performed in wrong state or by a different player");
    }

    @Test
    public void testGridPlacementRules() {
        Game g = new Game(2, 0, List.of(new TestObserver()), 15);

        forceState(g, GameState.TakeCardNoCardDiscarded);
        g.players.getFirst().g.beginTurn();

        assertTrue(g.takeCard(0, new CardSource(Deck.I, 0), new GridPosition(0,0)));

        forceState(g, GameState.TakeCardNoCardDiscarded);
        assertFalse(g.takeCard(0, new CardSource(Deck.I, 0), new GridPosition(1,1)));

        forceState(g, GameState.TakeCardNoCardDiscarded);
        assertFalse(g.takeCard(0, new CardSource(Deck.I, 0), new GridPosition(0, 1)));
        System.out.println("Game: grid placement restrictions are functional");
    }

    @Test
    public void testDiscardCardFlow() {
        Game g = new Game(2, 0, List.of(new TestObserver()), 7);

        TestCard c1 = new TestCard("A");
        TestCard c2 = new TestCard("B");
        TestCard c3 = new TestCard("C");
        TestCard c4 = new TestCard("D");
        TestCard c5 = new TestCard("E");

        Pile predictable = new Pile(1, c1, c2, c3, c4, c5);

        try {
            var f = Game.class.getDeclaredField("i");
            f.setAccessible(true);
            f.set(g, predictable);
        } catch (Exception e) {
            fail("failed");
        }

        forceState(g, GameState.TakeCardNoCardDiscarded);

        assertTrue(g.discardLastCardFromDeck(0, Deck.I));
        assertEquals(GameState.TakeCardCardDiscarded, g.getState());


        assertFalse(g.discardLastCardFromDeck(0, Deck.II));
        System.out.println("Game: discard changes state correctly");
    }


    @Test
    public void testTurnRotation() {
        TestObserver obs = new TestObserver();
        Game g = new Game(3, 0, List.of(obs), 500);

        assertEquals(0, g.getOnTurn());

        assertTrue(g.turnFinished(0));
        assertEquals(1, g.getOnTurn());

        assertTrue(g.turnFinished(1));
        assertEquals(2, g.getOnTurn());

        assertTrue(g.turnFinished(2));
        assertEquals(0, g.getOnTurn());
        assertEquals(2, g.getTurnNumber());
        System.out.println("Game: players rotate correctly when turn ends");
    }

    @Test
    public void testFullTurnFlow() {
        Game g = new Game(2, 0, List.of(new TestObserver()), 11);

        g.players.getFirst().g.beginTurn();
        g.players.getFirst().g.putCard(new GridPosition(0,0), new TestCard("Root"));
        forceState(g, GameState.TakeCardNoCardDiscarded);

        // take card
        assertTrue(g.takeCard(0, new CardSource(Deck.I, 1), new GridPosition(1,0)));
        assertEquals(GameState.ActivateCard, g.getState());

        // fake activate fails
        boolean activated = g.activateCard(
                0,
                new GridPosition(1,0),
                List.of(),
                List.of(),
                List.of(),
                Optional.empty(),
                Optional.empty()
        );
        assertFalse(activated);

        // discard fails after card placement
        assertFalse(g.discardLastCardFromDeck(0,Deck.I));

        // finish turn
        assertTrue(g.turnFinished(0));
        assertEquals(1, g.getOnTurn());

        // finish turn of other player
        g.turnFinished(1);
        assertEquals(0, g.getOnTurn());
        assertEquals(2, g.getTurnNumber());
        System.out.println("Game: full turn was executed and all involved methods are functional," +
                " turn counter increments correctly");
    }
}
