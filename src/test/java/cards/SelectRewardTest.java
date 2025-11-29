package test.java;

import main.java.com.terrafutura.cards.SelectReward;
import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.cards.effects.ArbitraryInOut;
import main.java.com.terrafutura.resources.Resource;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

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

public class SelectRewardTest {

    private CardFake c(String n) { return new CardFake(n); }

    @Test
    public void testInitialState() {
        SelectReward sr = new SelectReward();
        assertNull(sr.selected);
        assertFalse(sr.canSelectReward(Resource.Car));
    }

    @Test
    public void testSetRewardSuccess() {
        SelectReward sr = new SelectReward();

        boolean ok = sr.setReward(1, c("Card"), List.of(Resource.Car, Resource.Car));

        assertTrue(ok);
        assertTrue(sr.canSelectReward(Resource.Car));
        sr.selectReward(Resource.Car);
        assertTrue(sr.canSelectReward(Resource.Car));
        sr.selectReward(Resource.Car);
        assertFalse(sr.canSelectReward(Resource.Car));
    }

    @Test
    public void testSetRewardFailsOnNullCard() {
        SelectReward sr = new SelectReward();

        boolean ok = sr.setReward(1, null, List.of(Resource.Car));
        assertFalse(ok);
    }

    @Test
    public void testSetRewardFailsOnEmptyRewardList() {
        SelectReward sr = new SelectReward();

        boolean ok = sr.setReward(1, c("CardA"), List.of());
        assertFalse(ok);
    }

    @Test
    public void testCanSelectRewardRequiresSetReward() {
        SelectReward sr = new SelectReward();

        assertFalse(sr.canSelectReward(Resource.Car));

        sr.setReward(10, c("CardA"), List.of(Resource.Car, Resource.Car));

        assertTrue(sr.canSelectReward(Resource.Car));
        sr.selectReward(Resource.Car);
        sr.selectReward(Resource.Car);
        assertFalse(sr.canSelectReward(Resource.Car));
    }

    @Test
    public void testSelectRewardOnlyWhenAllowed() {
        SelectReward sr = new SelectReward();
        sr.setReward(7, c("Card"), List.of(Resource.Car, Resource.Car));

        sr.selectReward(Resource.Car);
        assertEquals(Resource.Car.toString(), sr.selected.toString());

        sr.selectReward(Resource.Car);
        assertEquals(Resource.Car.toString(), sr.selected.toString());
    }

    @Test
    public void testStateFormatting() {
        SelectReward sr = new SelectReward();
        sr.setReward(3, c("Card"), List.of(Resource.Car, Resource.Gear));

        String state = sr.state();

        assertTrue(state.startsWith("Select a reward: "));
        assertTrue(state.contains("Car"));
        assertTrue(state.contains("Gear"));
    }
}
