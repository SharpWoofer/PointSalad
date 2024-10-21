package PointSalad.Services;

import PointSalad.Models.AbstractPlayer;
import PointSalad.Models.Deck;
import PointSalad.Models.Player;
import PointSalad.Models.Pile;
import PointSalad.Utilities.GameUtil;
import PointSalad.Utilities.HandUtil;
import PointSalad.Utilities.ScoreUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameService {
    private ArrayList<AbstractPlayer> players = new ArrayList<>();
    private Deck deck; // Store the Deck instance
    private int currentPlayerIndex = 0;

    // Setup the game
    public void setupGame(String[] args) {
        int numberPlayers = 0;
        int numberOfBots = 0;

        if (args.length == 0) {
            System.out.println("Please enter the number of players (1-6): ");
            Scanner in = new Scanner(System.in);
            numberPlayers = in.nextInt();
            System.out.println("Please enter the number of bots (0-5): ");
            numberOfBots = in.nextInt();
        } else {
            if (args[0].matches("\\d+")) {
                numberPlayers = Integer.parseInt(args[0]);
                numberOfBots = Integer.parseInt(args[1]);
            } else {
                try {
                    client(args[0]); // Connect to server if provided
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int totalPlayers = numberPlayers + numberOfBots;

        // Use Deck to set up piles based on the number of players and bots
        deck = new Deck();
        deck.setupDeckForPlayers(totalPlayers);

        try {
            server(numberPlayers, numberOfBots); // Setup server for multiplayer
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set random starting player
        currentPlayerIndex = (int) (Math.random() * players.size());
    }

    // Check if the game is over
    public boolean gameOver() {
        // Check if there are still available cards in any of the piles
        for (Pile pile : deck.getPiles()) {
            if (!pile.isEmpty()) {
                return false; // If at least one pile has cards, the game continues
            }
        }
        return true; // No piles have cards left, game is over
    }

    public ArrayList<AbstractPlayer> getPlayers() {
        return players;
    }

    // Update the return type to AbstractPlayer
    public AbstractPlayer getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    // Advance to the next player
    public void advanceToNextPlayer() {
        if (currentPlayerIndex == players.size() - 1) {
            currentPlayerIndex = 0; // Rotate back to the first player
        } else {
            currentPlayerIndex++; // Move to the next player
        }
    }

    // Calculate scores for all players
    public void determineWinner() {
        // Notify all players that scores are being calculated
        GameUtil.sendToAllPlayers("\n-------------------------------------- CALCULATING SCORES --------------------------------------\n", new ArrayList<>(players));

        // Calculate and display each player's hand and score
        for (AbstractPlayer player : players) {
            GameUtil.sendToAllPlayers("Player " + player.getPlayerID() + "'s hand is: \n" + HandUtil.displayHand(player.getHand()), new ArrayList<>(players));

            // Use the correct utility method for score calculation
            int calculatedScore = ScoreUtil.calculateScore(player.getHand(), player.getPlayerID(), new ArrayList<>(players));
            player.setScore(calculatedScore);

            GameUtil.sendToAllPlayers("\nPlayer " + player.getPlayerID() + "'s score is: " + player.getScore(), new ArrayList<>(players));
        }

        // Determine the winner based on the highest score
        int maxScore = 0;
        int winnerID = 0;

        for (AbstractPlayer player : players) {
            if (player.getScore() > maxScore) {
                maxScore = player.getScore();
                winnerID = player.getPlayerID();
            }
        }

        // Notify players about the winner
        for (AbstractPlayer player : players) {
            if (player.getPlayerID() == winnerID) {
                player.sendMessage("\nCongratulations! You are the winner with a score of " + maxScore);
            } else {
                player.sendMessage("\nThe winner is player " + winnerID + " with a score of " + maxScore);
            }
        }
    }

    // Client method to connect to the server
    public void client(String ipAddress) throws Exception {
        Socket aSocket = new Socket(ipAddress, 2048);
        ObjectOutputStream outToServer = new ObjectOutputStream(aSocket.getOutputStream());
        ObjectInputStream inFromServer = new ObjectInputStream(aSocket.getInputStream());
        String nextMessage = "";
        while (!nextMessage.contains("winner")) {
            nextMessage = (String) inFromServer.readObject();
            System.out.println(nextMessage);
            if (nextMessage.contains("Take") || nextMessage.contains("into")) {
                Scanner in = new Scanner(System.in);
                outToServer.writeObject(in.nextLine());
            }
        }
    }

    // Server method to setup game with players and bots
    public void server(int numberPlayers, int numberOfBots) throws Exception {
        // Add the current player (this instance) as an offline player
        players.add(new Player(0, null, null, null)); // Add this instance as a player

        // Add bots to the game
        for (int i = 0; i < numberOfBots; i++) {
            players.add(new Player(i + 1, null, null, null)); // Bots are also offline, so null is passed
        }

        // Open for connections if there are online players
        if (numberPlayers > 1) {
            ServerSocket aSocket = new ServerSocket(2048);
            for (int i = numberOfBots + 1; i < numberPlayers + numberOfBots; i++) {
                Socket connectionSocket = aSocket.accept();
                ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());
                ObjectOutputStream outToClient = new ObjectOutputStream(connectionSocket.getOutputStream());
                players.add(new Player(i, connectionSocket, inFromClient, outToClient)); // Online client player
                System.out.println("Connected to player " + i);
                outToClient.writeObject("You connected to the server as player " + i + "\n");
            }
        }
    }

    public Deck getDeck() {
        return this.deck;
    }

    public int getMarketSize() {
        int marketSize = 0;
        for (Pile pile : deck.getPiles()) {
            if (pile.getVeggieCard(0) != null) {
                marketSize++;
            }
            if (pile.getVeggieCard(1) != null) {
                marketSize++;
            }
        }
        return marketSize;
    }

}
