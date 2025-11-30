package main.java.com.terrafutura.game;

import main.java.com.terrafutura.api.TerraFuturaObserverInterface;

public class ConsoleObserver implements TerraFuturaObserverInterface {
    private final int playerId;

    public ConsoleObserver(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public void notify(String gameState) {
        System.out.println("Player " + playerId + " received update: " + gameState);
    }
}