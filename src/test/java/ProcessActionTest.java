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
    private Grid fakeGrid;
    private FakeCard activeCard;
    private FakeCard sourceCard;
    private FakeCard targetCard;

    // Fake implementation for testing
    private static class FakeCard extends Card {
        private final List<Resource> resources = new ArrayList<>();
        private final int pollutionSpaces;
        private final boolean hasAssistanceFlag;
        private final Effect effect;
        private final Effect lowerEffect;

        public FakeCard(int pollutionSpaces, Optional<Effect> effect, Optional<Effect> lowerEffect, boolean hasAssistance) {
            super(pollutionSpaces, effect, lowerEffect);
            this.pollutionSpaces = pollutionSpaces;
            this.effect = effect.orElse(null);
            this.lowerEffect = lowerEffect.orElse(null);
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
            if (effect == null){
                return false;
            }
            return effect.check(input, output, pollution);
        }

        @Override
        public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
            if (lowerEffect == null){
                return false;
            }
            return lowerEffect.check(input, output, pollution);
        }

        @Override
        public boolean hasAssistance() {
            return hasAssistanceFlag;
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

        // Create a starting card for FakeGrid
        FakeCard startingCard = new FakeCard(2, Optional.empty(), Optional.empty(), false);
        fakeGrid = new FakeGrid(startingCard);

        // Create cards for testing
        AcceptingEffect acceptingEffect = new AcceptingEffect();
        RejectingEffect rejectingEffect = new RejectingEffect();

        activeCard = new FakeCard(2, Optional.of(acceptingEffect), Optional.of(rejectingEffect), false);
        sourceCard = new FakeCard(2, Optional.of(acceptingEffect), Optional.of(rejectingEffect), false);
        targetCard = new FakeCard(2, Optional.of(acceptingEffect), Optional.of(rejectingEffect), false);

        GridPosition activePos = new GridPosition(1, 0);
        GridPosition sourcePos = new GridPosition(2, 0);
        GridPosition targetPos = new GridPosition(-1, 0);

        fakeGrid.putCard(activePos, activeCard);
        fakeGrid.putCard(sourcePos, sourceCard);
        fakeGrid.putCard(targetPos, targetCard);
    }

    @Test
    public void activateCard_ValidTransaction_ReturnsTrue() {
        // Use the actual positions from setup
        GridPosition activePos = new GridPosition(1, 0);
        GridPosition sourcePos = new GridPosition(2, 0);
        GridPosition targetPos = new GridPosition(-1, 0);

        // Add resources to source card
        sourceCard.putResources(List.of(Resource.Green));

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, sourcePos)
        );

        List<Pair<Resource, GridPosition>> outputs = List.of(
                new Pair<>(Resource.Red, targetPos)
        );

        List<GridPosition> pollution = List.of(activePos);

        boolean result = processAction.activateCard(activeCard, fakeGrid, inputs, outputs, pollution);

        assertTrue("Valid transaction should return true", result);
    }

    @Test
    public void activateCard_CardWithAssistance_ReturnsFalse() {
        FakeCard assistanceCard = new FakeCard(2, Optional.of(new AcceptingEffect()), Optional.of(new RejectingEffect()), true);
        GridPosition pos = new GridPosition(1, 1);
        fakeGrid.putCard(pos, assistanceCard);

        boolean result = processAction.activateCard(assistanceCard, fakeGrid,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        assertFalse("Card with assistance should not be activated in ProcessAction", result);
    }

    @Test
    public void activateCard_CardBlockedByPollution_ReturnsFalse() {
        FakeCard blockedCard = new FakeCard(1, Optional.of(new AcceptingEffect()), Optional.of(new RejectingEffect()), false);
        blockedCard.putResources(List.of(Resource.Pollution)); // Card is now full (1/1)

        GridPosition pos = new GridPosition(1, 1);
        fakeGrid.putCard(pos, blockedCard);

        // Try to activate with pollution placement (which would exceed limit)
        List<GridPosition> pollution = List.of(pos);
        boolean result = processAction.activateCard(blockedCard, fakeGrid,
                new ArrayList<>(), new ArrayList<>(), pollution);

        assertFalse("Card blocked by pollution should return false", result);
    }

    @Test
    public void activateCard_SourceCannotProvideResource_ReturnsFalse() {
        GridPosition sourcePos = new GridPosition(2, 0);
        // Source card has NO resources

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, sourcePos) // But source doesn't have Green
        );

        boolean result = processAction.activateCard(activeCard, fakeGrid,
                inputs, new ArrayList<>(), new ArrayList<>());

        assertFalse("Source without required resource should return false", result);
    }

    @Test
    public void activateCard_TargetCannotAcceptResource_ReturnsFalse() {
        GridPosition targetPos = new GridPosition(-1, 0);
        // Fill target card with pollution so it can't accept more
        targetCard.putResources(List.of(Resource.Pollution, Resource.Pollution, Resource.Pollution));

        List<Pair<Resource, GridPosition>> outputs = List.of(
                new Pair<>(Resource.Red, targetPos)
        );

        boolean result = processAction.activateCard(activeCard, fakeGrid,
                new ArrayList<>(), outputs, new ArrayList<>());

        assertFalse("Target that cannot accept resource should return false", result);
    }

    @Test
    public void activateCard_CardEffectRejectsTransaction_ReturnsFalse() {
        // Create card with rejecting effect
        FakeCard rejectingCard = new FakeCard(2, Optional.of(new RejectingEffect()), Optional.of(new RejectingEffect()), false);
        GridPosition pos = new GridPosition(1, 1);
        fakeGrid.putCard(pos, rejectingCard);

        boolean result = processAction.activateCard(rejectingCard, fakeGrid,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        assertFalse("Card effect rejecting transaction should return false", result);
    }

    @Test
    public void activateCard_NullParameters_ReturnsFalse() {
        boolean result = processAction.activateCard(null, fakeGrid,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        assertFalse("Null card should return false", result);
    }

    @Test
    public void activateCard_EmptyTransaction_ValidIfCardAllows() {
        // activeCard has AcceptingEffect, so empty transaction should be valid
        boolean result = processAction.activateCard(activeCard, fakeGrid,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // With AcceptingEffect, it should return true
        assertTrue("Empty transaction with accepting effect should return true", result);
    }

    @Test
    public void activateCard_ValidPollutionPlacement_ReturnsTrue() {
        GridPosition activePos = new GridPosition(1, 0);
        // Card has 2 pollution spaces and no pollution yet
        activeCard.putResources(List.of(Resource.Green)); // Give card a resource

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, activePos)
        );

        List<Pair<Resource, GridPosition>> outputs = new ArrayList<>();
        List<GridPosition> pollution = List.of(activePos); // Pollution goes to same card

        boolean result = processAction.activateCard(activeCard, fakeGrid, inputs, outputs, pollution);

        assertTrue("Valid transaction with pollution should return true", result);
    }

    @Test
    public void activateCard_TooMuchPollution_ReturnsFalse() {
        GridPosition pos = new GridPosition(1, 1);
        // Card has only 1 pollution space and already has pollution
        FakeCard limitedCard = new FakeCard(1, Optional.of(new AcceptingEffect()), Optional.of(new RejectingEffect()), false);
        limitedCard.putResources(List.of(Resource.Pollution));
        fakeGrid.putCard(pos, limitedCard);

        // Try to add more pollution
        List<GridPosition> pollution = List.of(pos); // One more pollution token

        boolean result = processAction.activateCard(limitedCard, fakeGrid,
                new ArrayList<>(), new ArrayList<>(), pollution);

        assertFalse("Too much pollution should return false", result);
    }

    @Test
    public void activateCard_ValidTransactionWithoutPollution_ReturnsTrue() {
        GridPosition activePos = new GridPosition(1, 0);
        GridPosition sourcePos = new GridPosition(2, 0);
        GridPosition targetPos = new GridPosition(-1, 0);

        // Add resources to source card
        sourceCard.putResources(List.of(Resource.Green));
        sourceCard.putResources(List.of(Resource.Yellow));

        List<Pair<Resource, GridPosition>> inputs = Arrays.asList(
                new Pair<>(Resource.Green, sourcePos),
                new Pair<>(Resource.Yellow, sourcePos)
        );

        List<Pair<Resource, GridPosition>> outputs = Arrays.asList(
                new Pair<>(Resource.Red, targetPos),
                new Pair<>(Resource.Bulb, targetPos)
        );

        List<GridPosition> pollution = new ArrayList<>(); // No pollution

        boolean result = processAction.activateCard(activeCard, fakeGrid, inputs, outputs, pollution);

        assertTrue("Valid transaction without pollution should return true", result);
    }

    @Test
    public void activateCard_ValidTransactionMultiplePollution_ReturnsTrue() {
        // Create a card with larger pollution capacity
        FakeCard largeCard = new FakeCard(3, Optional.of(new AcceptingEffect()), Optional.of(new RejectingEffect()), false);
        GridPosition largeCardPos = new GridPosition(1, 1);
        fakeGrid.putCard(largeCardPos, largeCard);

        GridPosition sourcePos = new GridPosition(2, 0);
        sourceCard.putResources(List.of(Resource.Green));

        List<Pair<Resource, GridPosition>> inputs = List.of(
                new Pair<>(Resource.Green, sourcePos)
        );

        List<Pair<Resource, GridPosition>> outputs = new ArrayList<>();

        // Place 2 pollution on the same card
        List<GridPosition> pollution = Arrays.asList(largeCardPos, largeCardPos);

        boolean result = processAction.activateCard(largeCard, fakeGrid, inputs, outputs, pollution);

        assertTrue("Valid transaction with multiple pollution should return true", result);
    }
}