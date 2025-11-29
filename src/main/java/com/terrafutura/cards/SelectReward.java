package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SelectReward {
    private Optional<Integer> player = Optional.empty();
    private List<Resource> selection = new ArrayList<>();
    private Optional<Card> rewardCard = Optional.empty();

    public void setReward(int player, Card card, Resource[] reward) {
        this.player = Optional.of(player);
        this.rewardCard = Optional.of(card);
        this.selection = Arrays.asList(reward);
    }

    public boolean canSelectReward(Resource resource) {
        return player.isPresent() &&
                selection.contains(resource);
    }

    public void selectReward(Resource resource) {
        if (!canSelectReward(resource)) {
            throw new IllegalStateException("Cannot select this reward");
        }
        //???

        reset();
    }

    private void reset() {
        this.player = Optional.empty();
        this.rewardCard = Optional.empty();
        this.selection.clear();
    }

    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("SelectReward { player: ");
        if (player.isPresent()) {
            sb.append(player.get());
        } else {
            sb.append("null");
        }

        // Card
        sb.append(", card: ");
        if (rewardCard.isPresent()) {
            sb.append(rewardCard.get().getClass().getSimpleName());
        } else {
            sb.append("null");
        }

        // Selection
        sb.append(", selection: [");
        for (int i = 0; i < selection.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(selection.get(i));
        }
        sb.append("]}");
        return sb.toString();
    }
}
