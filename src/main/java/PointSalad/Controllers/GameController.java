package PointSalad.Controllers;

import PointSalad.Services.GameService;
import PointSalad.Models.AbstractPlayer;

public class GameController {
    private GameService gameService;

    // Default constructor for running the game
    public GameController() {
        this.gameService = new GameService();
    }

    // Overloaded constructor for testing
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    public void runGame(String[] args) {
        // Initialize players and piles
        gameService.setupGame(args);

        // Run the game loop
        while (!gameService.gameOver()) {
            handleTurn();
        }

        // Calculate and display scores
        gameService.determineWinner();
    }

    private void handleTurn() {
        // Get the current player
        AbstractPlayer currentPlayer = gameService.getCurrentPlayer();

        // Have the player take their turn using piles from the deck
        currentPlayer.move(gameService.getDeck().getPiles(), gameService.getPlayers());

        // Advance to the next player
        gameService.advanceToNextPlayer();
    }
}
