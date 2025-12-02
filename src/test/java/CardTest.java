package test.java;

import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class CardTest {

    /** Mock effect that always allows use if pollution == 0 */
    private static class AllowEffect implements Effect {
        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            return pollution == 0;
        }
        @Override
        public boolean hasAssistance() {
            return false;
        }

        @Override
        public String state() {
            return "";
        }
    }

    /** Mock effect that always denies */
    private static class DenyEffect implements Effect {
        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            return false;
        }
        @Override
        public boolean hasAssistance() {
            return false;
        }

        @Override
        public String state() {
            return "";
        }
    }

    /** Mock effect used for assistance checks */
    private static class AssistanceEffect implements Effect {
        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            return pollution == 0;
        }
        @Override
        public boolean hasAssistance() {
            return true;
        }

        @Override
        public String state() {
            return "";
        }
    }

    @Test
    public void testCanPutResourcesWithEffect() {
        Card card = new Card(1,
                Optional.of(new AllowEffect()),
                Optional.empty());

        assertTrue(card.canPutResources(List.of(Resource.Green)));
    }

    @Test
    public void testCannotPutResourcesWhenBlocked() {
        Card card = new Card(0,
                Optional.of(new AllowEffect()),
                Optional.empty());

        // adding pollution blocks immediately
        card.putResources(List.of(Resource.Pollution));
        assertTrue(card.toString().contains("isBlockedByPollution=true"));

        assertFalse(card.canPutResources(List.of(Resource.Green)));
    }

    @Test
    public void testPollutionGoesNegative() {
        Card card = new Card(1,
                Optional.of(new AllowEffect()),
                Optional.empty());

        card.putResources(List.of(Resource.Pollution)); // spacesLeft = 0
        card.putResources(List.of(Resource.Pollution)); // spacesLeft = -1 → blocked

        assertTrue(card.toString().contains("isBlockedByPollution=true"));
        assertFalse(card.canPutResources(List.of(Resource.Green)));
    }

    @Test
    public void testRemovingPollutionUnblocks() {
        Card card = new Card(1,
                Optional.of(new AllowEffect()),
                Optional.empty());

        card.putResources(List.of(Resource.Pollution));
        card.putResources(List.of(Resource.Pollution)); // blocked

        card.removeResource(Resource.Pollution);
        card.removeResource(Resource.Pollution); // should unblock

        assertTrue(card.canPutResources(List.of(Resource.Green)));
    }

    @Test
    public void testCheckUpperEffectUsed() {
        Card card = new Card(2,
                Optional.of(new AllowEffect()),
                Optional.of(new DenyEffect()));

        assertTrue(card.canPutResources(List.of(Resource.Green)));
    }

    @Test
    public void testCheckLowerWhenUpperFails() {
        Card card = new Card(2,
                Optional.of(new DenyEffect()),
                Optional.of(new AllowEffect()));

        assertTrue(card.canPutResources(List.of(Resource.Green)));
    }

    @Test
    public void testHasAssistance() {
        Card card = new Card(2,
                Optional.empty(),
                Optional.of(new AssistanceEffect()));

        assertTrue(card.hasAssistance());
    }
}