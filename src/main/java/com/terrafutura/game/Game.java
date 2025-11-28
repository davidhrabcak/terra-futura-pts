package main.java.com.terrafutura.game;

import main.java.com.terrafutura.api.TerraFuturaInterface;
import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.Pair;
import main.java.com.terrafutura.piles.ActivationPatterns;
import main.java.com.terrafutura.piles.CardSource;
import main.java.com.terrafutura.piles.Deck;
import main.java.com.terrafutura.piles.ScoringMethods;
import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game implements TerraFuturaInterface {
    public GameState state;
    public final List<Player> players;
    private int onTurn, startingPlayer, turnNumber;


    public Game(int playerNumber, int startingPlayerIndex) {
        state = null;
        this.players = new ArrayList<>();
        for (int i = 0; i < playerNumber; i++) {
            Player p = setupPlayer(i);
            players.add(p);
            onTurn = startingPlayerIndex;
            startingPlayer = startingPlayerIndex;
            turnNumber = 1;
        }
    }

    private Player setupPlayer(int id) {
        Grid g = new Grid();
        ActivationPatterns a = new ActivationPatterns(g);
        ScoringMethods s = new ScoringMethods();
        return new Player(id, s.getRandomScoringMethod(), s.getRandomScoringMethod(), a.getRandomActivationPattern(), a.getRandomActivationPattern(), g);
    }

    @Override
    public boolean takeCard(int playerId, CardSource source, GridPosition destination) {
        Player p = players.get(playerId);
        source.deck.
    }

    @Override
    public boolean discardLastCardFromDeck(int playerId, Deck deck) {
        return false;
    }

    @Override
    public boolean activateCard(int playerId, GridPosition card, List<Pair<Resource, GridPosition>> inputs, List<Pair<Resource, GridPosition>> outputs, List<GridPosition> pollution, Optional<Integer> otherPlayerId, Optional<GridPosition> otherCard) {
        return false;
    }

    @Override
    public void selectReward(int playerId, Resource resource) {

    }

    @Override
    public boolean turnFinished(int playerId) {
        return false;
    }

    @Override
    public boolean selectActivationPattern(int playerId, int card) {
        return false;
    }

    @Override
    public boolean selectScoring(int playerId, int card) {
        return false;
    }
}