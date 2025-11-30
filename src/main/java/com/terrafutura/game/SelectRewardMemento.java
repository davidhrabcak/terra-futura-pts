package main.java.com.terrafutura.game;

import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.List;

public record SelectRewardMemento(Card card, Card otherCard, int playerId, int otherPlayerId, List<Resource> reward) {
    public SelectRewardMemento(Card card, Card otherCard, int playerId, int otherPlayerId, List<Resource> reward) {
        this.card = card;
        this.otherCard = otherCard;
        this.playerId = playerId;
        this.otherPlayerId = otherPlayerId;
        this.reward = new ArrayList<>(reward);
    }
}
