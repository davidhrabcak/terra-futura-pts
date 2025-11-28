package main.java.com.terrafutura.game;

import main.java.com.terrafutura.board.ActivationPattern;
import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.piles.ActivationPatterns;
import main.java.com.terrafutura.piles.CardSource;
import main.java.com.terrafutura.piles.ScoringMethods;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public GameState state;
    public final List<Player> players;
    private int onTurn, startingPlayer, turnNumber;


    public Game(int playerNumber, int startingPlayerIndex) {
        this.players = new ArrayList<>();
        for (int i = 1; i <= playerNumber; i++) {
            Player p = setupPlayer(i);
            players.add(p);

        }
    }

    private Player setupPlayer(int id) {
        Grid g = new Grid();
        ActivationPatterns a = new ActivationPatterns(g);
        ScoringMethods s = new ScoringMethods();
        return new Player(id, s.getRandomScoringMethod(), s.getRandomScoringMethod(), a.getRandomActivationPattern(), a.getRandomActivationPattern(), g);
    }

    public void nextState() {

    }
}