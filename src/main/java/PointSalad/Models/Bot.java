package PointSalad.Models;

import PointSalad.Utilities.ScoreUtil;
import PointSalad.Utilities.GameUtil;
import PointSalad.Utilities.HandUtil;

import java.util.ArrayList;

public class Bot extends AbstractPlayer {

    public Bot(int botID) {
        this.playerID = botID;
        this.online = false; // Bots are not online
    }

    @Override
    public void sendMessage(Object message) {
        // Bots don't need to send messages
        System.out.println(message);
    }

    @Override
    public void move(ArrayList<Pile> piles, ArrayList<AbstractPlayer> players) {
        boolean emptyPiles = false;

        // Bot will randomly decide between taking a point card or veggie cards
        int choice = (int) (Math.random() * 2);

        if (choice == 0) {
            // Bot decides to take a point card with the highest score
            int highestPointCardIndex = -1;
            int highestPointCardScore = 0;

            // Look for the highest point card
            for (int i = 0; i < piles.size(); i++) {
                if (piles.get(i).getPointCard(piles) != null) {  // Pass piles as parameter
                    // Calculate the score for taking this point card
                    ArrayList<Card> tempHand = new ArrayList<>(this.hand); // Clone the bot's current hand
                    tempHand.add(piles.get(i).getPointCard(piles)); // Add the point card from the pile to the temp hand
                    int score = ScoreUtil.calculateScore(tempHand, this.getPlayerID(), players);

                    // If this score is higher than the current highest score, choose this pile
                    if (score > highestPointCardScore) {
                        highestPointCardScore = score;
                        highestPointCardIndex = i;
                    }
                }
            }

            // If the bot found a valid point card to pick, buy it
            if (highestPointCardIndex != -1 && piles.get(highestPointCardIndex).getPointCard(piles) != null) { // Pass piles
                this.hand.add(piles.get(highestPointCardIndex).buyPointCard(piles)); // Buy the point card
            } else {
                // If no point cards are available, switch to picking veggie cards
                choice = 1;
                emptyPiles = true; // Mark that there are no point cards available
            }

        } else if (choice == 1) {
            // Bot decides to take veggie cards instead

            int cardsPicked = 0;
            // Go through piles to pick available veggie cards
            for (Pile pile : piles) {
                // Try to take up to two veggie cards (if available)
                if (pile.veggieCards[0] != null && cardsPicked < 2) {
                    this.hand.add(pile.buyVeggieCard(0, piles)); // Buy the first veggie card from the pile
                    cardsPicked++;
                }
                if (pile.veggieCards[1] != null && cardsPicked < 2) {
                    this.hand.add(pile.buyVeggieCard(1, piles)); // Buy the second veggie card from the pile
                    cardsPicked++;
                }
            }

            // If no veggie cards were picked (all piles are empty), fallback to taking a point card
            if (cardsPicked == 0 && !emptyPiles) {
                // Fallback to taking a point card with the highest score if no veggies are left
                int highestPointCardIndex = -1;
                int highestPointCardScore = 0;

                // Find the highest point card, including criteria cards
                for (int i = 0; i < piles.size(); i++) {
                    Card pointCard = piles.get(i).getPointCard(piles); // Pass piles as parameter

                    if (pointCard instanceof VegetableCard) {
                        VegetableCard vegetableCard = (VegetableCard) pointCard;

                        if (vegetableCard.isCriteriaSideUp()) {
                            ArrayList<Card> tempHand = new ArrayList<>(this.hand); // Clone the bot's current hand
                            tempHand.add(pointCard); // Add the point card from the pile to the temp hand
                            int score = ScoreUtil.calculateScore(tempHand, this.getPlayerID(), players);

                            // If this score is higher than the current highest score, choose this pile
                            if (score > highestPointCardScore) {
                                highestPointCardScore = score;
                                highestPointCardIndex = i;
                            }
                        }
                    }
                }

                // If the bot found a valid point card, buy it
                if (highestPointCardIndex != -1 && piles.get(highestPointCardIndex).getPointCard(piles) != null) { // Pass piles
                    this.hand.add(piles.get(highestPointCardIndex).buyPointCard(piles)); // Buy the point card
                }
            }
        }

        // Display bot's hand after making the move
        GameUtil.sendToAllPlayers("Bot " + this.playerID + "'s hand is now: \n" + HandUtil.displayHand(this.hand) + "\n", players);
    }

}
