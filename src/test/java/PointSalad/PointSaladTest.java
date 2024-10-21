import PointSalad.Controllers.GameController;
import PointSalad.Models.AbstractPlayer;
import PointSalad.Models.Deck;
import PointSalad.Services.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointSaladTest {
    private GameController gameController;
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        gameService = new GameService();
        gameController = new GameController(gameService); // Use the overloaded constructor
    }

    @Test
    public void testGameInitialization() {
        String[] args = {"2", "0"}; // 2 players, 0 bots
        gameController.runGame(args);

        // Check if players were added
        assertEquals(2, gameService.getPlayers().size(), "Number of players should be 2.");

        // Check if the deck is initialized
        Deck deck = gameService.getDeck();
        assertEquals(72, deck.getTotalCards(), "Deck size should be adjusted to 72 for 2 players.");
    }

    // Additional tests...
}
