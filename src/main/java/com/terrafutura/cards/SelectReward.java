package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Manages reward selection for players in the Terra Futura game.
 * Handles the process of setting available rewards and allowing players to select them.
 */
public class SelectReward {
    private Optional<Integer> player = Optional.empty();
    private List<Resource> selection = new ArrayList<>();
    private Card rewardCard;

    /**
     * Sets the reward configuration for a specific player and card.
     *
     * @param player The ID of the player who can select the reward
     * @param card The card that will receive the selected resource
     * @param reward Array of available resources that can be selected as reward
     */
    public void setReward(int player, Card card, Resource[] reward) {
        this.player = Optional.of(player);
        this.rewardCard = card;
        this.selection = Arrays.asList(reward);
    }

    /**
     * Checks if a specific resource can be selected as a reward by the current player.
     *
     * @param resource The resource to check for selection eligibility
     * @return true if the resource can be selected, false otherwise
     */
    public boolean canSelectReward(Resource resource) {
        return player.isPresent() && selection.contains(resource) && rewardCard != null;
    }

    /**
     * Selects a specific resource as a reward and applies it to the reward card.
     * Resets the selection state after a successful reward application.
     *
     * @param resource The resource to select as a reward
     * @throws IllegalStateException if the resource cannot be selected
     */
    public void selectReward(Resource resource) {
        if (!canSelectReward(resource)) {
            throw new IllegalStateException("Cannot select this reward");
        }
        rewardCard.putResources(List.of(resource));
        reset();
    }

    /**
     * Resets the selection state, clearing the player, reward card and available selections.
     */
    private void reset() {
        this.player = Optional.empty();
        this.rewardCard = null;
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

        sb.append(", card: ");
        if (rewardCard != null) {
            sb.append(rewardCard);
        } else {
            sb.append("null");
        }

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
