package main.java.com.terrafutura.game;

import main.java.com.terrafutura.api.TerraFuturaInterface;
import main.java.com.terrafutura.api.TerraFuturaObserverInterface;
import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.*;
import main.java.com.terrafutura.piles.*;
import main.java.com.terrafutura.resources.Resource;

import java.util.*;

public class Game implements TerraFuturaInterface {
    private GameState state;
    public final List<Player> players;
    private int onTurn, startingPlayer, turnNumber; // startingPlayer only used by GUI
    private SelectReward selectReward;
    private final Pile i, ii;
    private final GameObserver observers;


    public Game(int playerNumber, int startingPlayerIndex, List<GameObserver> observers) {
        selectReward = null;
        i = new Pile(); // mock use - in real implementation, the actual cards
        ii = new Pile();// would be stored in some data class
        if (playerNumber < 2 || playerNumber > 4) {
            throw new IllegalArgumentException("Game: Invalid number of players");
        }

        state = null;
        this.players = new ArrayList<>();
        Map<Integer, TerraFuturaObserverInterface> map = new HashMap<>();
        for (int i = 0; i < playerNumber; i++) {
            Player p = setupPlayer(i);
            players.add(p);
            onTurn = startingPlayerIndex;
            startingPlayer = startingPlayerIndex;
            turnNumber = 1;
            map.put(i, new TerraFuturaObserverInterface() {
                @Override
                public void notify(String gameState) {
                    // does something
                }
            });
        }
        this.observers = new GameObserver(map);
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
    public boolean activateCard(int playerId, GridPosition card, List<Pair<Resource, GridPosition>> inputs, List<Pair<Resource, GridPosition>> outputs, List<GridPosition> pollution, Optional<Integer> otherPlayerId, Optional<GridPosition> otherCard) {
        if (state != GameState.ActivateCard || onTurn != playerId) return false;
        Player p = players.get(playerId);
        if (p.g.getCard(card).isEmpty() || p.g.canBeActivated(card)) return false;

        boolean check;

        if (otherPlayerId.isPresent() && otherCard.isPresent()
                && p.g.getCard(otherCard.get()).isPresent()) {
            ProcessActionAssistance paa = new ProcessActionAssistance();
            check = paa.activateCard(p.g.getCard(card).get(), p.g, otherPlayerId.get(), p.g.getCard(otherCard.get()).get()
                    ,inputs, outputs, pollution );
        } else {
            ProcessAction pa = new ProcessAction();
            check = pa.activateCard(p.g.getCard(card).get(), p.g, inputs, outputs, pollution);
        }
        if (!check) return false;

        if (turnNumber == 9) state = GameState.SelectActivationPattern;
        else turnFinished(playerId);
        return true;
    }

    @Override
    public void selectReward(int playerId, Resource resource) { // unclear how do you get to this state :/
        if (state != GameState.SelectReward || playerId != onTurn) {
            System.err.println("Action not possible");
        }
        // assumes selectReward was set somewhere...
        if (selectReward.canSelectReward(resource)) {
            selectReward.selectReward(resource);
            observers.notifyAll(Map.of(playerId, ("Selecting " + resource + " from " + selectReward.state())));
        }


    }

    @Override
    public boolean turnFinished(int playerId) {
        if (playerId >= players.size() || onTurn != playerId) return false;
        state = GameState.TakeCardNoCardDiscarded;
        onTurn = (onTurn + 1 >= players.size()) ? 0 : onTurn + 1;
        observers.notifyAll(Map.of(playerId, "Turn of Player " + playerId + " finished, Player " + onTurn + " is next."));
        players.get(playerId).g.endTurn();
        players.get(onTurn).g.beginTurn();
        if (onTurn == 0 && turnNumber != 0) turnNumber++;
        return true;
    }

    @Override
    public boolean selectActivationPattern(int playerId, int card) {
        if (onTurn != playerId || state != GameState.SelectActivationPattern || card > 1 || card < 0) return false;
        Player p = players.get(playerId);
        if (card == 0 && !p.a2.isSelected()) {
            observers.notifyAll(Map.of(playerId, "Selecting Activation pattern " +  p.a1.state()));
            p.a1.select();
        } else {
            p.a2.select();
            observers.notifyAll(Map.of(playerId, "Selecting Activation pattern " + p.a2.state()));
        }
        state = GameState.SelectScoringMethod;
        return true;
    }

    @Override
    public boolean selectScoring(int playerId, int card) {
        if (onTurn != playerId || state != GameState.SelectScoringMethod || card > 1 || card < 0) return false;
        Player p = players.get(playerId);
        if (card == 0) {
            observers.notifyAll(Map.of(playerId, "Selecting scoring method " +p.s1.state()));
            p.s1.selectThisMethodAndCalculate();
        }
        else {
            observers.notifyAll(Map.of(playerId, "Selecting scoring method " + p.s2.state()));
            p.s2.selectThisMethodAndCalculate();
        }
        return true;
    }
}