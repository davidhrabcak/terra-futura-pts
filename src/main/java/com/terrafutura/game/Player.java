package main.java.com.terrafutura.game;

import main.java.com.terrafutura.board.ActivationPattern;
import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.scoring.ScoringMethod;

public class Player {
    public final int id;
    public final ScoringMethod s1, s2;
    public final ActivationPattern a1, a2;
    public final Grid g;

    public Player(int id, ScoringMethod s1, ScoringMethod s2,
                  ActivationPattern a1, ActivationPattern a2, Grid g) {
        this.id = id;
        this.s1 = s1;
        this.s2 = s2;
        this.a1 = a1;
        this.a2 = a2;
        this.g = g;
    }
}