package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.cards.effects.*;
import main.java.com.terrafutura.resources.Resource;

import java.util.*;

public class CardFactory {
    private final Random rnd = new Random();


    public List<Card> createLevelIDeck() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 23; i++) {
            cards.add(createRandomLevelICard());
        }
        Collections.shuffle(cards);
        return cards;
    }

    public List<Card> createLevelIIDeck() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            cards.add(createRandomLevelIICard());
        }
        Collections.shuffle(cards);
        return cards;
    }

    public Card createRandomLevelICard() {
        int pollutionSpaces = rnd.nextInt(2);
        Effect upperEffect = createRandomSimpleEffect();

        return new Card(pollutionSpaces, upperEffect, null);
    }

    public Card createRandomLevelIICard() {
        int pollutionSpaces = rnd.nextInt(3);
        Effect upperEffect = createRandomComplexEffect();
        Effect lowerEffect = createRandomComplexEffect();

        return new Card(pollutionSpaces, upperEffect, lowerEffect);

    }

    public Card createStartingCard() {
        Effect upperEffect = new EffectOr(
                new ArbitraryBasic(0, Arrays.asList(Resource.Green, Resource.Red, Resource.Yellow)),
                new ArbitraryBasic(0, List.of(Resource.Money))
        );
        return new Card(0,  upperEffect, null);
    }

    private Effect createRandomSimpleEffect() {
        return switch (rnd.nextInt(11)) {
            case 0 -> new ArbitraryBasic(0, List.of(Resource.Green));
            case 1 -> new ArbitraryBasic(1, List.of(Resource.Green));
            case 2 -> new ArbitraryBasic(0, List.of(Resource.Red));
            case 3 -> new ArbitraryBasic(1, List.of(Resource.Red));
            case 4 -> new ArbitraryBasic(0, List.of(Resource.Yellow));
            case 5 -> new ArbitraryBasic(1, List.of(Resource.Yellow));
            case 6 -> new TransformationFixed(List.of(Resource.Green), List.of(Resource.Yellow), 1);
            case 7 -> new TransformationFixed(List.of(Resource.Red), List.of(Resource.Yellow), 1);
            case 8 -> new TransformationFixed(List.of(Resource.Green), List.of(Resource.Yellow), 0);
            case 9 -> new TransformationFixed(List.of(Resource.Yellow), List.of(Resource.Yellow), 0);
            case 10 -> new TransformationFixed(List.of(Resource.Green), List.of(Resource.Red), 1);
            default -> new ArbitraryBasic();
        };
    }

    private Effect createRandomComplexEffect() {
        return switch (rnd.nextInt(12)) {
            case 0 -> new EffectOr(
                    new TransformationFixed(Arrays.asList(Resource.Green, Resource.Green), List.of(Resource.Bulb), 0),
                    new TransformationFixed(Arrays.asList(Resource.Green, Resource.Green), Arrays.asList(Resource.Bulb, Resource.Bulb, Resource.Pollution), 1)
            );
            case 1 -> new EffectOr(
                    new TransformationFixed(Arrays.asList(Resource.Red, Resource.Red), List.of(Resource.Gear), 0),
                    new TransformationFixed(List.of(Resource.Red), Arrays.asList(Resource.Gear, Resource.Gear, Resource.Pollution), 1)
            );
            case 2 -> new EffectOr(
                    new TransformationFixed(Arrays.asList(Resource.Yellow, Resource.Yellow), List.of(Resource.Car), 0),
                    new TransformationFixed(Arrays.asList(Resource.Yellow, Resource.Yellow), Arrays.asList(Resource.Car, Resource.Car), 1)
            );
            case 3 -> new EffectOr(
                    new TransformationFixed(Arrays.asList(Resource.Green, Resource.Red), List.of(Resource.Bulb), 0),
                    new TransformationFixed(Arrays.asList(Resource.Green, Resource.Red), Arrays.asList(Resource.Bulb, Resource.Gear, Resource.Pollution, Resource.Pollution), 1)
            );
            case 4 -> new EffectOr(
                    new TransformationFixed(Arrays.asList(Resource.Bulb, Resource.Gear), List.of(Resource.Car), 0),
                    new TransformationFixed(List.of(Resource.Gear), Arrays.asList(Resource.Car, Resource.Money, Resource.Pollution, Resource.Pollution), 1)
            );
            case 5 -> new EffectOr(
                    new ArbitraryBasic(2, List.of(Resource.Money)),
                    new ArbitraryBasic(2, Arrays.asList(Resource.Money, Resource.Money, Resource.Pollution))
            );
            case 6 -> new EffectOr(
                    new TransformationFixed(Arrays.asList(Resource.Green, Resource.Green, Resource.Green), List.of(Resource.Car), 0),
                    new TransformationFixed(Arrays.asList(Resource.Green, Resource.Green), List.of(Resource.Car), 1)
            );
            case 7 -> new EffectOr(
                    new TransformationFixed(Arrays.asList(Resource.Red, Resource.Yellow), List.of(Resource.Bulb), 0),
                    new TransformationFixed(Arrays.asList(Resource.Red, Resource.Yellow), Arrays.asList(Resource.Bulb, Resource.Gear, Resource.Gear, Resource.Pollution, Resource.Pollution), 1)
            );
            case 8 -> new EffectOr(
                    new ArbitraryOutput(List.of(Resource.Money), 1),
                    new ArbitraryOutput(List.of(Resource.Money, Resource.Money), 3)
            );
            case 9 -> new EffectOr(
                    new TransformationFixed(Arrays.asList(Resource.Car, Resource.Gear), List.of(Resource.Money), 0),
                    new TransformationFixed(List.of(Resource.Car), Arrays.asList(Resource.Money, Resource.Money), 2)
            );
            case 10 -> new EffectOr(
                    new ArbitraryInOut(1, 1),
                    new ArbitraryInOut(1, 2)
            );
            case 11 -> new EffectOr(
                    new TransformationFixed(Arrays.asList(Resource.Gear, Resource.Gear), List.of(Resource.Car), 0),
                    new TransformationFixed(List.of(Resource.Gear), List.of(Resource.Car, Resource.Pollution), 1)
            );
            default -> new ArbitraryBasic(2, List.of(Resource.Money));
        };
    }
}