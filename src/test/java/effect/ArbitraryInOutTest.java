package test.java.effect;

import main.java.com.terrafutura.cards.effects.ArbitraryInOut;
import main.java.com.terrafutura.resources.Resource;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ArbitraryInOutTest {

    private final Resource gear = Resource.Gear;

    @Test
    public void testInputAndOutputArbitraryAndSizeAccepted() {
        ArbitraryInOut effect = new ArbitraryInOut(2, 1, 0);  // expects input size 2, output size 1

        List<Resource> input = List.of(gear, gear);
        List<Resource> output = List.of(gear);

        boolean ok = effect.check(input, output, 0);

        assertTrue(ok);
        assertEquals(1, output.size());
        assertEquals(gear, output.getFirst());
    }

    @Test
    public void testInvalidOutputSize() {
        ArbitraryInOut effect = new ArbitraryInOut(2, 1, 0);

        List<Resource> input = Arrays.asList(gear, gear);
        List<Resource> output = Arrays.asList(gear, gear);

        assertFalse(effect.check(input, output, 0));
    }

    @Test
    public void testInvalidInputSize() {
        ArbitraryInOut effect = new ArbitraryInOut(2, 1, 0);  // expects input size 2, output size 1

        boolean wrongIn = effect.check(List.of(gear), List.of(gear), 0);

        assertFalse(wrongIn);
    }

    @Test
    public void testPolluted() {
        ArbitraryInOut effect = new ArbitraryInOut(2, 1, 0);  // expects input size 2, output size 1

        List<Resource> input = Arrays.asList(gear, gear);
        List<Resource> output = Arrays.asList(gear, gear);

        boolean polluted = effect.check(input, output, 1);
        assertFalse(polluted);
    }

    @Test
    public void testStateFormat() {
        ArbitraryInOut effect = new ArbitraryInOut(3, 2, 0);
        assertEquals("[(any 3 resources) -> (any 2 resources)]", effect.state());
    }
}
