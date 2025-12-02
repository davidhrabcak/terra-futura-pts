package test.java.effect;

import main.java.com.terrafutura.cards.effects.*;
import main.java.com.terrafutura.resources.Resource;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class EffectOrTest {

    private final Resource gear = Resource.Gear;
    private final Resource car = Resource.Car;
    private final Resource bulb = Resource.Bulb;

    @Test
    public void testFirstMatchingEffectApplies() {

        TransformationFixed e1 =
                new TransformationFixed(List.of(gear, car), List.of(bulb), 0);  // matches
        TransformationFixed e2 =
                new TransformationFixed(List.of(gear, gear), List.of(car), 0);

        EffectOr orEffect = new EffectOr(e1, e2);

        List<Resource> input = List.of(gear, car);
        List<Resource> output = List.of(bulb);

        boolean ok = orEffect.check(input, output, 0);

        assertTrue(ok);
        assertEquals(1, output.size());
        assertEquals(bulb, output.getFirst());           // e1 used
    }

    @Test
    public void testWrongSecondEffect() {
        TransformationFixed e1 =
                new TransformationFixed(List.of(gear, bulb), List.of(car), 0); // does not match
        TransformationFixed e2 =
                new TransformationFixed(List.of(gear, car), List.of(bulb), 0); // matches

        EffectOr orEffect = new EffectOr(e1, e2);

        List<Resource> input = List.of(gear, car);
        List<Resource> output = List.of(bulb);

        boolean ok = orEffect.check(input, output, 0);

        assertTrue(ok);
    }

    @Test
    public void testNoEffectMatches() {
        TransformationFixed e1 =
                new TransformationFixed(List.of(gear, bulb), List.of(car), 0);
        TransformationFixed e2 =
                new TransformationFixed(List.of(bulb, bulb), List.of(gear), 0);

        EffectOr orEffect = new EffectOr(e1, e2);

        List<Resource> input = List.of(gear, car);
        List<Resource> output = new ArrayList<>();

        assertFalse(orEffect.check(input, output, 0));
        assertTrue(output.isEmpty());
    }

    @Test
    public void testStateFormat() {
        TransformationFixed e1 =
                new TransformationFixed(List.of(gear), List.of(car), 0);
        TransformationFixed e2 =
                new TransformationFixed(List.of(car), List.of(bulb), 0);

        EffectOr orEffect = new EffectOr(e1, e2);

        String state = orEffect.state();
        assertEquals("{[(Gear ) -> (Car )], [(Car ) -> (Bulb )]}", state);
    }
}
