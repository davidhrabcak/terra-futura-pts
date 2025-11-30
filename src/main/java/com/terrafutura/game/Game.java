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

    public int getOnTurn() { // used in gameObservers
        return onTurn;
    }

    public int getStartingPlayer() { // used in gameObservers
        return startingPlayer;
    }

    public int getTurnNumber() { // used in gameObservers
        return turnNumber;
    }

    private int onTurn, startingPlayer, turnNumber; // startingPlayer only used by GUI
    private final ProcessActionAssistance paa; // used in activateCard and selectReward
    private SelectRewardMemento selectRewardMemento;
    private final Pile i, ii;
    private final GameObserver observers;
    private final MoveCard m = new MoveCard(); // used in takeCard


    public Game(int playerNumber, int startingPlayerIndex, List<GameObserver> observers, long seed) {
        i = new Pile(seed); // mock use - in real implementation, the actual cards
        ii = new Pile(seed);// would be stored in some data class
        if (playerNumber < 2 || playerNumber > 4) {
            throw new IllegalArgumentException("Game: Invalid number of players");
        }
        paa = new ProcessActionAssistance();
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
                    // does something useful to the player
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
        if (onTurn != playerId || (state != GameState.TakeCardCardDiscarded && state != GameState.TakeCardNoCardDiscarded)) return false;

        Player p = players.get(playerId);
        switch (source.deck) {
            case I -> m.moveCard(i, destination, p.g);
            case II -> m.moveCard(ii, destination, p.g);
            case null, default -> { return false; }
        };
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
                && players.get(otherPlayerId.get()).g.getCard(otherCard.get()).isPresent()) {
            check = paa.activateCard(p.g.getCard(card).get(), p.g, otherPlayerId.get(),
                    players.get(otherPlayerId.get()).g.getCard(otherCard.get()).get(),
                    inputs, outputs, pollution );
            if (check) {
                state = GameState.SelectReward;
                List<Resource> some_reward = new ArrayList<>(); // we get this list somewhere in paa...
                selectRewardMemento = new SelectRewardMemento(p.g.getCard(card).get(), players.get(otherPlayerId.get()).g.getCard(otherCard.get()).get(), playerId, otherPlayerId.get(),  some_reward);
                return true;
            }
            return false;
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
    public void selectReward(int playerId, Resource resource) {
        SelectReward r = new SelectReward();
        r.setReward(playerId, selectRewardMemento.card(), selectRewardMemento.reward());

        if (r.canSelectReward(resource) && onTurn == playerId
                 && state == GameState.SelectReward) {
            r.selectReward(resource);
        }

    }

    @Override
    public boolean turnFinished(int playerId) {
        if (playerId >= players.size() || onTurn != playerId) return false;
        onTurn = (onTurn + 1 >= players.size()) ? 0 : onTurn + 1;
        observers.notifyAll(Map.of(playerId, "Turn of Player " + playerId + " finished, Player " + onTurn + " is next."));
        players.get(playerId).g.endTurn();
        players.get(onTurn).g.beginTurn();
        if (onTurn == 0 && turnNumber != 0) turnNumber++;
        if (turnNumber > 10) state = GameState.TakeCardNoCardDiscarded;
        else if (turnNumber == 10) state = GameState.SelectActivationPattern;
        else state = GameState.SelectScoringMethod;
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
        state = GameState.ActivateCard;
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
        state = GameState.Finish;
        return true;
    }
}