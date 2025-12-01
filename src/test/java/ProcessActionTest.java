package test.java;

import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.cards.Pair;
import main.java.com.terrafutura.cards.ProcessAction;
import main.java.com.terrafutura.resources.Resource;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Unit tests for ProcessAction class
 * Tests validation and execution of card activations without assistance
 */
public class ProcessActionTest {

    private ProcessAction processAction;
    private Grid mockGrid;
    private FakeCard activeCard;
    private FakeCard sourceCard;
    private FakeCard targetCard;

    // Fake implementation for testing
    private static class FakeCard extends Card {
        private final List<Resource> resources = new ArrayList<>();
        private final int pollutionSpaces;
        private final boolean hasAssistanceFlag;
        private final Effect upperEffect;
        private final Effect lowerEffect;

        public FakeCard(int pollutionSpaces, Effect upperEffect, Effect lowerEffect, boolean hasAssistance) {
            super(pollutionSpaces, Optional.of(upperEffect), Optional.of(lowerEffect));
            this.pollutionSpaces = pollutionSpaces;
            this.upperEffect = upperEffect;
            this.lowerEffect = lowerEffect;
            this.hasAssistanceFlag = hasAssistance;
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
            return currentPollution + newPollution <= pollutionSpaces;
        }

        @Override
        public boolean canGetResources(List<Resource> resourcesToGet) {
            return new HashSet<>(resources).containsAll(resourcesToGet);
        }

        @Override
        public boolean removeResource(Resource resource) {
            resources.remove(resource);
            return true;
        }

        @Override
        public List<Resource> getResources() {
            return new ArrayList<>(resources);
        }


        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            return upperEffect != null && upperEffect.check(input, output, pollution);
        }

        @Override
        public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
            return lowerEffect != null && lowerEffect.check(input, output, pollution);
        }

        @Override
        public boolean hasAssistance() {
            return hasAssistanceFlag;
        }

        @Override
        public String state() {
            return "FakeCard";
        }

        public void addResource(Resource resource) {
            resources.add(resource);
        }

        public int getPollutionSpaces() {
            return pollutionSpaces;
        }
    }

    // Fake Grid implementation
    private static class FakeGrid extends Grid {
        private final java.util.Map<GridPosition, Card> cards = new java.util.HashMap<>();

        public FakeGrid(Card startingCard) {
            super();
        }

        @Override
        public java.util.Optional<Card> getCard(GridPosition position) {
            return java.util.Optional.ofNullable(cards.get(position));
        }

        @Override
        public boolean canPutCard(GridPosition position) {
            return !cards.containsKey(position);
        }

        @Override
        public void putCard(GridPosition position, Card card) {
            cards.put(position, card);
        }

        public void placeCard(GridPosition position, Card card) {
            cards.put(position, card);
        }
    }

    // Fake Effect that always accepts transactions
    private static class AcceptingEffect implements Effect {
        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            return true;
        }

        @Override
        public String state() {
            return "AcceptingEffect";
        }
    }

    // Fake Effect that always rejects transactions
    private static class RejectingEffect implements Effect {
        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            return false;
        }

        @Override
        public String state() {
            return "RejectingEffect";
        }
    }

    @Before
    public void setUp() {
        processAction = new ProcessAction();
        mockGrid = new FakeGrid(new Card(2, null, null));

        // Create cards for testing
        AcceptingEffect acceptingEffect = new AcceptingEffect();
        RejectingEffect rejectingEffect = new RejectingEffect();

        activeCard = new FakeCard(2, acceptingEffect, rejectingEffect, false);
        sourceCard = new FakeCard(2, acceptingEffect, rejectingEffect, false);
        targetCard = new FakeCard(2, acceptingEffect, rejectingEffect, false);

        // Place cards in grid
        GridPosition activePos = new GridPosition(0, 0);
        GridPosition sourcePos = new GridPosition(1, 0);
        GridPosition targetPos = new GridPosition(-1, 0);

        ((FakeGrid) mockGrid).placeCard(activePos, activeCard);
        ((FakeGrid) mockGrid).placeCard(sourcePos, sourceCard);
        ((FakeGrid) mockGrid).placeCard(targetPos, targetCard);
    }

    @Test
    public void activateCard_ValidTransaction_ReturnsTrue() {
        // Given
        GridPosition activePos = new GridPosition(0, 0);
        GridPosition sourcePos = new GridPosition(1, 0);
        GridPosition targetPos = new GridPosition(-1, 0);

        // Add resources to source card
        sourceCard.addResource(Resource.Green);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, sourcePos)
        );

        List<Pair<Resource, GridPosition>> outputs = List.of(
                new Pair<>(Resource.Red, targetPos)
        );

        List<GridPosition> pollution = List.of(activePos);

        // When
        boolean result = processAction.activateCard(activeCard, mockGrid, inputs, outputs, pollution);

        // Then
        assertTrue("Valid transaction should return true", result);
    }

    @Test
    public void activateCard_CardWithAssistance_ReturnsFalse() {
        // Given
        FakeCard assistanceCard = new FakeCard(2, new AcceptingEffect(), new RejectingEffect(), true);
        GridPosition pos = new GridPosition(0, 0);
        ((FakeGrid) mockGrid).placeCard(pos, assistanceCard);

        // When
        boolean result = processAction.activateCard(assistanceCard, mockGrid,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // Then
        assertFalse("Card with assistance should not be activated in ProcessAction", result);
    }

    @Test
    public void activateCard_CardBlockedByPollution_ReturnsFalse() {
        // Given
        FakeCard blockedCard = new FakeCard(0, new AcceptingEffect(), new RejectingEffect(), false);
        blockedCard.addResource(Resource.Pollution); // Card is now full

        GridPosition pos = new GridPosition(0, 0);
        ((FakeGrid) mockGrid).placeCard(pos, blockedCard);

        // When
        boolean result = processAction.activateCard(blockedCard, mockGrid,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // Then
        assertFalse("Card blocked by pollution should return false", result);
    }

    @Test
    public void activateCard_SourceCannotProvideResource_ReturnsFalse() {
        // Given
        GridPosition sourcePos = new GridPosition(1, 0);
        // Source card has NO resources

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, sourcePos) // But source doesn't have Green
        );

        // When
        boolean result = processAction.activateCard(activeCard, mockGrid,
                inputs, new ArrayList<>(), new ArrayList<>());

        // Then
        assertFalse("Source without required resource should return false", result);
    }

    @Test
    public void activateCard_TargetCannotAcceptResource_ReturnsFalse() {
        // Given
        GridPosition targetPos = new GridPosition(-1, 0);
        // Fill target card with pollution so it can't accept more
        for (int i = 0; i <= targetCard.getPollutionSpaces(); i++) {
            targetCard.addResource(Resource.Pollution);
        }

        List<Pair<Resource, GridPosition>> outputs = List.of(
                new Pair<>(Resource.Red, targetPos)
        );

        // When
        boolean result = processAction.activateCard(activeCard, mockGrid,
                new ArrayList<>(), outputs, new ArrayList<>());

        // Then
        assertFalse("Target that cannot accept resource should return false", result);
    }

    @Test
    public void activateCard_CardEffectRejectsTransaction_ReturnsFalse() {
        // Given
        // Create card with rejecting effect
        FakeCard rejectingCard = new FakeCard(2, new RejectingEffect(), new RejectingEffect(), false);
        GridPosition pos = new GridPosition(0, 0);
        ((FakeGrid) mockGrid).placeCard(pos, rejectingCard);

        // When
        boolean result = processAction.activateCard(rejectingCard, mockGrid,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // Then
        assertFalse("Card effect rejecting transaction should return false", result);
    }

    @Test
    public void activateCard_NullParameters_ReturnsFalse() {
        // When
        boolean result = processAction.activateCard(null, mockGrid,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // Then
        assertFalse("Null card should return false", result);
    }

    @Test
    public void activateCard_EmptyTransaction_ValidIfCardAllows() {
        // Given - card with accepting effect

        // When
        boolean result = processAction.activateCard(activeCard, mockGrid,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // Then - depends on card's effect
        // With AcceptingEffect, it should return true
        assertTrue("Empty transaction with accepting effect should return true", result);
    }

    @Test
    public void activateCard_ValidPollutionPlacement_ReturnsTrue() {
        // Given
        GridPosition activePos = new GridPosition(0, 0);
        activeCard.addResource(Resource.Green); // Give card a resource

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, activePos)
        );

        List<Pair<Resource, GridPosition>> outputs = new ArrayList<>();
        List<GridPosition> pollution = List.of(activePos); // Pollution goes to same card

        // When
        boolean result = processAction.activateCard(activeCard, mockGrid, inputs, outputs, pollution);

        // Then
        assertTrue("Valid transaction with pollution should return true", result);
    }

    @Test
    public void activateCard_TooMuchPollution_ReturnsFalse() {
        // Given
        GridPosition pos = new GridPosition(0, 0);
        // Card has only 1 pollution space and already has pollution
        FakeCard limitedCard = new FakeCard(1, new AcceptingEffect(), new RejectingEffect(), false);
        limitedCard.addResource(Resource.Pollution);
        ((FakeGrid) mockGrid).placeCard(pos, limitedCard);

        // Try to add more pollution
        List<GridPosition> pollution = Arrays.asList(pos, pos); // Two pollution tokens

        // When
        boolean result = processAction.activateCard(limitedCard, mockGrid,
                new ArrayList<>(), new ArrayList<>(), pollution);

        // Then
        assertFalse("Too much pollution should return false", result);
    }
}