package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SelectReward {
    private Optional<Integer> playerId;
    private  List<Resource> selection;
    private Card card;
    public Resource selected = null;

    public SelectReward() {
        playerId = Optional.empty();
        card = null;
    }

    public boolean setReward(int playerId, Card card, List<Resource> reward) {
        if (card == null || reward.isEmpty()) return false;
        this.playerId = Optional.of(playerId);
        selection = new ArrayList<>(reward);
        this.card = card;
        return true;
    }

    public boolean canSelectReward(Resource resource) {
        if (playerId.isEmpty()) return false;
        return selection.contains(resource);
    }

    public Resource selectReward(Resource resource) {
        if (playerId.isEmpty() || !selection.contains(resource)) return null;
        else {
            selected = resource;
            return resource;
        }
    }

    public String state() {
        StringBuilder s = new StringBuilder(20);
        s.append("Select a reward: ");
        for (Resource r : selection) {
            s.append(r).append(", ");
        }
        s.append('\n');
        return s.toString();
    }

}
