package test.java.cards.effect;

import main.java.com.terrafutura.cards.effects.ArbitraryBasic;
import main.java.com.terrafutura.resources.Resource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ArbitraryBasicTest {

    private final Resource bulb = Resource.Bulb;
    private final Resource car = Resource.Car;

    @Test
    public void testValidTransaction() {
        ArbitraryBasic effect = new ArbitraryBasic(List.of(car));

        List<Resource> input = List.of(bulb, bulb);
        List<Resource> output = new ArrayList<>();

        boolean ok = effect.check(input, output, 0);

        assertTrue(ok);
        System.out.println("ArbitraryBasic: check passes in a valid transaction");
    }

    @Test
    public void testWrongInput() {
        ArbitraryBasic effect = new ArbitraryBasic(List.of(car));

        List<Resource> output = new ArrayList<>();

        boolean wrongIn = effect.check(List.of(), output, 0);

        assertFalse(wrongIn);
        System.out.println("ArbitraryBasic: check fails with wrong input");
    }

    @Test
    public void testWrongOutput() {
        ArbitraryBasic effect = new ArbitraryBasic(List.of(car));

        List<Resource> input = List.of(bulb, bulb);

        boolean wrongOut = effect.check(input, List.of(bulb, bulb, bulb), 0);

        assertFalse(wrongOut);
        System.out.println("ArbitraryBasic: check fails with wrong output");
    }

    @Test
    public void testPolluted() {
        ArbitraryBasic effect = new ArbitraryBasic(List.of(car));

        List<Resource> output = new ArrayList<>();
        List<Resource> input = List.of(bulb, bulb);

        boolean polluted = effect.check(input, output, 1);

        assertFalse(polluted);
        System.out.println("ArbitraryBasic: check fails if card is polluted");
    }

    @Test
    public void testStateFormat() {
        ArbitraryBasic effect = new ArbitraryBasic(List.of(car));
        ArbitraryBasic effect1 = new ArbitraryBasic(List.of(car, bulb, car));

        String s = effect.state();
        assertEquals("[(any 1 resources) -> (Car)]", s);

        String s1 = effect1.state();
        assertEquals("[(any 1 resources) -> (Car Bulb Car )]", s1);
        System.out.println("ArbitraryBasic: state works correctly");
    }

    @Test
    public void testAssistanceFlag() {
        assertFalse(new ArbitraryBasic(List.of(bulb)).hasAssistance());
        System.out.println("ArbitraryBasic: hasAssistance() returns false");
    }
}
