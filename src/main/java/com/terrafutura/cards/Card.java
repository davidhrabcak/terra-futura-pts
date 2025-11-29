package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.resources.Resource;

import java.util.*;

public class Card {

    private final List<Resource> resources;
    private final int pollutionSpaces;
    private int pollution = 0;

    public Card(List<Resource> resources, int pollutionSpaces) {
        this.resources = new ArrayList<>(resources);
        this.pollutionSpaces = pollutionSpaces;
    }


    public Card(int pollutionSpaces, Optional<Effect> effect, Optional<Effect> lowerEffect) {
        // needs implementation
        resources = new ArrayList<>();
        this.pollutionSpaces = pollutionSpaces;
    }

    public boolean canGetResources(List<Resource> resources) {
        return new HashSet<>(this.resources).containsAll(resources);
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void removeResource(Resource resource) {
        resources.remove(resource);
    }
    public void addPollution(){
        if(canAddPollution()) {
            pollution++;
        }else {
            throw new IllegalStateException("Card has no pollution spaces left");
        }
    }
    public void removePollution(){
        pollution--;
    }
    public int getPollution(){
        return pollution;
    }
    public boolean canAddPollution(){
        return pollution < pollutionSpaces + 1 ;
    }
    public boolean isActive(){
        return pollution <= pollutionSpaces;
    }


    public boolean canPutResources(List<Resource> resources) {
        return true;
    }

    public void putResources(List<Resource> resources) {
        this.resources.addAll(resources);
    }

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