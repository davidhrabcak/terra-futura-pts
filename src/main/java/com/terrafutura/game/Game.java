package main.java.com.terrafutura.game;

import main.java.com.terrafutura.api.TerraFuturaInterface;
import main.java.com.terrafutura.board.ActivationPattern;
import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.*;
import main.java.com.terrafutura.cards.effects.ArbitraryBasic;
import main.java.com.terrafutura.cards.effects.EffectOr;
import main.java.com.terrafutura.piles.CardSource;
import main.java.com.terrafutura.piles.Deck;
import main.java.com.terrafutura.piles.Pile;
import main.java.com.terrafutura.resources.Resource;
import main.java.com.terrafutura.scoring.Points;
import main.java.com.terrafutura.scoring.ScoringMethod;

import java.util.*;

public class Game implements TerraFuturaInterface {
    private GameState currentState;
    private int[] playerIDs;
    private GameObserver observer;
    private Map<Integer,Player> players;
    private TurnManger turnManager;
    private Random rnd = new Random();
    private Pile pile1;
    private Pile pile2;
    private CardFactory cardFactory;

    public Game(int playerCount, int startingPlayerID){
        if (playerCount < 2 || playerCount > 4){
            throw new IllegalArgumentException("Player count must be between 2 and 4");
        }

        if (startingPlayerID < 1 || startingPlayerID > playerCount){
            throw new IllegalArgumentException("Starting player must be between 1 and player count");
        }

        this.playerIDs = new int[playerCount];
        for (int i = 0; i < playerCount; i++){
            playerIDs[i] = i + 1;
        }

        this.currentState = GameState.TakeCardNoCardDiscarded;
        this.players = new HashMap<>();
        this.turnManager = new TurnManger(playerIDs, startingPlayerID);
        this.cardFactory = new CardFactory();
        this.pile1 = new Pile(cardFactory.createLevelIDeck());
        this.pile2 = new Pile(cardFactory.createLevelIIDeck());

        for(int playerID : playerIDs){
            //The third argument is null because we are implementing a game without assistance.
            //In complete implementation it would be effect with hasAssistance == True;
            Card startingCard = cardFactory.createStartingCard();
            Grid g = new Grid(startingCard);
            Player p = new Player(playerID, randomScoringMethod(), randomScoringMethod(), randomActivationPattern(g), randomActivationPattern(g), g);
            players.put(playerID, p);
        }

    }

    private ActivationPattern randomActivationPattern(Grid grid){
        Collection<AbstractMap.SimpleEntry<Integer,Integer>> position = List.of(new AbstractMap.SimpleEntry<>(rnd.nextInt(5) - 2, rnd.nextInt(5) - 2));
        return new ActivationPattern(grid, position);
    }
    private ScoringMethod randomScoringMethod(){
        int pointsPerCombination = rnd.nextInt(6);
        List<Resource> resources = new ArrayList<>();
        Resource r;
        int count;
        for (int i = 0; i < 2; i++){
            do {
                r = Resource.values()[rnd.nextInt(Resource.values().length)];
            }while (r.equals(Resource.Pollution));
            count = rnd.nextInt(3) + 1;
            for (int j = 0; j < count; j++){
                resources.add(r);
            }
        }
        return new ScoringMethod(resources,new Points(pointsPerCombination));
    }


    @Override
    public boolean takeCard(int playerID, CardSource cardSource, GridPosition destination) {

        //it has to be players's turn to take card
        if(!turnManager.isPlayerTurn(playerID)){
            return false;
        }

        //we can only take card in state TakeCardNoCardDiscarded or TakeCardCardDiscarded
        if (currentState != GameState.TakeCardNoCardDiscarded && currentState != GameState.TakeCardCardDiscarded) {
            return false;
        }

        Player player = players.get(playerID);
        Grid grid = player.getGrid();

        //check if the destination is valid
        if (!grid.canPutCard(destination)) {
            return false;
        }

        //which pile to take a card from?
        Pile targetPile = (cardSource.deck == Deck.I) ? pile1 : pile2;

        Optional<Card> cardOpt;
        cardOpt = targetPile.getCard(cardSource.index);

        if (cardOpt.isEmpty()) {
            return false;
        }

        Card card = cardOpt.get();
        grid.putCard(destination, card);

        //we must still terminate the card from that position and replace it with a new one if possible
        targetPile.takeCard(cardSource.index);

        currentState = GameState.ActivateCard;

        return true;
    }

    @Override
    public boolean discardCard(int playerID, Deck deck) {
        if(!turnManager.isPlayerTurn(playerID)){
            return false;
        }
        if (currentState != GameState.TakeCardNoCardDiscarded) {
            return false;
        }
        switch (deck){
            case I -> pile1.removeLastCard();
            case II -> pile2.removeLastCard();
            default -> {
                return false;
            }
        }
        currentState = GameState.TakeCardCardDiscarded;
        return true;
    }

    @Override
    public void activateCard(int playerID, GridPosition card, List<Pair<Resource, GridPosition>> inputs,
                             List<Pair<Resource, GridPosition>> outputs, List<GridPosition> pollution,
                             Optional<Integer> otherPlayerId, Optional<GridPosition> otherCard) {

        if (!turnManager.isPlayerTurn(playerID)){
            return;
        }
        if (currentState != GameState.ActivateCard){
            return;
        }

        Player player = players.get(playerID);
        Grid grid = player.getGrid();

        if (!grid.canBeActivated(card)){
            return;
        }

        Optional<Card> cardOpt = grid.getCard(card);
        if (cardOpt.isEmpty()){
            return;
        }
        Card cardToActivate = cardOpt.get();
        boolean success;

        // NOTE: Assistance functionality is not fully implemented according to assignment requirements
        // The following block shows how Assistance WOULD work if implemented completely
        if (cardToActivate.hasAssistance() && otherPlayerId.isPresent() && otherCard.isPresent()) {
            int assistingPlayer = otherPlayerId.get();
            // Get the assisting card from the OTHER player's grid
            Optional<Card> assistingCardOpt = players.get(assistingPlayer).getGrid().getCard(otherCard.get());
            if (assistingCardOpt.isEmpty()) {
                return;
            }
            Card assistingCard = assistingCardOpt.get();

            ProcessActionAssistance processActionAssistance = new ProcessActionAssistance();
            success = processActionAssistance.activateCard(cardToActivate, grid, assistingPlayer, assistingCard, inputs, outputs, pollution);

            if (success) {
                // State transition for Assistance - the player who owns the copied card selects a reward
                currentState = GameState.SelectReward;
            }
        }else {
            ProcessAction processAction = new ProcessAction();
             success = processAction.activateCard(cardToActivate, grid, inputs, outputs, pollution);
        }

        if (success){
            grid.setActivated(card);
            // Check if there are any more cards that can be activated in the grid
            if (hasMoreCardsToActivate(grid)) {
                // Stay in ActivateCard state for more activations
                currentState = GameState.ActivateCard;
            } else {
                // No more cards to activate - transition to card drawing for next player
                currentState = GameState.TakeCardNoCardDiscarded;
            }
        }
    }

    /**
     * Checks if there are any cards in the grid that can still be activated this turn
     * This allows players to activate multiple cards in one turn if they wish
     */
    private boolean hasMoreCardsToActivate(Grid grid) {
        // Check all possible grid positions (-2 to +2 in both X and Y)
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                GridPosition pos = new GridPosition(x, y);
                if (grid.canBeActivated(pos)) {
                    // Found at least one card that can still be activated
                    return true;
                }
            }
        }
        // No more cards can be activated this turn
        return false;
    }

    @Override
    public void selectReward(int playerID, Resource resource) {
        if (currentState != GameState.SelectReward){
            return;
        }


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
