package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.resources.Resource;

import java.util.*;

public class Card {

    private final List<Resource> resources;
    private final int pollutionSpaces;
    private Effect upperEffect;
    private Effect lowerEffect;

    public Card(int pollutionSpaces, Effect upperEffect, Effect lowerEffect) {
        this.resources = new ArrayList<>();
        this.pollutionSpaces = pollutionSpaces;
        this.upperEffect = upperEffect;
        this.lowerEffect = lowerEffect;
    }


    public Card(int pollutionSpaces, Optional<Effect> effect, Optional<Effect> lowerEffect) {
        resources = new ArrayList<>();
        this.pollutionSpaces = pollutionSpaces;
    }

    public boolean canGetResources(List<Resource> requested) {
        int actualPollution = (int)resources.stream().filter(r -> r == Resource.Pollution).count();
        // we can only get resources from active cards
        if (actualPollution > pollutionSpaces) {
            return false;
        }
        return new HashSet<>(this.resources).containsAll(requested);
    }

    public List<Resource> getResources() {
        if (!canGetResources(resources)) {
            throw new IllegalStateException("Cannot get resources, because card is not active");
        }
        return resources;
    }

    public void removeResource(Resource resource) {
        resources.remove(resource);
    }

    public boolean isActive() {
        return getPollutionCount() <= pollutionSpaces;
    }

    private int getPollutionCount() {
        return (int) resources.stream().filter(r -> r == Resource.Pollution).count();
    }


    public boolean canPutResources(List<Resource> toAdd) {
        // count pollution in the resources we want to add
        int newPollution = (int) toAdd.stream().filter(r -> r == Resource.Pollution).count();

        if (newPollution == 0) {
            return true; // we are adding only normal resources
        }

        // count current pollution
        int currentPollution = (int)resources.stream().filter(r -> r == Resource.Pollution).count();

        // we can only add pollutionSpaces + 1 pollution
        return currentPollution + newPollution <= pollutionSpaces + 1;
    }

    public void putResources(List<Resource> toAdd) {
        if (!canPutResources(toAdd)) {
            throw new IllegalStateException("Cannot add resources");
        }
        this.resources.addAll(toAdd);
    }

    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        return upperEffect.check(input, output, pollution);
    }

    public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
        if (lowerEffect != null) {
            return lowerEffect.check(input, output, pollution);
        }
        return false;
    }

    public boolean hasAssistance() {
        return false;
    }

    public String state() {
        return "Card{resources="+resources+", pollutionSpaces="+pollutionSpaces+"}";
    }
}