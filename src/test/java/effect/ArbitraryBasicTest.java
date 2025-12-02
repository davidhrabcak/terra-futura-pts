package test.java.effect;

import main.java.com.terrafutura.cards.effects.ArbitraryBasic;
import main.java.com.terrafutura.resources.Resource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.*;

import static org.junit.Assert.*;

public class ArbitraryBasicTest {

    private final Resource bulb = Resource.Bulb;
    private final Resource car = Resource.Car;

    @Test
    public void testValidTransaction() {
        ArbitraryBasic effect = new ArbitraryBasic(List.of(car), 0);

        List<Resource> input = List.of(bulb, bulb);
        List<Resource> output = new ArrayList<>();

        boolean ok = effect.check(input, output, 0);

        assertTrue(ok);
    }

    @Test
    public void testWrongInput() {
        ArbitraryBasic effect = new ArbitraryBasic(List.of(car), 0);

        List<Resource> output = new ArrayList<>();

        boolean wrongIn = effect.check(List.of(), output, 0);

        assertFalse(wrongIn);
    }

    @Test
    public void testWrongOutput() {
        ArbitraryBasic effect = new ArbitraryBasic(List.of(car), 0);

        List<Resource> input = List.of(bulb, bulb);

        boolean wrongOut = effect.check(input, List.of(bulb, bulb, bulb), 0);

        assertFalse(wrongOut);
    }

    @Test
    public void testPolluted() {
        ArbitraryBasic effect = new ArbitraryBasic(List.of(car), 0);

        List<Resource> output = new ArrayList<>();
        List<Resource> input = List.of(bulb, bulb);

        boolean polluted = effect.check(input, output, 1);

        assertFalse(polluted);
    }

    @Test
    public void testStateFormat() {
        ArbitraryBasic effect = new ArbitraryBasic(List.of(car), 0);
        ArbitraryBasic effect1 = new ArbitraryBasic(List.of(car, bulb, car), 0);

        String s = effect.state();
        assertEquals("[(any 1 resources) -> (Car)]", s);

        String s1 = effect1.state();
        assertEquals("[(any 1 resources) -> (Car Bulb Car )]", s1);
    }

    @Test
    public void testAssistanceFlag() {
        assertFalse(new ArbitraryBasic(List.of(bulb), 0).hasAssistance());
    }
}
