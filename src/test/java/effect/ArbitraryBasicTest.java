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
        ArbitraryBasic effect = new ArbitraryBasic(Arrays.asList(car));

        List<Resource> input = Arrays.asList(bulb, bulb); // arbitrary
        List<Resource> output = new ArrayList<>();

        boolean ok = effect.check(input, output, 0);

        assertTrue(ok);
        assertEquals(1, output.size());
        assertEquals(car, output.getFirst());
    }

    @Test
    public void testStateFormat() {
        ArbitraryBasic effect = new ArbitraryBasic(Arrays.asList(car));

        String s = effect.state();
        assertEquals("[(any 1 resources) -> (Bulb)]", s);
    }

    @Test
    public void testAssistanceFlag() {
        assertFalse(new ArbitraryBasic(Arrays.asList(bulb)).hasAssistance());
    }
}
