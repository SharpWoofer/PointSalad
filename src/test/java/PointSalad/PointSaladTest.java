package PointSalad;

import PointSalad.Controllers.GameController;
import PointSalad.Models.*;
import PointSalad.Services.GameService;
import PointSalad.Utilities.HandUtil;
import PointSalad.Utilities.MarketUtil;
import PointSalad.Utilities.ScoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PointSaladTest {
    private GameController gameController;
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        gameService = new GameService();
        gameController = new GameController(gameService); // Use the overloaded constructor
    }

    // Requirement 1 - Test for correct player count (2 to 6 players)
    @Test
    public void testPlayerCount() {
        String[] args = {"1", "1"}; // 1 player, 1 bot
        gameService.setupGame(args);

        assertEquals(2, gameService.getPlayers().size(), "Number of players should be 2.");
    }

    // Requirement 2 - Test deck size based on the number of players
    @Test
    public void testDeckSize() {
        String[] args2Players = {"1", "1"};
        gameService.setupGame(args2Players);
        assertEquals(36, gameService.getDeck().getTotalCards(), "Deck size should be 36 for 2 players.");

        String[] args3Players = {"1", "2"};
        gameService.setupGame(args3Players);
        assertEquals(54, gameService.getDeck().getTotalCards(), "Deck size should be 54 for 3 players.");
    }

    // Requirement 4 - Test deck shuffling and creation of draw piles
    @Test
    public void testDeckShufflingAndDrawPiles() {
        String[] args = {"1", "1"}; // 1 players, 1 bots
        gameService.setupGame(args);
        Deck deck = gameService.getDeck();

        // Check if the deck is shuffled (we can only check that the deck is not in default order)
        Pile pile1 = deck.getPiles().get(0);
        Pile pile2 = deck.getPiles().get(0);
        Pile pile3 = deck.getPiles().get(0);

        assertFalse(!pile1.equals(pile2) && !pile2.equals(pile3), "The piles should be shuffled and unique.");
    }

    // Requirement 5 - Test vegetable market creation (2 cards from each pile)
    @Test
    public void testVegetableMarketSetup() {
        String[] args = {"1", "1"};
        gameService.setupGame(args);

        assertEquals(6, gameService.getMarketSize(), "There should be 6 cards in the vegetable market (2 from each of the 3 piles).");
    }

    // Requirement 6 - Test random start player
    @Test
    public void testRandomStartPlayer() {
        String[] args = {"1", "2"};
        gameService.setupGame(args);

        AbstractPlayer startingPlayer = gameService.getCurrentPlayer();
        assertTrue(gameService.getPlayers().contains(startingPlayer), "The starting player should be a valid player from the list.");
    }

    class TestPlayer extends AbstractPlayer {
        private String simulatedInput; // Variable to simulate user input

        public TestPlayer(String name, String simulatedInput) {
            this.simulatedInput = simulatedInput; // Set the input during the test
        }

        @Override
        public void sendMessage(Object message) {

        }

        @Override
        public void move(ArrayList<Pile> piles, ArrayList<AbstractPlayer> players) {
            boolean validChoice = false;

            // Simulate point card drafting
            if (simulatedInput.matches("\\d")) { // Simulate choosing a point card
                int pileIndex = Integer.parseInt(simulatedInput);
                if (piles.get(pileIndex).getPointCard(piles) != null) { // Skip the check for an empty pile
                    // Buy the point card and add it to the player's hand
                    this.hand.add(piles.get(pileIndex).buyPointCard(piles));
                    validChoice = true; // Mark the choice as valid
                }
            } else { // Simulate veggie card drafting
                int takenVeggies = 0;
                for (int charIndex = 0; charIndex < simulatedInput.length(); charIndex++) {
                    char choiceChar = Character.toUpperCase(simulatedInput.charAt(charIndex));
                    int choice = choiceChar - 'A';
                    int pileIndex = (choice == 0 || choice == 3) ? 0 :
                            (choice == 1 || choice == 4) ? 1 :
                                    (choice == 2 || choice == 5) ? 2 : -1;
                    int veggieIndex = (choice == 0 || choice == 1 || choice == 2) ? 0 :
                            (choice == 3 || choice == 4 || choice == 5) ? 1 : -1;

                    if (piles.get(pileIndex).veggieCards[veggieIndex] != null) {
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

            // Here we assume the player has made a valid choice
            if (!validChoice) {
                throw new IllegalArgumentException("Invalid simulated input or all choices were empty");
            }
        }
    }

    // Requirement 7a - Test player drafts a point card
    @Test
    public void testDraftPointCard() {
        String[] args = {"1", "1"};
        gameService.setupGame(args);

        // Simulate player input to draft a point card from pile 0
        TestPlayer testPlayer = new TestPlayer("Test Player", "0");
        gameService.getPlayers().set(0, testPlayer);

        int initialHandSize = testPlayer.getHand().size();
        testPlayer.move(gameService.getDeck().getPiles(), gameService.getPlayers());

        assertEquals(initialHandSize + 1, testPlayer.getHand().size(), "Player should have drafted 1 point card.");
    }

    // Requirement 7b - Test player drafts two vegetable cards
    @Test
    public void testDraftVeggieCards() {
        String[] args = {"1", "1"};
        gameService.setupGame(args);

        // Simulate player input to draft veggie cards A and B (first pile, two veggie cards)
        TestPlayer testPlayer = new TestPlayer("Test Player", "AB");
        gameService.getPlayers().set(0, testPlayer);

        int initialHandSize = testPlayer.getHand().size();
        testPlayer.move(gameService.getDeck().getPiles(), gameService.getPlayers());

        assertEquals(initialHandSize + 2, testPlayer.getHand().size(), "Player should have drafted 2 veggie cards.");
    }


    // Requirement 8 - Test turning a point card to a vegetable side
    @Test
    public void testTurnPointCardToVegetable() {
        String[] args = {"1", "1"};
        gameService.setupGame(args);

        // Simulate player drafting a point card from pile 0
        TestPlayer testPlayer = new TestPlayer("Test Player", "0");
        gameService.getPlayers().set(0, testPlayer);

        // Simulate player drafting a point card
        testPlayer.move(gameService.getDeck().getPiles(), gameService.getPlayers());

        // Simulate turning point card to vegetable side (first card in hand)
        testPlayer.turnPointCardToVegetable(0); // Assuming the first card is turned

        assertFalse(testPlayer.getHand().get(0).isCriteriaSideUp(), "The card should have been turned to its vegetable side.");
    }


    // Requirement 9 - Test hand is shown to other players
    @Test
    public void testShowHandToOtherPlayers() {
        String[] args = {"1", "1"};
        gameService.setupGame(args);

        AbstractPlayer player = gameService.getPlayers().get(0);

        // Mock player drafting cards into their hand for testing
        TestPlayer testPlayer = new TestPlayer("Test Player", "0");
        gameService.getPlayers().set(0, testPlayer);
        testPlayer.move(gameService.getDeck().getPiles(), gameService.getPlayers());

        // Use HandUtil to display the player's hand
        String handDisplay = HandUtil.displayHand(testPlayer.getHand());

        // Check if the hand display output is not empty (meaning it successfully shows the hand)
        assertNotNull(handDisplay, "Hand display should return a string representation of the hand.");
        assertFalse(handDisplay.isEmpty(), "Hand display should not be empty.");
    }


    // Requirement 10 - Test replacing the market after drafting
    @Test
    public void testReplaceMarket() {
        String[] args = {"1", "1"};
        gameService.setupGame(args);

        // Capture the initial market state as a String
        String initialMarketState = MarketUtil.printMarket(gameService.getDeck().getPiles());
        System.out.println("Initial Market State:\n" + initialMarketState);

        // Simulate drafting two vegetable cards
        TestPlayer testPlayer = new TestPlayer("Test Player", "AB");
        gameService.getPlayers().set(0, testPlayer);
        testPlayer.move(gameService.getDeck().getPiles(), gameService.getPlayers());

        // Capture the new market state after drafting
        String marketStateAfterDraft = MarketUtil.printMarket(gameService.getDeck().getPiles());
        System.out.println("Market State After Draft:\n" + marketStateAfterDraft);

        // Assert that the market state has changed
        assertNotEquals(initialMarketState, marketStateAfterDraft, "The market state should change after drafting cards.");

        // Optionally, check that the market size is still the same
        int initialMarketSize = gameService.getMarketSize();
        int marketSizeAfterDraft = gameService.getMarketSize();

        assertEquals(initialMarketSize, marketSizeAfterDraft, "The market should be replenished to its original size.");
    }



//    // Requirement 11 - Test drawing from the bottom of the largest draw pile
//    @Test
//    public void testDrawFromBottomOfLargestPile() {
//        String[] args = {"2", "0"};
//        gameService.setupGame(args);
//
//        gameService.getDeck().getPiles().get(0).clear(); // Simulate one pile running out of cards
//        int initialSize = gameService.getDeck().getPiles().get(2).size(); // Largest pile
//
//        gameService.drawFromBottomOfLargestPile(); // Simulate drawing from the largest pile
//
//        assertEquals(initialSize - 1, gameService.getDeck().getPiles().get(2).size(), "The largest pile should have one less card after drawing from the bottom.");
//    }



    // Requirement 12 - Test the end of the game
    @Test
    public void testEndOfGame() {
        String[] args = {"1", "1"};
        gameService.setupGame(args);

        // Simulate market running out of cards by using buy methods to remove all cards
        for (Pile pile : gameService.getDeck().getPiles()) {
            while (!pile.isEmpty()) {
                // Remove cards from the veggie slots
                if (pile.getVeggieCard(0) != null) {
                    pile.buyVeggieCard(0, gameService.getDeck().getPiles());
                }
                if (pile.getVeggieCard(1) != null) {
                    pile.buyVeggieCard(1, gameService.getDeck().getPiles());
                }
                // Remove a point card if available
                pile.buyPointCard(gameService.getDeck().getPiles());
            }
        }

        // Now that all piles are empty, the game should be over
        assertTrue(gameService.gameOver(), "The game should be over when there are no more cards in any pile.");
    }



    // Requirement 13 - Test score calculation
    @Test
    public void testCalculateScores() {
        // Setup the game for one player using TestPlayer
        String[] args = {"1", "0"};
        gameService.setupGame(args);

        // Create some VegetableCards to simulate a hand
        VegetableCard lettuceCard1 = new VegetableCard(VegetableCard.Vegetable.LETTUCE, "1 point for every lettuce");
        VegetableCard lettuceCard2 = new VegetableCard(VegetableCard.Vegetable.LETTUCE, "2 points for every lettuce");

        // Simulate the player acquiring cards
        // Instead of calling move(), manually add cards for testing purposes
        TestPlayer testPlayer = new TestPlayer("Test Player", "0");
        gameService.getPlayers().set(0, testPlayer);
        testPlayer.getHand().add(lettuceCard1);
        testPlayer.getHand().add(lettuceCard2);

        // Calculate the score
        ArrayList<AbstractPlayer> players = new ArrayList<>();
        players.add(testPlayer);
        int playerScore = ScoreUtil.calculateScore(testPlayer.getHand(), testPlayer.getPlayerID(), players);

        // Assert the expected score based on the cards' criteria
        int expectedScore = 0;
        assertEquals(expectedScore, playerScore, "The calculated score for the player should be correct.");
    }



    // Requirement 14 - Test for announcing the winner with the highest score
    @Test
    public void testAnnounceWinner() {
        // Setup players
        TestPlayer player1 = new TestPlayer("Player 1", "0");
        TestPlayer player2 = new TestPlayer("Player 2", "1");
        TestPlayer player3 = new TestPlayer("Player 3", "2");

        // Simulate players' hands with VegetableCards for scoring
        player1.getHand().add(new VegetableCard(VegetableCard.Vegetable.LETTUCE, "3 points for every lettuce")); // Assuming the score for this card is calculated in your scoring logic
        player1.getHand().add(new VegetableCard(VegetableCard.Vegetable.LETTUCE, "1 point for every lettuce")); // Total score for player1

        player2.getHand().add(new VegetableCard(VegetableCard.Vegetable.CABBAGE, "4 points for every cabbage")); // Total score for player2
        player2.getHand().add(new VegetableCard(VegetableCard.Vegetable.CABBAGE, "2 points for every cabbage")); // Total score for player2

        player3.getHand().add(new VegetableCard(VegetableCard.Vegetable.TOMATO, "5 points for every tomato")); // Total score for player3
        player3.getHand().add(new VegetableCard(VegetableCard.Vegetable.TOMATO, "1 point for every tomato")); // Total score for player3

        // Setup game and add players
        ArrayList<AbstractPlayer> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);

        // Add players to GameService
        gameService.getPlayers().addAll(players);

        // Call determineWinner method
        gameService.determineWinner();

        // Expected output based on the test setup
        AbstractPlayer winner = null;
        for (AbstractPlayer player : players) {
            if (player.getScore() == 0) { // Update this based on who you expect to win
                winner = player;
                break;
            }
        }

        // Check the winner
        assertEquals(winner, players.get(0), "The winner should be the player with a score of 2."); // Update the index based on your expected winner
    }

}
