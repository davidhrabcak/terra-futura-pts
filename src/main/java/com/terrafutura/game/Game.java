package main.java.com.terrafutura.game;

import main.java.com.terrafutura.api.TerraFuturaInterface;
import main.java.com.terrafutura.board.ActivationPattern;
import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.board.GridPosition;
import main.java.com.terrafutura.cards.Card;
import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.cards.Pair;
import main.java.com.terrafutura.cards.effects.ArbitraryBasic;
import main.java.com.terrafutura.cards.effects.EffectOr;
import main.java.com.terrafutura.cards.effects.TransformationFixed;
import main.java.com.terrafutura.piles.CardSource;
import main.java.com.terrafutura.piles.Deck;
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

        for(int playerID : playerIDs){
            //The third argument is null because we are implementing a game without assistance.
            //In complete implementation it would be effected with hasAssistance == True;
            Card startingCard = new Card(0,startingCardEffects(),null);
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

    private Effect startingCardEffects(){
        return new EffectOr(
                new ArbitraryBasic(0, Arrays.asList(Resource.Green, Resource.Red, Resource.Yellow)),
                new ArbitraryBasic()
        );
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
