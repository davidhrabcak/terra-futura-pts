package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SelectReward {
    private Optional<Integer> playerId;
    private  List<Resource> selection;
    private Card card;

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

    }

}
