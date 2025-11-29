package main.java.com.terrafutura.api;

import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.Pair;
import main.java.com.terrafutura.piles.CardSource;
import main.java.com.terrafutura.piles.Deck;
import main.java.com.terrafutura.resources.Resource;

import java.util.List;
import java.util.Optional;

public interface TerraFuturaInterface {
    boolean takeCard(int playerId, CardSource source, GridPosition destination);
    boolean discardLastCardFromDeck(int playerId, Deck deck);
    boolean activateCard(int playerId, GridPosition card,
                         List<Pair<List<Resource>, GridPosition>> inputs,
                         List<Pair<List<Resource>, GridPosition>> outputs,
                         List<GridPosition> pollution,
                         Optional<Integer> otherPlayerId, Optional<GridPosition> otherCard);
    void selectReward(int playerId, Resource resource);
    boolean turnFinished(int playerId);
    boolean selectActivationPattern(int playerId, int card);
    boolean selectScoring(int playerId, int card);
}