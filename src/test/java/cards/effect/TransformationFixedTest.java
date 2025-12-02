package test.java.cards.effect;

import main.java.com.terrafutura.cards.effects.TransformationFixed;
import main.java.com.terrafutura.resources.Resource;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TransformationFixedTest {

    private final Resource gear = Resource.Gear;
    private final Resource car = Resource.Car;

    @Test
    public void testValidTransformation() {
        TransformationFixed effect =
                new TransformationFixed(List.of(gear, gear), List.of(car), 0);

        List<Resource> input = List.of(gear, gear);
        List<Resource> output = List.of(car);

        boolean ok = effect.check(input, output, 0);

        assertTrue(ok);
        assertEquals(1, output.size());
        assertEquals(car, output.getFirst());
        System.out.println("TransformationFixed: check passes with valid transaction");
    }

    @Test
    public void testInvalidInput() {
        TransformationFixed effect =
                new TransformationFixed(List.of(gear, car), List.of(car), 0);

        List<Resource> input = List.of(gear, gear);
        List<Resource> output = List.of(car);

        assertFalse(effect.check(input, output, 0));
        System.out.println("TransformationFixed: check fails with invalid input");
    }

    @Test
    public void testInvalidOutput() {
        TransformationFixed effect =
                new TransformationFixed(List.of(gear, car), List.of(car), 0);

        List<Resource> input = List.of(gear, gear);
        List<Resource> output = new ArrayList<>();

        assertFalse(effect.check(input, output, 0));
        System.out.println("TransformationFixed: check fails with invalid output");
    }

    @Test
    public void testPolluted() {
        TransformationFixed effect =
                new TransformationFixed(List.of(gear, car), List.of(car), 0);

        List<Resource> input = List.of(gear, gear);
        List<Resource> output = List.of(car);

        assertFalse(effect.check(input, output, 1));
        System.out.println("TransformationFixed: check fails when the pollution output is wrong");
    }

    @Test
    public void testStateFormat() {
        TransformationFixed effect =
                new TransformationFixed(List.of(gear, car), List.of(car), 0);

        assertEquals("[(Gear Car ) -> (Car )]", effect.state());
        System.out.println("TransformationFixed: state() works correctly");
    }
}
