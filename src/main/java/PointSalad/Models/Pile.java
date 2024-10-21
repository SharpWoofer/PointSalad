package PointSalad.Models;

import java.util.ArrayList;
import java.util.List;

public class Pile {
    private ArrayList<Card> cards = new ArrayList<>();
    public Card[] veggieCards = new Card[2];  // Changed to use Card interface

    public Pile(ArrayList<Card> cards) {
        this.cards = cards;
        if (cards.size() > 1) {
            this.veggieCards[0] = cards.remove(0);
            this.veggieCards[1] = cards.remove(0);
            if (this.veggieCards[0] instanceof VegetableCard) {
                ((VegetableCard) this.veggieCards[0]).setCriteriaSideUp(false);
            }
            if (this.veggieCards[1] instanceof VegetableCard) {
                ((VegetableCard) this.veggieCards[1]).setCriteriaSideUp(false);
            }
        }
    }

    public Card getPointCard(List<Pile> piles) {
        if (cards.isEmpty()) {
            // Fallback logic for when the pile is empty
            int biggestPileIndex = 0;
            int biggestSize = 0;
            for (int i = 0; i < piles.size(); i++) {
                if (i != piles.indexOf(this) && piles.get(i).cards.size() > biggestSize) {
                    biggestSize = piles.get(i).cards.size();
                    biggestPileIndex = i;
                }
            }
            if (biggestSize > 1) {
                cards.add(piles.get(biggestPileIndex).cards.remove(piles.get(biggestPileIndex).cards.size() - 1));
            } else {
                return null;
            }
        }
        return cards.get(0);
    }

    public Card buyPointCard(List<Pile> piles) {
        if (cards.isEmpty()) {
            int biggestPileIndex = 0;
            int biggestSize = 0;
            for (int i = 0; i < piles.size(); i++) {
                if (i != piles.indexOf(this) && piles.get(i).cards.size() > biggestSize) {
                    biggestSize = piles.get(i).cards.size();
                    biggestPileIndex = i;
                }
            }
            if (biggestSize > 1) {
                cards.add(piles.get(biggestPileIndex).cards.remove(piles.get(biggestPileIndex).cards.size() - 1));
            } else {
                return null;
            }
        }
        return cards.remove(0);
    }

    public Card getVeggieCard(int index) {
        return veggieCards[index];
    }

    public Card buyVeggieCard(int index, List<Pile> piles) {
        Card aCard = veggieCards[index];
        if (cards.size() <= 1) {
            int biggestPileIndex = 0;
            int biggestSize = 0;
            for (int i = 0; i < piles.size(); i++) {
                if (i != piles.indexOf(this) && piles.get(i).cards.size() > biggestSize) {
                    biggestSize = piles.get(i).cards.size();
                    biggestPileIndex = i;
                }
            }
            if (biggestSize > 1) {
                cards.add(piles.get(biggestPileIndex).cards.remove(piles.get(biggestPileIndex).cards.size() - 1));
                veggieCards[index] = cards.remove(0);
                if (veggieCards[index] instanceof VegetableCard) {
                    ((VegetableCard) veggieCards[index]).setCriteriaSideUp(false);
                }
            } else {
                veggieCards[index] = null;
            }
        } else {
            veggieCards[index] = cards.remove(0);
            if (veggieCards[index] instanceof VegetableCard) {
                ((VegetableCard) veggieCards[index]).setCriteriaSideUp(false);
            }
        }

        return aCard;
    }

    public boolean isEmpty() {
        return cards.isEmpty() && veggieCards[0] == null && veggieCards[1] == null;
    }
}
