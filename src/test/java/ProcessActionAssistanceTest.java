package test.java;

import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.cards.Pair;
import main.java.com.terrafutura.cards.ProcessActionAssistance;
import main.java.com.terrafutura.resources.Resource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Unit tests for ProcessActionAssistance class
 * Simplified rules version - assistance always returns false
 */
public class ProcessActionAssistanceTest {

    // Simple fake classes for testing
    private static class SimpleCard extends Card {
        public SimpleCard() {
            super(2, Optional.empty(), Optional.empty());
        }

        @Override
        public void putResources(List<Resource> resources) {}

        @Override
        public boolean canGetResources(List<Resource> resources) {
            return false;
        }

        @Override
        public List<Resource> getResources() {
            return new ArrayList<>();
        }

        @Override
        public String state() {
            return "SimpleCard";
        }
    }

    private static class SimpleGrid extends Grid {

        public SimpleGrid(Card startingCard) {
            super(startingCard);
        }

        @Override
        public boolean canPutCard(GridPosition position) {
            return true;
        }

    }

    @Test
    public void activateCard_AlwaysReturnsFalse_InSimplifiedRules() {

        ProcessActionAssistance paa = new ProcessActionAssistance();
        SimpleCard card = new SimpleCard();
        SimpleGrid grid = new SimpleGrid(card);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, new GridPosition(0, 0))
        );

        List<Pair<Resource, GridPosition>> outputs = List.of(
                new Pair<>(Resource.Red, new GridPosition(1, 0))
        );

        List<GridPosition> pollution = List.of(new GridPosition(0, 0));


        boolean result = paa.activateCard(card, grid, 1, card, inputs, outputs, pollution);


        assertFalse("ProcessActionAssistance should always return false in simplified rules", result);
    }

    @Test
    public void activateCard_WithNullParameters_ReturnsFalse() {

        ProcessActionAssistance paa = new ProcessActionAssistance();

        // all null parameters
        boolean result = paa.activateCard(null, null, 0, null, null, null, null);


        assertFalse("Should return false even with null parameters", result);
    }

    @Test
    public void activateCard_EmptyLists_ReturnsFalse() {

        ProcessActionAssistance paa = new ProcessActionAssistance();
        SimpleCard card = new SimpleCard();
        SimpleGrid grid = new SimpleGrid(card);

        boolean result = paa.activateCard(card, grid, 1, card,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());


        assertFalse("Should return false with empty lists", result);
    }

    @Test
    public void activateCard_DifferentAssistingPlayer_ReturnsFalse() {

        ProcessActionAssistance paa = new ProcessActionAssistance();
        SimpleCard card = new SimpleCard();
        SimpleGrid grid = new SimpleGrid(card);
        SimpleCard assistingCard = new SimpleCard();

        // with valid looking parameters
        boolean result = paa.activateCard(card, grid, 2, assistingCard,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());


        assertFalse("Should return false regardless of assisting player", result);
    }

}