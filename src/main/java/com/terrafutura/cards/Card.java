package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.resources.Resource;

import java.util.*;

public class Card {

    private final List<Resource> resources;
    private final int pollutionSpaces;
    private Effect effect;
    private Effect lowerEffect;

    public Card(List<Resource> resources, int pollutionSpaces) {
        this.resources = new ArrayList<>(resources);
        this.pollutionSpaces = pollutionSpaces;
    }


    public Card(int pollutionSpaces, Optional<Effect> effect, Optional<Effect> lowerEffect) {
        // needs implementation
        resources = new ArrayList<>();
        this.pollutionSpaces = pollutionSpaces;
        this.effect = effect.orElse(null);
        this.lowerEffect = lowerEffect.orElse(null);
    }

    public boolean canGetResources(List<Resource> resources) {
        return new HashSet<>(this.resources).containsAll(resources);
    }

    public List<Resource> getResources() {
        return resources;
    }

    public boolean canPutResources(List<Resource> resources) {
        return true;
    }

    public void putResources(List<Resource> resources) {
        this.resources.addAll(resources);
    }
    
    public void removeResource(Resource resource){}

    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        return false;
    }

    public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
        return false;
    }

    public boolean hasAssistance() {
        return false;
    }

    public String state() {
        return "Card{resources="+resources+", pollutionSpaces="+pollutionSpaces+"}";
    }
}