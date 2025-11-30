package main.java.com.terrafutura.game;

import main.java.com.terrafutura.api.TerraFuturaInterface;
import main.java.com.terrafutura.api.TerraFuturaObserverInterface;
import main.java.com.terrafutura.board.ActivationPattern;
import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.*;
import main.java.com.terrafutura.piles.CardSource;
import main.java.com.terrafutura.piles.Deck;
import main.java.com.terrafutura.piles.Pile;
import main.java.com.terrafutura.resources.Resource;
import main.java.com.terrafutura.scoring.Points;
import main.java.com.terrafutura.scoring.ScoringMethod;


import java.util.*;


/**
 * Main game controller implementing the Terra Futura board game logic.
 * Manages game state transitions, player turns, card actions, and scoring.
 * Implements the state machine as specified in the design documentation.
 *
 * @see TerraFuturaInterface
 */
public class Game implements TerraFuturaInterface {
    private GameState currentState;
    private final int[] playerIDs;
    private final GameObserver observer;
    private final Map<Integer,Player> players;
    private final TurnManager turnManager;
    private final Random rnd = new Random();
    private final Pile pile1;
    private final Pile pile2;
    private SelectReward selectReward;
    private ProcessActionAssistance processActionAssistance;

    /**
     * Constructs a new Terra Futura game with specified number of players and starting player.
     * Initializes all game components including players, grids, card piles, and observers.
     * Activation patterns are created at the end of the game based on actual card positions.
     *
     * @param playerCount the number of players (must be between 2 and 4)
     * @param startingPlayerID the ID of the player who starts the game
     * @throws IllegalArgumentException if playerCount is invalid or startingPlayerID is not valid for the player count
     */
    public Game(int playerCount, int startingPlayerID){
        if (playerCount < 2 || playerCount > 4){
            throw new IllegalArgumentException("Player count must be between 2 and 4");
        }

        if (startingPlayerID < 1 || startingPlayerID > playerCount){
            throw new IllegalArgumentException("Starting player must be between 1 and player count");
        }

        // Initialize player IDs and observers
        Map<Integer, TerraFuturaObserverInterface> observers = new HashMap<>();
        this.playerIDs = new int[playerCount];
        for (int i = 0; i < playerCount; i++){
            playerIDs[i] = i + 1;
            observers.put(i+1, new ConsoleObserver(i+1));
        }
        this.observer = new GameObserver(observers);
        this.currentState = GameState.TakeCardNoCardDiscarded;
        notifyObservers();
        this.players = new HashMap<>();
        this.turnManager = new TurnManager(playerIDs, startingPlayerID);
        CardFactory cardFactory = new CardFactory();
        this.pile1 = new Pile(cardFactory.createLevelIDeck());
        this.pile2 = new Pile(cardFactory.createLevelIIDeck());

        // Initialize each player with starting card, scoring methods and activation patterns
        for(int playerID : playerIDs){
            //The third argument is null because we are implementing a game without assistance.
            //In complete implementation it would be effect with hasAssistance == True;
            Card startingCard = cardFactory.createStartingCard();
            Grid g = new Grid(startingCard);
            Player p = new Player(playerID, randomScoringMethod(), randomScoringMethod(), null, null, g);
            players.put(playerID, p);
        }

    }

    /**
     * Gets all card positions in the grid by scanning all possible positions.
     * @param grid the grid to scan for card positions
     * @return a collection of SimpleEntry objects representing the (x,y) coordinates
     *         of all positions that contain cards
     */
    private Collection<AbstractMap.SimpleEntry<Integer, Integer>> getAllCardPositions(Grid grid) {
        Collection<AbstractMap.SimpleEntry<Integer, Integer>> positions = new ArrayList<>();
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                GridPosition pos = new GridPosition(x, y);
                if (grid.getCard(pos).isPresent()) {
                    positions.add(new AbstractMap.SimpleEntry<>(x, y));
                }
            }
        }
        return positions;
    }

    /**
     * Creates two random activation patterns by selecting 4 random card positions for each pattern.
     * This method is called at the end of the game when the grid is complete with exactly 9 cards.
     * It generates two distinct activation patterns by randomly shuffling the card positions
     * and selecting the first 4 positions for each pattern.
     * @param grid the player's grid containing all placed cards
     * @return a list containing two ActivationPattern objects with randomly selected positions
     * @throws IllegalStateException if the grid does not contain exactly 9 cards,
     * indicating an invalid game state at the end phase
     */
    private List<ActivationPattern> createRandomActivationPatterns(Grid grid) {
        Collection<AbstractMap.SimpleEntry<Integer, Integer>> allPositions = getAllCardPositions(grid);

        if (allPositions.size() != 9) {
            throw new IllegalStateException("Grid should have exactly 9 cards at the end of the game");
        }

        // Convert to List for shuffling
        List<AbstractMap.SimpleEntry<Integer, Integer>> positionList = new ArrayList<>(allPositions);

        // Create first pattern - shuffle and take first 4 positions
        Collections.shuffle(positionList);
        Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern1 = new ArrayList<>(positionList.subList(0, 4));

        // Create second pattern - shuffle again and take first 4 positions
        Collections.shuffle(positionList);
        Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern2 = new ArrayList<>(positionList.subList(0, 4));

        return Arrays.asList(
                new ActivationPattern(grid, pattern1),
                new ActivationPattern(grid, pattern2)
        );
    }

    /**
     * Generates a random scoring method with random resources and point value.
     * @return a random ScoringMethod
     */
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

    /**
     * Notifies all observers about the current game state.
     * Each player receives information about the current state and whether it's their turn.
     */
    private void notifyObservers() {
        Map<Integer, String> states = new HashMap<>();
        for (int playerId : playerIDs) {
            String playerState = "State: " + currentState + ", Player: " + playerId + ", On turn: " + (turnManager.isPlayerTurn(playerId));
            states.put(playerId, playerState);
        }
        observer.notifyAll(states);
    }

    /**
     * Allows the current player to take a card from the specified source and place it on their grid.
     * Transitions game state to ActivateCard if successful.
     * @param playerID the ID of the player taking the card
     * @param cardSource the source of the card (deck and index)
     * @param destination the grid position where to place the card
     * @return true if the card was successfully taken and placed, false otherwise
     */
    @Override
    public boolean takeCard(int playerID, CardSource cardSource, GridPosition destination) {
        if(!turnManager.isPlayerTurn(playerID)){
            return false;
        }

        if (currentState != GameState.TakeCardNoCardDiscarded && currentState != GameState.TakeCardCardDiscarded) {
            return false;
        }

        Player player = players.get(playerID);
        Grid grid = player.getGrid();

        //Check if the destination is valid
        if (!grid.canPutCard(destination)) {
            return false;
        }

        //Which pile to take a card from?
        Pile targetPile = (cardSource.deck == Deck.I) ? pile1 : pile2;

        Optional<Card> cardOpt;
        cardOpt = targetPile.getCard(cardSource.index);

        if (cardOpt.isEmpty()) {
            return false;
        }

        Card card = cardOpt.get();
        grid.putCard(destination, card);

        // Remove the card from the pile and replace it with a new one if possible
        targetPile.takeCard(cardSource.index);

        currentState = GameState.ActivateCard;
        notifyObservers();

        return true;
    }

    /**
     * Allows the current player to discard the last card from the specified deck.
     * Transition game state to TakeCardCardDiscarded if successful.
     * @param playerID the ID of the player discarding the card
     * @param deck the deck from which to discard the last card
     * @return true if the card was successfully discarded, false otherwise
     */
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
        notifyObservers();
        return true;
    }

    /**
     * Activates a card on the current player's grid, executing its effects.
     * Handles both regular card activation and Assistance card effects.
     * For Assistance cards, transitions to SelectReward state for the assisting player.
     * (NOTE: Assistance functionality is not fully implemented according to assignment requirements)
     * @param playerID the ID of the player activating the card
     * @param card the grid position of the card to activate
     * @param inputs list of resource-position pairs used as input for the card effect
     * @param outputs list of resource-position pairs produced as output from the card effect
     * @param pollution list of grid positions where pollution should be placed
     * @param otherPlayerId optional ID of another player involved in Assistance effect
     * @param otherCard optional grid position of another player's card for Assistance effect
     */
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

        // The following block shows how Assistance would work if implemented completely
        if (cardToActivate.hasAssistance() && otherPlayerId.isPresent() && otherCard.isPresent()) {
            int assistingPlayer = otherPlayerId.get();
            // Get the assisting card from the OTHER player's grid
            Optional<Card> assistingCardOpt = players.get(assistingPlayer).getGrid().getCard(otherCard.get());
            if (assistingCardOpt.isEmpty()) {
                return;
            }
            Card assistingCard = assistingCardOpt.get();

            this.processActionAssistance = new ProcessActionAssistance();
            if(processActionAssistance.activateCard(cardToActivate, grid, assistingPlayer, assistingCard, inputs, outputs, pollution)) {

                // State transition for Assistance - the player who owns the copied card selects a reward
                selectReward = processActionAssistance.getSelectReward();
                currentState = GameState.SelectReward;
                notifyObservers();
            }
        }
        else {
            ProcessAction processAction = new ProcessAction();
            if (processAction.activateCard(cardToActivate, grid, inputs, outputs, pollution)) {
                grid.setActivated(card);
            }
        }
    }

    /**
     * Allows a player to select a reward after an Assistance card effect.
     * Transition game state back to ActivateCard after reward selection.
     * @param playerID the ID of the player selecting the reward
     * @param resource the resource selected as a reward
     */
    @Override
    public void selectReward(int playerID, Resource resource) {
        if (currentState != GameState.SelectReward || selectReward == null){
            return;
        }
        if(!selectReward.canSelectReward(resource)){
            return;
        }
        selectReward.selectReward(resource);
        processActionAssistance = null;
        selectReward = null;
        currentState = GameState.ActivateCard;
        notifyObservers();
    }

    /**
     * Ends the current player's turn and advances to the next player.
     * Checks if the game should end (after 9 turns) and transitions to appropriate state.
     * @param playerID the ID of the player ending their turn
     * @return true if the turn was successfully ended, false otherwise
     */
    @Override
    public boolean turnFinished(int playerID) {
        if(!turnManager.isPlayerTurn(playerID)){
            return false;
        }
        if (currentState != GameState.ActivateCard) {
            return false;
        }
        Player player = players.get(playerID);
        player.getGrid().endTurn();

        turnManager.nextTurn();

        if (turnManager.getTurnNumber() == 9){
            for (Player p : players.values()) {
                List<ActivationPattern> patterns = createRandomActivationPatterns(p.getGrid());
                p.setActivationPattern1(patterns.get(0));
                p.setActivationPattern2(patterns.get(1));
            }
            currentState = GameState.SelectActivationPattern;
            notifyObservers();
        }else {
            currentState = GameState.TakeCardNoCardDiscarded;
            notifyObservers();
        }

        return true;
    }

    /**
     * Allows a player to select an activation pattern for the final activation phase.
     * When all players have selected a pattern, transitions to ActivateCard state.
     * @param playerID the ID of the player selecting the pattern
     * @param card which pattern to select (1 or 2)
     * @return true if the pattern was successfully selected, false otherwise
     */
    @Override
    public boolean selectActivationPattern(int playerID, int card) {
        if(currentState != GameState.SelectActivationPattern){
            return false;
        }

        Player player = players.get(playerID);
        ActivationPattern selected;
        switch (card){
            case 1: selected = player.getActivationPattern1(); break;
            case 2: selected = player.getActivationPattern2(); break;
            default: return false;
        }

        selected.select();

        boolean allSelected = true;  // Check if all players have selected at least one activation pattern
        for (Player p : players.values()){
            if (!p.getActivationPattern1().isSelected() && !p.getActivationPattern2().isSelected()){
                allSelected = false;
                break;
            }
        }
        if (allSelected){
            currentState = GameState.ActivateCard;
            notifyObservers();
        }

        return true;
    }

    /**
     * Allows a player to select a scoring method for final scoring.
     * Immediately transitions game to Finish state after selection.
     * Note: Unlike activation patterns, scoring method selection is individual and doesn't wait for other players.
     * @param playerID the ID of the player selecting the scoring method
     * @param card which scoring method to select (1 or 2)
     * @return true if the scoring method was successfully selected, false otherwise
     */
    @Override
    public boolean selectScoring(int playerID, int card) {
        if (currentState != GameState.SelectScoringMethod) {
            return false;
        }

        Player player = players.get(playerID);
        ScoringMethod selected;
        switch (card) {
            case 1:
                selected = player.getScoreMethod1();
                break;
            case 2:
                selected = player.getScoreMethod2();
                break;
            default:
                return false;
        }

        selected.selectThisMethodAndCalculate();

        currentState = GameState.Finish;
        notifyObservers();

        return true;
    }
}
