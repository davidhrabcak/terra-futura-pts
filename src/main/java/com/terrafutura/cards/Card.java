package main.java.com.terrafutura.cards;

import main.java.com.terrafutura.resources.Resource;

import java.util.*;

/**
 * Represents a card placed in the player's grid.
 * Stores resources, handles pollution blocking, delegates checks to its effects and may offer assistance.
 */
public class Card {

    /**
     * List of resources currently stored on the card.
     */
    private final List<Resource> resources;

    /**
     * Number of pollution spaces remaining before the card becomes blocked.
     * A negative value indicates that the card is blocked.
     */
    private int pollutionSpacesLeft;

    /**
     * True if the card is fully blocked by pollution and cannot produce resources.
     */
    private boolean isBlockedByPollution;

    /**
     * Upper (primary) effect of this card.
     */
    private final Optional<Effect> effect;

    /**
     * Lower effect of this card.
     */
    private final Optional<Effect> lowerEffect;


    /**
     * Creates a card with the given pollution capacity and effects.
     *
     * @param pollutionSpaces number of pollution spaces before card becomes blocked
     * @param effect          upper effect of the card
     * @param lowerEffect     lower effect of the card
     */
    public Card(int pollutionSpaces, Optional<Effect> effect, Optional<Effect> lowerEffect) {
        resources = new ArrayList<>();
        pollutionSpacesLeft = pollutionSpaces;
        isBlockedByPollution = false;
        this.effect = effect;
        this.lowerEffect = lowerEffect;
    }

    /**
     * Creates a card with the given pollution capacity and effect.
     *
     * @param pollutionSpaces number of pollution spaces before card becomes blocked
     * @param effect          upper effect of the card
     */
    public Card(int pollutionSpaces, Effect effect) {
        resources = new ArrayList<>();
        pollutionSpacesLeft = pollutionSpaces;
        isBlockedByPollution = false;
        this.effect = Optional.of(effect);
        this.lowerEffect = Optional.empty();
    }

    /**
     * Creates a card with the given pollution capacity and effects.
     *
     * @param pollutionSpaces number of pollution spaces before card becomes blocked
     * @param effect          upper effect of the card
     * @param lowerEffect     lower effect of the card
     */
    public Card(int pollutionSpaces, Effect effect, Effect lowerEffect) {
        resources = new ArrayList<>();
        pollutionSpacesLeft = pollutionSpaces;
        isBlockedByPollution = false;
        this.effect = Optional.of(effect);
        this.lowerEffect = Optional.of(lowerEffect);
    }

    /**
     * Checks if the card contains all requested resources.
     * Blocked cards cannot provide any resources unless requesting pollution to be removed.
     *
     * @param resources list of resources to check
     * @return true if all requested resources are present
     */
    public boolean canGetResources(List<Resource> resources) {
        if (isBlockedByPollution) {
            for (Resource resource : resources) {
                if (!resource.equals(Resource.Pollution)) {
                    return false;
                }
            }
        }
        return new HashSet<>(this.resources).containsAll(resources);
    }

    /**
     * Returns the resources currently stored on the card.
     *
     * @return list of resources
     */
    public List<Resource> getResources() {
        return resources;
    }

    /**
     * Checks whether new resources may be placed on this card.
     * This requires not overpassing the limit of pollution that can be placed on this card.
     * If the card is blocked by pollution, it cannot accept any resources.
     *
     * @param resources resources to add
     * @return true if new resources can be added
     */
    public boolean canPutResources(List<Resource> resources) {
        if (isBlockedByPollution) return false;
        int currentPollution = (int) this.resources.stream().filter(r -> r == Resource.Pollution).count();
        int pollutionToPut = (int) resources.stream().filter(r -> r == Resource.Pollution).count();
        return currentPollution + pollutionToPut <= pollutionSpacesLeft + 1;
    }

    /**
     * Adds the specified resources to this card.
     * Handles blocking the card by adding pollution.
     *
     * @param resources list of resources to add
     */
    public void putResources(List<Resource> resources) {
        this.resources.addAll(resources);
        int newPollution = (int) resources.stream().filter(r -> r == Resource.Pollution).count();
        for (int i = 0; i < newPollution; i++) {
            pollutionSpacesLeft--;
            if (pollutionSpacesLeft < 0) {
                isBlockedByPollution = true;
            }
        }
    }

    /**
     * Removes a single resource from this card.
     * Handles unblocking the card if pollution is removed.
     *
     * @param resource resource to remove
     */
    public void removeResource(Resource resource) {
        this.resources.remove(resource);
        if (resource.equals(Resource.Pollution)) {
            pollutionSpacesLeft++;
            if (pollutionSpacesLeft > 0) {
                isBlockedByPollution = false;
            }
        }
    }

    /**
     * Removes specified resources from this card.
     *
     * @param resources resources to remove
     */
    public void removeResources(List<Resource> resources) {
        for (Resource resource : resources) {
            removeResource(resource);
        }
    }
    
    public void removeResource(Resource resource){}

    /**
     * Checks whether this card's upper effect may be used for the given input and output resource lists.
     *
     * @param input list of resources that the effect attempts to consume
     * @param output list of resources that the effect attempts to produce
     * @param pollution non-zero value if the card is blocked by pollution
     * @return true if the effect may be used
     */
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        return effect.map(value -> value.check(input, output, pollution)).orElse(false);
    }

    /**
     * Checks whether this card's lower effect may be used for the given input and output resource lists.
     *
     * @param input list of resources that the effect attempts to consume
     * @param output list of resources that the effect attempts to produce
     * @param pollution non-zero value if the card is blocked by pollution
     * @return true if the effect may be used
     */
    public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
        return lowerEffect.map(value -> value.check(input, output, pollution)).orElse(false);
    }

    /**
     * Checks whether this card can support assistance.
     *
     * @return true if card supports assistance
     */
    public boolean hasAssistance() {
        return lowerEffect.map(Effect::hasAssistance).orElse(false);
    }

    /**
     * @return readable representation of the card state
     */
    public String state() {
        StringBuilder sb = new StringBuilder();

        sb.append("Card{resources=").append(resources);
        sb.append(", pollutionSpacesLeft=").append(pollutionSpacesLeft);
        sb.append(", isBlockedByPollution=").append(isBlockedByPollution);
        effect.ifPresent(value -> sb.append(", effect=").append(value.state()));
        lowerEffect.ifPresent(value -> sb.append(", lowerEffect=").append(value.state()));
        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toString() {
        return state();
    }
}