package main.java.com.terrafutura.game;

import main.java.com.terrafutura.api.TerraFuturaObserverInterface;

import java.util.Map;

public class GameObserver {
    private final Map<Integer, TerraFuturaObserverInterface> observers;

    public GameObserver(Map<Integer, TerraFuturaObserverInterface> observers) {
        this.observers = observers;
    }

    public void notifyAll(Map<Integer, String> newState) {
        for (Map.Entry<Integer, TerraFuturaObserverInterface> entry : observers.entrySet()) {
            Integer playerId = entry.getKey();
            TerraFuturaObserverInterface observer = entry.getValue();
            String state = newState.get(playerId); // každý hráč dostane svoj stav
            if (state != null) {
                observer.notify(state);
            }
        }
    }
}
