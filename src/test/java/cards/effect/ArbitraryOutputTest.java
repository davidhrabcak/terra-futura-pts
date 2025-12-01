package test.java.cards.effect;

import main.java.com.terrafutura.cards.effects.ArbitraryOutput;
import main.java.com.terrafutura.resources.Resource;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ArbitraryOutputTest {

    private final Resource gear = Resource.Gear;
    private final Resource car = Resource.Car;

    @Test
    public void testStrictInputButArbitraryOutputAllowed() {
        ArbitraryOutput effect = new ArbitraryOutput(List.of(gear, car), 2);

        List<Resource> input = List.of(gear, car);
        List<Resource> output = List.of(gear, gear);   // arbitrary content

        boolean ok = effect.check(input, output, 0);

        assertTrue(ok);
        assertEquals(2, output.size());
        System.out.println("ArbitraryOutput: check passes when transaction is valid");
    }

    @Test
    public void testInvalidInput() {
        ArbitraryOutput effect = new ArbitraryOutput(List.of(gear, car), 2);

        List<Resource> input = List.of(gear, gear);   // wrong second resource
        List<Resource> output = List.of(gear, gear);

        assertFalse(effect.check(input, output, 0));
        System.out.println("ArbitraryOutput: check fails with invalid input");
    }
    
    @Test
    public void testInvalidOutput() {
        ArbitraryOutput effect = new ArbitraryOutput(List.of(gear, car), 2);

        List<Resource> input = List.of(gear, gear);
        List<Resource> output = List.of(gear, gear, gear); // too many resources

        assertFalse(effect.check(input, output, 0));
        System.out.println("ArbitraryOutput: check fails with invalid output");
    }

    @Test
    public void testPolluted() {
        ArbitraryOutput effect = new ArbitraryOutput(List.of(gear, car), 2);

        List<Resource> input = List.of(gear, car);
        List<Resource> output = List.of(gear, gear);

        boolean polluted = effect.check(input, output, 1);

        assertFalse(polluted);
        System.out.println("ArbitraryOutput: check fails if card is polluted");
    }

    @Test
    public void testStateString() {
        ArbitraryOutput effect = new ArbitraryOutput(List.of(gear, car), 2);
        assertEquals("[(Gear Car ) -> (any 2 resources)]", effect.state());
        System.out.println("ArbitraryOutput: state() works correctly");
    }
}
