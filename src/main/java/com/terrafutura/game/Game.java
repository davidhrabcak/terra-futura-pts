package main.java.com.terrafutura.game;

import main.java.com.terrafutura.api.TerraFuturaInterface;
import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.cards.Pair;
import main.java.com.terrafutura.piles.*;
import main.java.com.terrafutura.resources.Resource;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game implements TerraFuturaInterface {
    public GameState state;
    public final List<Player> players;
    private int onTurn, startingPlayer, turnNumber;
    private final Pile i, ii;


    public Game(int playerNumber, int startingPlayerIndex) {
        i = new Pile();
        ii = new Pile();
        if (playerNumber < 2 || playerNumber > 4) {
            throw new IllegalArgumentException("Game: Invalid number of players");
        }
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
        Optional<Card> card;
        switch (source.deck) {
            case I -> card = i.takeCard(source.index);
            case II -> card = ii.takeCard(source.index);
            case null, default -> { return false; }
        };
        if (!p.g.canPutCard(destination) || card.isEmpty()) return false;
        p.g.putCard(destination, card.get());
        return true;
    }

    @Override
    public boolean discardLastCardFromDeck(int playerId, Deck deck) {
        boolean success;
        switch (deck) {
            case I -> success = i.removeLastCard();
            case II -> success = ii.removeLastCard();
            case null, default -> { return false; }
        };
        return success;
    }

    @Override
    public boolean activateCard(int playerId, GridPosition card, List<Pair<List<Resource>, GridPosition>> inputs, List<Pair<List<Resource>, GridPosition>> outputs, List<GridPosition> pollution, Optional<Integer> otherPlayerId, Optional<GridPosition> otherCard) {
        Player p = players.get(playerId);
        if (p.g.getCard(card).isEmpty() || p.g.canBeActivated(card)) return false;
        p.g.setActivated(card);

        for (Pair<List<Resource>, GridPosition> entry : inputs) { // cele zleeeeeeeeeeee treba cez i a naraz robit input aj output
            Optional<Card> cardOptional = p.g.getCard(entry.getSecond());
            if (cardOptional.isEmpty()
                    || !cardOptional.get().canPutResources(entry.getFirst())
                    || ! cardOptional.get().canGetResources()) continue;

        }
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