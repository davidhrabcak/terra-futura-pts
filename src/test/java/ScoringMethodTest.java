package test.java;

import main.java.com.terrafutura.resources.Resource;
import main.java.com.terrafutura.scoring.Points;
import main.java.com.terrafutura.scoring.ScoringMethod;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ScoringMethodTest {

    @Test
    public void testBasicScoring() {
        ScoringMethod method = new ScoringMethod(
                List.of(Resource.Red, Resource.Red, Resource.Yellow),
                new Points(5)
        );

        List<Resource> playerResources = List.of(
                Resource.Red, Resource.Red, Resource.Red, Resource.Red, Resource.Red,
                Resource.Yellow, Resource.Yellow, Resource.Pollution
        );

        method.selectThisMethodAndCalculate(playerResources);
        assertEquals(16, method.getCalculatedTotal().getValue()); // 2 * 5 + 7 - 1
    }

    @Test
    public void testZeroCombinations() {
        ScoringMethod method = new ScoringMethod(
                List.of(Resource.Red, Resource.Yellow),
                new Points(4)
        );

        method.selectThisMethodAndCalculate(List.of(Resource.Red));
        assertEquals(1, method.getCalculatedTotal().getValue());
    }

    @Test
    public void testZeroResources() {
        ScoringMethod method = new ScoringMethod(List.of(Resource.Green), new Points(4));

        method.selectThisMethodAndCalculate(List.of());
        assertEquals(0, method.getCalculatedTotal().getValue());
    }

    @Test
    public void testSingleResourceMethod() {
        ScoringMethod method = new ScoringMethod(
                List.of(Resource.Bulb),
                new Points(1)
        );

        List<Resource> res = List.of(
                Resource.Bulb, Resource.Bulb, Resource.Bulb
        );

        method.selectThisMethodAndCalculate(res);
        assertEquals(18, method.getCalculatedTotal().getValue());
    }

    @Test
    public void testMorePollutionThanResources() {
        ScoringMethod method = new ScoringMethod(List.of(Resource.Red, Resource.Green), new Points(1));

        List<Resource> res = List.of(
                Resource.Red, Resource.Green, Resource.Pollution,
                Resource.Pollution, Resource.Pollution, Resource.Pollution,
                Resource.Pollution, Resource.Pollution, Resource.Pollution,
                Resource.Pollution, Resource.Pollution, Resource.Pollution
        );

        method.selectThisMethodAndCalculate(res);
        assertEquals(-7, method.getCalculatedTotal().getValue());
    }

    @Test
    public void testLargeResourceCounts() {
        ScoringMethod method = new ScoringMethod(
                List.of(Resource.Red, Resource.Red),
                new Points(3)
        );

        List<Resource> res = new ArrayList<>();

        // add 1000 red resources
        for (int i = 0; i < 1000; i++) {
            res.add(Resource.Red);
        }

        // 1000 red -> 500 combinations * 3 + 1000 = 2500 points
        method.selectThisMethodAndCalculate(res);

        assertEquals(2500, method.getCalculatedTotal().getValue());
    }
}
