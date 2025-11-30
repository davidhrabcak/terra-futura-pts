package main.java.com.terrafutura.game;

public class TurnManger {
    private int[] playersIds;
    private int currentPlayerIndex = -1;
    private int turnNumber;
    private int startingPlayer;

    public TurnManger(int[] playersIds, int startingPlayer) {
        this.playersIds = playersIds;
        this.startingPlayer = startingPlayer;
        for (int i = 0; i < playersIds.length; i++) {
            if (playersIds[i] == startingPlayer){
                this.currentPlayerIndex = i;
            }
        }
        if (this.currentPlayerIndex == -1) {
            throw new IllegalArgumentException("Player not found: " + playersIds);
        }
        this.turnNumber = 1;
    }

    public int getPlayerOnTurn() {
        return playersIds[currentPlayerIndex];
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

    public boolean isGameFinished() {
        return turnNumber > 9;
    }
}
