package main.java.com.terrafutura.cards;
import main.java.com.terrafutura.piles.*;
import main.java.com.terrafutura.board.*;

import java.util.Optional;

public class MoveCard {

    /**
     * Moves a card from a Pile to a Grid in Terra Futura.
     *
     * @param pile The pile to take the card from
     * @param gridCoordinate The coordinate on the grid to place the card
     * @param grid The grid where the card will be placed
     * @return true if the card was successfully moved, false otherwise
     */
    public boolean moveCard(Pile pile, GridPosition gridCoordinate, Grid grid) {
        Optional<Card> optionalCard = pile.getCard(0);
        if (optionalCard.isEmpty()) {
            return false;
        }

        Card card = optionalCard.get();

        if (!grid.canPutCard(gridCoordinate)) {
            return false;
        }
        pile.takeCard(0); // predpokladám, že takeCard odstrani kartu z pile

        grid.putCard(gridCoordinate, card);

        return true;
    }
}
