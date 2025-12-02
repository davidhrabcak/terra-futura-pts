package test.java;

import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.ActionHelper;
import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.cards.Pair;
import main.java.com.terrafutura.resources.Resource;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for ActionHelper class
 * Tests all validation logic used by ProcessAction and ProcessActionAssistance
 */
public class ActionHelperTest {

    private ActionHelper actionHelper;
    private FakeGrid fakeGrid;
    private FakeCard validCard1;
    private FakeCard validCard2;

    // Fake Card implementation for testing
    private static class FakeCard extends Card {
        private final List<Resource> resources = new ArrayList<>();
        private final int pollutionSpaces;

        public FakeCard(int pollutionSpaces) {
            super(pollutionSpaces, Optional.empty(), Optional.empty());
            this.pollutionSpaces = pollutionSpaces;
        }

        public void addResource(Resource resource) {
            resources.add(resource);
        }

        @Override
        public void putResources(List<Resource> resourcesToAdd) {
            resources.addAll(resourcesToAdd);
        }

        @Override
        public boolean canPutResources(List<Resource> resourcesToAdd) {
            long currentPollution = resources.stream()
                    .filter(r -> r == Resource.Pollution)
                    .count();
            if (currentPollution > pollutionSpaces) {
                return false;
            }
            long newPollution = resourcesToAdd.stream()
                    .filter(r -> r == Resource.Pollution)
                    .count();

            return currentPollution + newPollution <= pollutionSpaces;
        }

        @Override
        public boolean canGetResources(List<Resource> resourcesToGet) {
            // Need to check if card has all required resources with proper count
            List<Resource> copy = new ArrayList<>(resources);
            for (Resource resource : resourcesToGet) {
                if (!copy.remove(resource)) {
                    return false;
                }
            }
            return true;
        }


        @Override
        public void removeResource(Resource resource) {
            resources.remove(resource);
        }

        @Override
        public List<Resource> getResources() {
            return new ArrayList<>(resources);
        }

        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            return true;
        }

        @Override
        public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
            return true;
        }

        @Override
        public String state() {
            return "FakeCard";
        }
    }

    // Fake Grid implementation
    private static class FakeGrid extends Grid {
        private final Map<GridPosition, Card> cards = new HashMap<>();

        public FakeGrid(Card startingCard) {
            super(startingCard);
        }


        @Override
        public Optional<Card> getCard(GridPosition position) {
            return Optional.ofNullable(cards.get(position));
        }

        @Override
        public boolean canPutCard(GridPosition position) {
            // canPutCard returns true if position is empty
            return !cards.containsKey(position);
        }

        @Override
        public void putCard(GridPosition position, Card card) {
            cards.put(position, card);
        }

        @Override
        public boolean canBeActivated(GridPosition coordinate) {
            return true;
        }

        @Override
        public String state() {
            return "FakeGrid";
        }

    }

    @Before
    public void setUp() {
        actionHelper = new ActionHelper();
        fakeGrid = new FakeGrid(new FakeCard(1));

        // Create test cards
        validCard1 = new FakeCard(3);
        validCard2 = new FakeCard(3);
        FakeCard fullPollutionCard = new FakeCard(1);
        fullPollutionCard.addResource(Resource.Pollution); // Card is now full

        // Place cards in grid at valid positions (avoid (0,0) which is starting card)
        GridPosition card1Pos = new GridPosition(1, 0);
        GridPosition card2Pos = new GridPosition(1, 1);
        GridPosition card3Pos = new GridPosition(-1, -1);

        fakeGrid.putCard(card1Pos, validCard1);
        fakeGrid.putCard(card2Pos, validCard2);
        fakeGrid.putCard(card3Pos, fullPollutionCard);
    }

    // Test 1: nullEntry method tests
    @Test
    public void nullEntry_AllParametersNull_ReturnsTrue() {
        assertTrue("All null parameters should return true",
                actionHelper.nullEntry(null, null, null, null, null));
    }

    @Test
    public void nullEntry_SomeParametersNull_ReturnsTrue() {
        assertTrue("Some null parameters should return true",
                actionHelper.nullEntry(fakeGrid, null, new ArrayList<>(), null, null));
    }

    @Test
    public void nullEntry_NoNullParameters_ReturnsFalse() {
        assertFalse("No null parameters should return false",
                actionHelper.nullEntry(fakeGrid, validCard1,
                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    public void nullEntry_NullGridOnly_ReturnsTrue() {
        assertTrue("Null grid only should return true",
                actionHelper.nullEntry(null, validCard2,
                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    public void nullEntry_NullCardOnly_ReturnsTrue() {
        assertTrue("Null card only should return true",
                actionHelper.nullEntry(fakeGrid, null,
                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    // Test 2: validateInputs method tests
    @Test
    public void validateInputs_EmptyPosition_ReturnsFalse() {
        // position with no card
        GridPosition emptyPos = new GridPosition(2, 2);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, emptyPos)
        );

        boolean result = actionHelper.validateInputs(inputs, fakeGrid);

        assertFalse("Empty position should return false", result);
    }

    @Test
    public void validateInputs_CardCannotProvideResource_ReturnsFalse() {
        GridPosition validPos = new GridPosition(1, 1);
        // Card does NOT have Red resource

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Red, validPos)
        );

        boolean result = actionHelper.validateInputs(inputs, fakeGrid);

        assertFalse("Card without required resource should return false", result);
    }

    @Test
    public void validateInputs_ValidInput_ReturnsTrue() {
        // Add resource to card first
        GridPosition validPos = new GridPosition(1, 1);
        validCard2.addResource(Resource.Red);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Red, validPos)
        );

        boolean result = actionHelper.validateInputs(inputs, fakeGrid);

        assertTrue("Card with required resource should return true", result);
    }

    @Test
    public void validateOutputs_CardCannotAcceptResource_ReturnsFalse() {
        GridPosition fullCardPos = new GridPosition(-1, -1);
        // fullPollutionCard has pollution capacity of 1 and is already full

        List<Pair<Resource, GridPosition>> outputs = List.of(
                new Pair<>(Resource.Pollution, fullCardPos) // Trying to add more pollution
        );

        boolean result = actionHelper.validateOutputs(outputs, fakeGrid);

        assertFalse("Card that cannot accept resource should return false", result);
    }

    @Test
    public void validateOutputs_EmptyPosition_ReturnsFalse() {
        GridPosition emptyPos = new GridPosition(2, 2);

        List<Pair<Resource, GridPosition>> outputs = List.of(
                new Pair<>(Resource.Green, emptyPos)
        );

        boolean result = actionHelper.validateOutputs(outputs, fakeGrid);

        assertFalse("Empty position should return false", result);
    }

    @Test
    public void validateOutputs_ValidOutput_ReturnsTrue() {
        // Use validCard1 at position (1, 0) which has capacity for more resources
        GridPosition validPos = new GridPosition(1, 0);

        List<Pair<Resource, GridPosition>> outputs = List.of(
                new Pair<>(Resource.Green, validPos)
        );

        boolean result = actionHelper.validateOutputs(outputs, fakeGrid);

        assertTrue("Valid output should return true", result);
    }

    @Test
    public void validatePollution_CardCannotAcceptMorePollution_ReturnsFalse() {
        GridPosition fullCardPos = new GridPosition(-1, -1);
        // fullPollutionCard already has 1 pollution and capacity is 1

        List<GridPosition> pollution = List.of(fullCardPos);

        boolean result = actionHelper.validatePollution(pollution, fakeGrid);

        assertFalse("Card that cannot accept more pollution should return false", result);
    }

    @Test
    public void validatePollution_MultiplePollutionExceedsCapacity_ReturnsFalse() {
        // card with capacity 1, no pollution yet
        FakeCard smallCard = new FakeCard(1); // Capacity 1
        GridPosition smallCardPos = new GridPosition(2, 0);
        fakeGrid.putCard(smallCardPos, smallCard);

        List<GridPosition> pollution = Arrays.asList(smallCardPos, smallCardPos); // 2 pollution

        boolean result = actionHelper.validatePollution(pollution, fakeGrid);

        assertFalse("Pollution exceeding capacity should return false", result);
    }

    @Test
    public void validatePollution_EmptyPosition_ReturnsFalse() {
        GridPosition emptyPos = new GridPosition(2, 2);

        List<GridPosition> pollution = List.of(emptyPos);

        boolean result = actionHelper.validatePollution(pollution, fakeGrid);

        assertFalse("Empty pollution position should return false", result);
    }

    @Test
    public void validatePollution_EmptyPollutionList_ReturnsTrue() {
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = actionHelper.validatePollution(pollution, fakeGrid);

        assertTrue("Empty pollution list should return true", result);
    }

    @Test
    public void validatePollution_ValidPollutionOnEmptyCard_ReturnsTrue() {
        // card with capacity 2, no pollution yet
        FakeCard emptyCard = new FakeCard(2);
        GridPosition emptyCardPos = new GridPosition(2, 1);
        fakeGrid.putCard(emptyCardPos, emptyCard);

        List<GridPosition> pollution = List.of(emptyCardPos); // 1 pollution

        boolean result = actionHelper.validatePollution(pollution, fakeGrid);

        assertTrue("Valid pollution on empty card should return true", result);
    }

    @Test
    public void validTransaction_UpperEffectAccepts_ReturnsTrue() {
        FakeCard acceptingCard = new FakeCard(2);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, new GridPosition(-1, 0))
        );

        List<Pair<Resource, GridPosition>> outputs = List.of(
                new Pair<>(Resource.Red, new GridPosition(2, 0))
        );

        List<GridPosition> pollution = List.of(new GridPosition(1, 0));

        //test upper effect (true)
        boolean result = actionHelper.validTransaction(acceptingCard, inputs, outputs, pollution, true);

        assertTrue("Upper effect accepting should return true", result);
    }

    @Test
    public void validTransaction_UpperEffectRejectsLowerAccepts_ReturnsTrue() {
        // card where upper effect rejects but lower accepts
        FakeCard card = new FakeCard(2) {
            @Override
            public boolean check(List<Resource> input, List<Resource> output, int pollution) {
                return false;
            }

            @Override
            public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
                return true;
            }
        };

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, new GridPosition(-1, 0))
        );

        List<Pair<Resource, GridPosition>> outputs = List.of(
                new Pair<>(Resource.Red, new GridPosition(2, 0))
        );

        List<GridPosition> pollution = List.of(new GridPosition(1, 0));

        // test lower effect (false parameter)
        boolean result = actionHelper.validTransaction(card, inputs, outputs, pollution, false);

        assertTrue("Lower effect accepting should return true", result);
    }

    @Test
    public void validTransaction_BothEffectsReject_ReturnsFalse() {
        // card where both effects reject
        FakeCard rejectingCard = new FakeCard(2) {
            @Override
            public boolean check(List<Resource> input, List<Resource> output, int pollution) {
                return false;
            }

            @Override
            public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
                return false;
            }
        };

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, new GridPosition(-1, 0))
        );

        List<Pair<Resource, GridPosition>> outputs = List.of(
                new Pair<>(Resource.Red, new GridPosition(2, 0))
        );

        List<GridPosition> pollution = List.of(new GridPosition(1, 0));

        // test upper effect
        boolean result = actionHelper.validTransaction(rejectingCard, inputs, outputs, pollution, true);

        assertFalse("Both effects rejecting should return false", result);
    }

    @Test
    public void validTransaction_EmptyTransaction_ReturnsTrueIfCardAccepts() {
        FakeCard acceptingCard = new FakeCard(2);

        List<Pair<Resource, GridPosition>> inputs = new ArrayList<>();
        List<Pair<Resource, GridPosition>> outputs = new ArrayList<>();
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = actionHelper.validTransaction(acceptingCard, inputs, outputs, pollution, true);

        assertTrue("Empty transaction with accepting card should return true", result);
    }

    @Test
    public void extractResources_SinglePair_ReturnsSingleResource() {
        List<Pair<Resource, GridPosition>> pairs = List.of(
                new Pair<>(Resource.Green, new GridPosition(-1, 0))
        );

        List<Resource> resources = actionHelper.extractResources(pairs);

        assertEquals("Should extract 1 resource", 1, resources.size());
        assertEquals("Extracted resource should be Green", Resource.Green, resources.getFirst());
    }

    @Test
    public void extractResources_MultiplePairs_ReturnsAllResources() {
        List<Pair<Resource, GridPosition>> pairs = Arrays.asList(
                new Pair<>(Resource.Green, new GridPosition(-1, 0)),
                new Pair<>(Resource.Red, new GridPosition(2, 0)),
                new Pair<>(Resource.Yellow, new GridPosition(1, 1))
        );

        List<Resource> resources = actionHelper.extractResources(pairs);

        assertEquals("Should extract 3 resources", 3, resources.size());
        assertTrue("Should contain Green", resources.contains(Resource.Green));
        assertTrue("Should contain Red", resources.contains(Resource.Red));
        assertTrue("Should contain Yellow", resources.contains(Resource.Yellow));
    }

    @Test
    public void extractResources_EmptyList_ReturnsEmptyList() {
        List<Pair<Resource, GridPosition>> pairs = new ArrayList<>();

        List<Resource> resources = actionHelper.extractResources(pairs);

        assertTrue("Empty list should return empty list", resources.isEmpty());
    }

    @Test
    public void extractResources_DuplicateResources_ReturnsAllDuplicates() {
        List<Pair<Resource, GridPosition>> pairs = Arrays.asList(
                new Pair<>(Resource.Green, new GridPosition(-1, 0)),
                new Pair<>(Resource.Green, new GridPosition(2, 0)),
                new Pair<>(Resource.Green, new GridPosition(1, 0))
        );

        List<Resource> resources = actionHelper.extractResources(pairs);

        assertEquals("Should extract 3 duplicate resources", 3, resources.size());
        assertEquals("All should be Green", 3,
                resources.stream().filter(r -> r == Resource.Green).count());
    }

    @Test
    public void complexScenario_ExceedsPollutionCapacity_Invalid() {
        // Card with small pollution capacity
        FakeCard smallCard = new FakeCard(1); // Only 1 pollution space
        GridPosition pos = new GridPosition(2, 1);
        fakeGrid.putCard(pos, smallCard);

        // Try to place 2 pollution on same card
        List<GridPosition> pollution = Arrays.asList(pos, pos);

        assertFalse("Should reject exceeding pollution capacity",
                actionHelper.validatePollution(pollution, fakeGrid));
    }

    @Test
    public void validatePollution_MultipleCards_ValidDistribution() {
        // Create two cards with capacity
        FakeCard card1 = new FakeCard(2);
        FakeCard card2 = new FakeCard(2);
        GridPosition pos1 = new GridPosition(2, 1);
        GridPosition pos2 = new GridPosition(2, 2);

        fakeGrid.putCard(pos1, card1);
        fakeGrid.putCard(pos2, card2);

        // Distribute 3 pollution: 2 on card1, 1 on card2
        List<GridPosition> pollution = Arrays.asList(pos1, pos1, pos2);

        boolean result = actionHelper.validatePollution(pollution, fakeGrid);

        assertTrue("Valid distribution of pollution should return true", result);
    }
}