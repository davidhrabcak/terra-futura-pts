package main.java.com.terrafutura.game;

import main.java.com.terrafutura.board.ActivationPattern;
import main.java.com.terrafutura.board.Grid;
import main.java.com.terrafutura.scoring.ScoringMethod;

public class Player {
    public int id;
    public ScoringMethod s1, s2;
    public ActivationPattern a1, a2;
    public Grid g;

    public Player(int id, ScoringMethod s1, ScoringMethod s2,
                  ActivationPattern a1, ActivationPattern a2, Grid g) {
        this.id = id;
        this.s1 = s1;
        this.s2 = s2;
        this.a1 = a1;
        this.a2 = a2;
        this.g = g;
    }

    public ScoringMethod getScoreMethod1(){return s1;}
    public ScoringMethod getScoreMethod2(){return s2;}
    public ActivationPattern getActivationPattern1(){return a1;}
    public void setActivationPattern1(ActivationPattern a1){this.a1 = a1;}
    public ActivationPattern getActivationPattern2(){return a2;}
    public void setActivationPattern2(ActivationPattern a2){this.a2 = a2;}
    public Grid getGrid(){return g;}
}