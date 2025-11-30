package main.java.com.terrafutura.api;

import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.cards.Pair;
import main.java.com.terrafutura.piles.CardSource;
import main.java.com.terrafutura.piles.Deck;
import main.java.com.terrafutura.resources.Resource;

import java.util.List;
import java.util.Optional;

public interface TerraFuturaInterface {
    boolean takeCard(int playerID, CardSource cardSource, GridPosition destination);
    boolean discardCard(int playerID, Deck deck);
    void activateCard(int playerID, GridPosition card,
                      List<Pair<Resource, GridPosition>> inputs, List<Pair<Resource, GridPosition>> outputs,
                      List<GridPosition> pollution, Optional<Integer> otherPlayerId, Optional<GridPosition> otherCard);
    void selectReward(int playerID, Resource resource);
    boolean turnFinished(int playerID);
    boolean selectActivationPattern(int playerID, int card);
    boolean selectScoring(int playerID, int card);
}
