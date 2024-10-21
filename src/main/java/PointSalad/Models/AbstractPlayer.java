package PointSalad.Models;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

public abstract class AbstractPlayer {
    protected int playerID;
    protected ArrayList<Card> hand = new ArrayList<>(); // Strictly using ArrayList
    protected int score = 0;
    protected boolean online;
    protected ObjectOutputStream outToClient;

    public int getPlayerID() {
        return this.playerID;
    }

    public ArrayList<Card> getHand() {
        return new ArrayList<>(this.hand); // Returns a copy to prevent modification
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    // Abstract method to send messages (to be overridden in Player and Bot)
    public abstract void sendMessage(Object message);

    // Abstract method move (to be overridden in Player and Bot)
    public abstract void move(ArrayList<Pile> piles, ArrayList<AbstractPlayer> players);

    public void turnPointCardToVegetable(int cardIndex) {
        if (cardIndex >= 0 && cardIndex < hand.size()) {
            Card card = hand.get(cardIndex);
            if (card instanceof VegetableCard) {
                VegetableCard vegetableCard = (VegetableCard) card;
                if (vegetableCard.isCriteriaSideUp()) {
                    vegetableCard.setCriteriaSideUp(false); // Turn the card to its vegetable side
                }
            }
        }
    }

}
