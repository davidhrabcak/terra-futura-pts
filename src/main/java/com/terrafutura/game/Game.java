package main.java.com.terrafutura.game;

import main.java.com.terrafutura.api.TerraFuturaInterface;
import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.cards.Pair;
import main.java.com.terrafutura.piles.*;
import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//TODO add state() calls so players know what is happening
public class Game implements TerraFuturaInterface {
    private GameState state;
    public final List<Player> players;
    private int onTurn, startingPlayer, turnNumber; // startingPlayer only used by GUI
    private Card selectReward;
    private final Pile i, ii;


    public Game(int playerNumber, int startingPlayerIndex) {
        selectReward = null;
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
        if (onTurn != playerId || state != GameState.TakeCardCardDiscarded && state != GameState.TakeCardNoCardDiscarded) return false;

        Player p = players.get(playerId);
        Optional<Card> card;
        switch (source.deck) {
            case I -> card = i.takeCard(source.index);
            case II -> card = ii.takeCard(source.index);
            case null, default -> { return false; }
        };
        if (!p.g.canPutCard(destination) || card.isEmpty()) return false;
        p.g.putCard(destination, card.get());
        state = GameState.ActivateCard;
        return true;
    }

    @Override
    public boolean discardLastCardFromDeck(int playerId, Deck deck) {
        if (state != GameState.TakeCardNoCardDiscarded || onTurn != playerId) return false;
        boolean success;
        switch (deck) {
            case I -> success = i.removeLastCard();
            case II -> success = ii.removeLastCard();
            case null, default -> { return false; }
        };
        state = GameState.TakeCardCardDiscarded;
        return success;
    }

    @Override
    public boolean activateCard(int playerId, GridPosition card, List<Pair<List<Resource>, GridPosition>> inputs, List<Pair<List<Resource>, GridPosition>> outputs, List<GridPosition> pollution, Optional<Integer> otherPlayerId, Optional<GridPosition> otherCard) {
        if (state != GameState.ActivateCard || onTurn != playerId) return false;
        Player p = players.get(playerId);
        if (p.g.getCard(card).isEmpty() || p.g.canBeActivated(card)) return false;
        p.g.setActivated(card);

        for (int i = 0; i < inputs.size(); i++) {
            Pair<List<Resource>, GridPosition> entryInput = inputs.get(i);
            Pair<List<Resource>, GridPosition> entryOutput = outputs.get(i);
            int entryPollution = (pollution.contains(entryInput.getSecond())) ? 1 : 0;
            Optional<Card> cardOptional = p.g.getCard(entryInput.getSecond());
            if (cardOptional.isEmpty()) continue;
            if (cardOptional.get().check(entryInput.getFirst(), entryOutput.getFirst(), entryPollution)) {
                if (cardOptional.get().checkLower(entryInput.getFirst(), entryOutput.getFirst(), entryPollution)) {
                    state = GameState.SelectReward; // both effect possible - choose one
                    return true;
                }
            } else continue; // invalid input or output for both effects
            if (otherPlayerId.isEmpty() && cardOptional.get().hasAssistance()) {
                if (otherCard.isEmpty()) return false;
                // do stuff with otherCard - Assistance is not implemented in this project
            }
        }
        selectReward = p.g.getCard(card).get();
        if (turnNumber == 9) state = GameState.SelectActivationPattern;
        else turnFinished(playerId);
        return true;
    }

    @Override
    public void selectReward(int playerId, Resource resource) { //TODO depends on implementation of Effect and Card

        if (state != GameState.SelectReward || playerId != onTurn) {
            throw new RuntimeException("Illegal turn action selectReward");
        }
        Player p = players.get(playerId);

    }

    @Override
    public boolean turnFinished(int playerId) {
        if (playerId >= players.size() || onTurn != playerId) return false;
        state = GameState.TakeCardNoCardDiscarded;
        onTurn = (onTurn + 1 >= players.size()) ? 0 : onTurn + 1;
        return true;
    }

    @Override
    public boolean selectActivationPattern(int playerId, int card) {
        if (onTurn != playerId || state != GameState.SelectActivationPattern || card > 1 || card < 0) return false;
        Player p = players.get(playerId);
        if (card == 0 && !p.a2.isSelected()) {
            p.a1.select();
        } else p.a2.select();
        state = GameState.SelectScoringMethod;
        return true;
    }

    @Override
    public boolean selectScoring(int playerId, int card) {
        if (onTurn != playerId || state != GameState.SelectScoringMethod || card > 1 || card < 0) return false;
        Player p = players.get(playerId);
        if (card == 0) p.s1.selectThisMethodAndCalculate();
        else p.s2.selectThisMethodAndCalculate();
        return true;
    }
}