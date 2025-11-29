package main.java.com.terrafutura.game;

import main.java.com.terrafutura.api.TerraFuturaInterface;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.Pair;
import main.java.com.terrafutura.piles.CardSource;
import main.java.com.terrafutura.piles.Deck;
import main.java.com.terrafutura.resources.Resource;

import java.util.List;
import java.util.Optional;

public class Game implements TerraFuturaInterface {
    private GameState currentState;
    private int[] playerIDs;
    private int currentPlayerID;
    private int turnCounter;
    private GameObserver observer;
    private List<Player> players;
    private Player startingPlayer;

    public Game(int playerCount){
        if (playerCount < 2 || playerCount > 4){
            throw new IllegalArgumentException("Player count must be between 2 and 4");
        }

    }

    @Override
    public boolean takeCard(int playerID, CardSource cardSource, GridPosition destination) {
        return false;
    }

    @Override
    public boolean discardCard(int playerID, Deck deck) {
        return false;
    }

    @Override
    public void activateCard(int playerID, GridPosition card, List<Pair<Resource, GridPosition>> inputs, List<Pair<Resource, GridPosition>> outputs, List<GridPosition> pollution, Optional<Integer> otherPlayerId, Resource resource) {

    }

    @Override
    public void selectReward(int playerID, Resource resource) {

    }

    @Override
    public boolean turnFinished(int playerID) {
        return false;
    }

    @Override
    public boolean selectActivationPattern(int playerID, int card) {
        return false;
    }

    @Override
    public boolean selectScoring(int playerID, int card) {
        return false;
    }
}
