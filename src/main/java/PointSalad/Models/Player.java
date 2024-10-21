package PointSalad.Models;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import PointSalad.Utilities.GameUtil;
import PointSalad.Utilities.HandUtil;
import PointSalad.Utilities.MarketUtil;

public class Player extends AbstractPlayer {
    private Socket connection;
    private ObjectInputStream inFromClient;
    private Scanner in; // Used for reading from console if offline

    public Player(int playerID, Socket connection, ObjectInputStream inFromClient, ObjectOutputStream outToClient) {
        this.playerID = playerID;
        this.connection = connection;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
        this.online = (connection != null);
        if (!online) {
            in = new Scanner(System.in); // Initialize Scanner if player is offline
        }
    }

    @Override
    public void sendMessage(Object message) {
        if (online) {
            try {
                outToClient.writeObject(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(message);
        }
    }

    @Override
    public void move(ArrayList<Pile> piles, ArrayList<AbstractPlayer> players) {
        // Send initial message to the player
        this.sendMessage("\n\n****************************************************************\nIt's your turn! Your hand is:\n");
        this.sendMessage(HandUtil.displayHand(this.hand)); // Display the player's hand

        // Display the piles available in the game
        this.sendMessage("\nThe piles are: ");
        this.sendMessage(MarketUtil.printMarket(piles)); // Show the market/piles available for moves

        boolean validChoice = false;
        while (!validChoice) {
            this.sendMessage("\n\nTake either one point card (Syntax example: 2) or up to two vegetable cards (Syntax example: CF).\n");
            String pileChoice = this.readMessage(); // Read the player's choice

            if (pileChoice.matches("\\d")) { // If the player is choosing a point card
                int pileIndex = Integer.parseInt(pileChoice);
                if (piles.get(pileIndex).getPointCard(piles) == null) { // Check if the chosen pile is empty
                    this.sendMessage("\nThis pile is empty. Please choose another pile.\n");
                    continue;
                } else {
                    // Buy the point card and add it to the player's hand
                    this.hand.add(piles.get(pileIndex).buyPointCard(piles));
                    this.sendMessage("\nYou took a card from pile " + (pileIndex) + " and added it to your hand.\n");
                    validChoice = true; // Mark the choice as valid
                }
            } else { // If the player is choosing vegetable cards
                int takenVeggies = 0;
                for (int charIndex = 0; charIndex < pileChoice.length(); charIndex++) {
                    char choiceChar = Character.toUpperCase(pileChoice.charAt(charIndex));
                    if (choiceChar < 'A' || choiceChar > 'F') { // Check if the input is valid (A to F)
                        this.sendMessage("\nInvalid choice. Please choose up to two veggie cards from the market.\n");
                        validChoice = false; // Mark as invalid
                        break;
                    }

                    // Determine the pile and index based on the choice
                    int choice = choiceChar - 'A';
                    int pileIndex = (choice == 0 || choice == 3) ? 0 :
                            (choice == 1 || choice == 4) ? 1 :
                                    (choice == 2 || choice == 5) ? 2 : -1;
                    int veggieIndex = (choice == 0 || choice == 1 || choice == 2) ? 0 :
                            (choice == 3 || choice == 4 || choice == 5) ? 1 : -1;

                    // Check if the chosen vegetable is available in the pile
                    if (piles.get(pileIndex).veggieCards[veggieIndex] == null) {
                        this.sendMessage("\nThis veggie is empty. Please choose another pile.\n");
                        validChoice = false;
                        break;
                    } else {
                        // Add the vegetable card to the player's hand
                        this.hand.add(piles.get(pileIndex).buyVeggieCard(veggieIndex, piles));
                        takenVeggies++;
                        if (takenVeggies == 2) {
                            validChoice = true; // Stop after two veggies are taken
                            break;
                        }
                    }
                }
            }
        }

        // Check if the player has any criteria cards in their hand
        boolean criteriaCardInHand = false;
        for (Card card : this.hand) {
            if (card instanceof VegetableCard && ((VegetableCard) card).isCriteriaSideUp()) {
                criteriaCardInHand = true;
                break;
            }
        }

        // If the player has criteria cards, give them the option to turn one into a veggie card
        if (criteriaCardInHand) {
            this.sendMessage("\n" + HandUtil.displayHand(this.hand) + "\nWould you like to turn a criteria card into a veggie card? (Syntax example: n or 2)");
            String choice = this.readMessage();
            if (choice.matches("\\d")) {
                int cardIndex = Integer.parseInt(choice);
                Card chosenCard = this.hand.get(cardIndex);
                if (chosenCard instanceof VegetableCard) {
                    ((VegetableCard) chosenCard).setCriteriaSideUp(false); // Turn the criteria card to a veggie card
                }
            }
        }

        // End the player's turn
        this.sendMessage("\nYour turn is completed\n****************************************************************\n\n");
        GameUtil.sendToAllPlayers("Player " + this.playerID + "'s hand is now: \n" + HandUtil.displayHand(this.hand) + "\n", players);
    }

    // Method to read message from the client if online, or from console if offline
    public String readMessage() {
        String word = "";
        if (online) {
            try {
                word = (String) inFromClient.readObject();
            } catch (Exception e) {
                e.printStackTrace(); // Optionally handle the exception
            }
        } else {
            try {
                word = in.nextLine(); // Read from console if offline
            } catch (Exception e) {
                e.printStackTrace(); // Optionally handle the exception
            }
        }
        return word;
    }
}