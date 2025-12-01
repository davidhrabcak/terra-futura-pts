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
    private FakeCard validCard;
    private FakeCard fullPollutionCard;

    // Fake Card implementation for testing
    private static class FakeCard extends Card {
        private List<Resource> resources = new ArrayList<>();
        private int pollutionSpaces;
        private boolean canPutResourcesResult = true;
        private boolean canGetResourcesResult = true;

        public FakeCard(int pollutionSpaces) {
            super(pollutionSpaces, null, null);
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
            long newPollution = resourcesToAdd.stream()
                    .filter(r -> r == Resource.Pollution)
                    .count();

            return canPutResourcesResult && (currentPollution + newPollution <= pollutionSpaces);
        }

        @Override
        public boolean canGetResources(List<Resource> resourcesToGet) {
            return canGetResourcesResult && new HashSet<>(resources).containsAll(resourcesToGet);
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
        private final Set<GridPosition> invalidPositions = new HashSet<>();

        public FakeGrid(Card startingCard) {
            super();
        }

        public void placeCard(GridPosition position, Card card) {
            cards.put(position, card);
        }

        public void markPositionInvalid(GridPosition position) {
            invalidPositions.add(position);
        }

        @Override
        public Optional<Card> getCard(GridPosition position) {
            if (invalidPositions.contains(position)) {
                return Optional.empty();
            }
            return Optional.ofNullable(cards.get(position));
        }

        @Override
        public boolean canPutCard(GridPosition position) {
            // canPutCard returns true if position is empty
            return !cards.containsKey(position) && !invalidPositions.contains(position);
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
        public void setActivated(GridPosition coordinate) {
            // Not needed for testing
        }

        @Override
        public void endTurn() {
            // Not needed for testing
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
        validCard = new FakeCard(3);
        fullPollutionCard = new FakeCard(1);
        fullPollutionCard.addResource(Resource.Pollution); // Card is now full

        // Place cards in grid at valid positions
        GridPosition card1Pos = new GridPosition(0, 0);
        GridPosition card2Pos = new GridPosition(1, 1);
        GridPosition card3Pos = new GridPosition(-1, -1);

        fakeGrid.placeCard(card1Pos, validCard);
        fakeGrid.placeCard(card2Pos, validCard);
        fakeGrid.placeCard(card3Pos, fullPollutionCard);
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
                actionHelper.nullEntry(fakeGrid, validCard,
                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    public void nullEntry_NullGridOnly_ReturnsTrue() {
        assertTrue("Null grid only should return true",
                actionHelper.nullEntry(null, validCard,
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
    public void validateInputs_ValidInputs_ReturnsTrue() {
        // Given
        GridPosition validPos = new GridPosition(0, 0);
        validCard.addResource(Resource.Green);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, validPos)
        );

        // When
        boolean result = actionHelper.validateInputs(inputs, fakeGrid);

        // Then
        assertTrue("Valid inputs should return true", result);
    }

    @Test
    public void validateInputs_EmptyPosition_ReturnsFalse() {
        // Given - position with no card
        GridPosition emptyPos = new GridPosition(2, 2);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, emptyPos)
        );

        // When
        boolean result = actionHelper.validateInputs(inputs, fakeGrid);

        // Then
        assertFalse("Empty position should return false", result);
    }

    @Test
    public void validateInputs_CardCannotProvideResource_ReturnsFalse() {
        // Given
        GridPosition validPos = new GridPosition(0, 0);
        // Card does NOT have Red resource

        List<Pair<Resource, GridPosition>> inputs = Arrays.asList(
                new Pair<>(Resource.Red, validPos)
        );

        // When
        boolean result = actionHelper.validateInputs(inputs, fakeGrid);

        // Then
        assertFalse("Card without required resource should return false", result);
    }

    @Test
    public void validateInputs_MultipleValidInputs_ReturnsTrue() {
        // Given
        GridPosition pos1 = new GridPosition(0, 0);
        GridPosition pos2 = new GridPosition(1, 1);
        validCard.addResource(Resource.Green);
        validCard.addResource(Resource.Red);

        List<Pair<Resource, GridPosition>> inputs = Arrays.asList(
                new Pair<>(Resource.Green, pos1),
                new Pair<>(Resource.Red, pos2)
        );

        // When
        boolean result = actionHelper.validateInputs(inputs, fakeGrid);

        // Then
        assertTrue("Multiple valid inputs should return true", result);
    }

    @Test
    public void validateOutputs_ValidOutputs_ReturnsTrue() {
        // Given
        GridPosition validPos = new GridPosition(0, 0);

        List<Pair<Resource, GridPosition>> outputs = Arrays.asList(
                new Pair<>(Resource.Green, validPos)
        );

        // When
        boolean result = actionHelper.validateOutputs(outputs, fakeGrid);

        // Then
        assertTrue("Valid outputs should return true", result);
    }

    @Test
    public void validateOutputs_CardCannotAcceptResource_ReturnsFalse() {
        // Given
        GridPosition fullCardPos = new GridPosition(-1, -1); // Position of fullPollutionCard
        // fullPollutionCard has pollution capacity of 1 and is already full

        List<Pair<Resource, GridPosition>> outputs = Arrays.asList(
                new Pair<>(Resource.Pollution, fullCardPos) // Trying to add more pollution
        );

        // When
        boolean result = actionHelper.validateOutputs(outputs, fakeGrid);

        // Then
        assertFalse("Card that cannot accept resource should return false", result);
    }

    @Test
    public void validateOutputs_EmptyPosition_ReturnsFalse() {
        // Given
        GridPosition emptyPos = new GridPosition(2, 2);

        List<Pair<Resource, GridPosition>> outputs = Arrays.asList(
                new Pair<>(Resource.Green, emptyPos)
        );

        // When
        boolean result = actionHelper.validateOutputs(outputs, fakeGrid);

        // Then
        assertFalse("Empty position should return false", result);
    }

    @Test
    public void validatePollution_ValidPollutionPositions_ReturnsTrue() {
        // Given
        GridPosition pos1 = new GridPosition(0, 0);
        GridPosition pos2 = new GridPosition(1, 1);

        List<GridPosition> pollution = Arrays.asList(pos1, pos2);

        // When
        boolean result = actionHelper.validatePollution(pollution, fakeGrid);

        // Then
        assertTrue("Valid pollution positions should return true", result);
    }

    @Test
    public void validatePollution_CardCannotAcceptMorePollution_ReturnsFalse() {
        // Given
        GridPosition fullCardPos = new GridPosition(-1, -1); // fullPollutionCard position
        // fullPollutionCard already has 1 pollution and capacity is 1

        List<GridPosition> pollution = Arrays.asList(fullCardPos);

        // When
        boolean result = actionHelper.validatePollution(pollution, fakeGrid);

        // Then
        assertFalse("Card that cannot accept more pollution should return false", result);
    }

    @Test
    public void validatePollution_MultiplePollutionToSameCardWithinCapacity_ReturnsTrue() {
        // Given - card with capacity 3, no pollution yet
        GridPosition pos = new GridPosition(0, 0);

        List<GridPosition> pollution = Arrays.asList(pos, pos, pos); // 3 pollution to same card

        // When
        boolean result = actionHelper.validatePollution(pollution, fakeGrid);

        // Then
        assertTrue("Multiple pollution within capacity should return true", result);
    }

    @Test
    public void validatePollution_MultiplePollutionExceedsCapacity_ReturnsFalse() {
        // Given - card with capacity 1, no pollution yet
        FakeCard smallCard = new FakeCard(1); // Capacity 1
        GridPosition smallCardPos = new GridPosition(2, 0);
        fakeGrid.placeCard(smallCardPos, smallCard);

        List<GridPosition> pollution = Arrays.asList(smallCardPos, smallCardPos); // 2 pollution

        // When
        boolean result = actionHelper.validatePollution(pollution, fakeGrid);

        // Then
        assertFalse("Pollution exceeding capacity should return false", result);
    }

    @Test
    public void validatePollution_EmptyPosition_ReturnsFalse() {
        // Given
        GridPosition emptyPos = new GridPosition(2, 2);

        List<GridPosition> pollution = Arrays.asList(emptyPos);

        // When
        boolean result = actionHelper.validatePollution(pollution, fakeGrid);

        // Then
        assertFalse("Empty pollution position should return false", result);
    }

    @Test
    public void validatePollution_EmptyPollutionList_ReturnsTrue() {
        // Given
        List<GridPosition> pollution = new ArrayList<>();

        // When
        boolean result = actionHelper.validatePollution(pollution, fakeGrid);

        // Then
        assertTrue("Empty pollution list should return true", result);
    }

    @Test
    public void validTransaction_UpperEffectAccepts_ReturnsTrue() {
        // Given
        FakeCard acceptingCard = new FakeCard(2);

        List<Pair<Resource, GridPosition>> inputs = Arrays.asList(
                new Pair<>(Resource.Green, new GridPosition(0, 0))
        );

        List<Pair<Resource, GridPosition>> outputs = Arrays.asList(
                new Pair<>(Resource.Red, new GridPosition(1, 0))
        );

        // When - test upper effect (true)
        boolean result = actionHelper.validTransaction(acceptingCard, inputs, outputs, true);

        // Then
        assertTrue("Upper effect accepting should return true", result);
    }

    @Test
    public void validTransaction_UpperEffectRejectsLowerAccepts_ReturnsTrue() {
        // Given - card where upper effect rejects but lower accepts
        FakeCard card = new FakeCard(2) {
            @Override
            public boolean check(List<Resource> input, List<Resource> output, int pollution) {
                return false; // Upper rejects
            }

            @Override
            public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
                return true; // Lower accepts
            }
        };

        List<Pair<Resource, GridPosition>> inputs = Arrays.asList(
                new Pair<>(Resource.Green, new GridPosition(0, 0))
        );

        List<Pair<Resource, GridPosition>> outputs = Arrays.asList(
                new Pair<>(Resource.Red, new GridPosition(1, 0))
        );

        // When - test lower effect (false parameter)
        boolean result = actionHelper.validTransaction(card, inputs, outputs, false);

        // Then
        assertTrue("Lower effect accepting should return true", result);
    }

    @Test
    public void validTransaction_BothEffectsReject_ReturnsFalse() {
        // Given - card where both effects reject
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

        List<Pair<Resource, GridPosition>> inputs = Arrays.asList(
                new Pair<>(Resource.Green, new GridPosition(0, 0))
        );

        List<Pair<Resource, GridPosition>> outputs = Arrays.asList(
                new Pair<>(Resource.Red, new GridPosition(1, 0))
        );

        // When - test upper effect
        boolean result = actionHelper.validTransaction(rejectingCard, inputs, outputs, true);

        // Then
        assertFalse("Both effects rejecting should return false", result);
    }

    @Test
    public void validTransaction_EmptyTransaction_ReturnsTrueIfCardAccepts() {
        // Given
        FakeCard acceptingCard = new FakeCard(2);

        List<Pair<Resource, GridPosition>> inputs = new ArrayList<>();
        List<Pair<Resource, GridPosition>> outputs = new ArrayList<>();

        // When
        boolean result = actionHelper.validTransaction(acceptingCard, inputs, outputs, true);

        // Then
        assertTrue("Empty transaction with accepting card should return true", result);
    }

    @Test
    public void extractResources_SinglePair_ReturnsSingleResource() {
        // Given
        List<Pair<Resource, GridPosition>> pairs = Arrays.asList(
                new Pair<>(Resource.Green, new GridPosition(0, 0))
        );

        // When
        List<Resource> resources = actionHelper.extractResources(pairs);

        // Then
        assertEquals("Should extract 1 resource", 1, resources.size());
        assertEquals("Extracted resource should be Green", Resource.Green, resources.get(0));
    }

    @Test
    public void extractResources_MultiplePairs_ReturnsAllResources() {
        // Given
        List<Pair<Resource, GridPosition>> pairs = Arrays.asList(
                new Pair<>(Resource.Green, new GridPosition(0, 0)),
                new Pair<>(Resource.Red, new GridPosition(1, 0)),
                new Pair<>(Resource.Yellow, new GridPosition(2, 0))
        );

        // When
        List<Resource> resources = actionHelper.extractResources(pairs);

        // Then
        assertEquals("Should extract 3 resources", 3, resources.size());
        assertTrue("Should contain Green", resources.contains(Resource.Green));
        assertTrue("Should contain Red", resources.contains(Resource.Red));
        assertTrue("Should contain Yellow", resources.contains(Resource.Yellow));
    }

    @Test
    public void extractResources_EmptyList_ReturnsEmptyList() {
        // Given
        List<Pair<Resource, GridPosition>> pairs = new ArrayList<>();

        // When
        List<Resource> resources = actionHelper.extractResources(pairs);

        // Then
        assertTrue("Empty list should return empty list", resources.isEmpty());
    }

    @Test
    public void extractResources_DuplicateResources_ReturnsAllDuplicates() {
        // Given
        List<Pair<Resource, GridPosition>> pairs = Arrays.asList(
                new Pair<>(Resource.Green, new GridPosition(0, 0)),
                new Pair<>(Resource.Green, new GridPosition(1, 0)),
                new Pair<>(Resource.Green, new GridPosition(2, 0))
        );

        // When
        List<Resource> resources = actionHelper.extractResources(pairs);

        // Then
        assertEquals("Should extract 3 duplicate resources", 3, resources.size());
        assertEquals("All should be Green", 3,
                resources.stream().filter(r -> r == Resource.Green).count());
    }

    @Test
    public void complexScenario_ExceedsPollutionCapacity_Invalid() {
        // Card with small pollution capacity
        FakeCard smallCard = new FakeCard(1); // Only 1 pollution space
        GridPosition pos = new GridPosition(0, 0);
        fakeGrid.placeCard(pos, smallCard);

        // Try to place 2 pollution on same card
        List<GridPosition> pollution = Arrays.asList(pos, pos);

        assertFalse("Should reject exceeding pollution capacity",
                actionHelper.validatePollution(pollution, fakeGrid));
    }
}

