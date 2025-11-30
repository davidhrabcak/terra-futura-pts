package main.java.com.terrafutura.game;

import java.util.Arrays;

public class TurnManager {
    private final int[] playersIds;
    private int currentPlayerIndex = -1;
    private int turnNumber;

    public TurnManager(int[] playersIds, int startingPlayer) {
        this.playersIds = playersIds;
        for (int i = 0; i < playersIds.length; i++) {
            if (playersIds[i] == startingPlayer){
                this.currentPlayerIndex = i;
            }
        }
        if (this.currentPlayerIndex == -1) {
            throw new IllegalArgumentException("Player not found");
        }
        this.turnNumber = 1;
    }


    public boolean isPlayerTurn(int playerId) {
        return playerId == playersIds[currentPlayerIndex];
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playersIds.length;
        if (currentPlayerIndex == 0) {
            turnNumber++;
        }
    }
    public int getTurnNumber() {
        return turnNumber;
    }
}
