package PointSalad.Models;

import PointSalad.Utilities.JsonUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards = new ArrayList<>();
    private ArrayList<Pile> piles = new ArrayList<>();

    // Setup deck based on the number of players
    public void setupDeckForPlayers(int playerCount) {
        List<Card> fullDeck = JsonUtil.loadDeck();
        int cardsPerVeggie = calculateCardsPerVeggie(playerCount);

        // Loop through each vegetable type in VegetableCard.Vegetable
        for (VegetableCard.Vegetable veggie : VegetableCard.Vegetable.values()) {
            List<Card> veggieCards = getVegetableCards(fullDeck, veggie);
            Collections.shuffle(veggieCards); // Shuffle each vegetable deck
            cards.addAll(veggieCards.subList(0, cardsPerVeggie)); // Add limited number of veggie cards
        }

        Collections.shuffle(cards); // Shuffle the combined deck
        divideDeckIntoPiles();
    }

    // Helper to determine the number of cards per veggie
    private int calculateCardsPerVeggie(int playerCount) {
        return switch (playerCount) {
            case 2 -> 6;
            case 3 -> 9;
            case 4 -> 12;
            case 5 -> 15;
            default -> playerCount * 6;
        };
    }

    // Helper to divide the deck into 3 piles
    private void divideDeckIntoPiles() {
        ArrayList<Card> pile1 = new ArrayList<>();
        ArrayList<Card> pile2 = new ArrayList<>();
        ArrayList<Card> pile3 = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            if (i % 3 == 0) {
                pile1.add(cards.get(i));
            } else if (i % 3 == 1) {
                pile2.add(cards.get(i));
            } else {
                pile3.add(cards.get(i));
            }
        }

        piles.add(new Pile(pile1));
        piles.add(new Pile(pile2));
        piles.add(new Pile(pile3));
    }

    // Return piles for GameService to use
    public ArrayList<Pile> getPiles() {
        return piles;
    }

    private List<Card> getVegetableCards(List<Card> deck, VegetableCard.Vegetable veggie) {
        List<Card> veggieCards = new ArrayList<>();
        for (Card card : deck) {
            if (card instanceof VegetableCard && ((VegetableCard) card).getVegetable() == veggie) {
                veggieCards.add(card);
            }
        }
        return veggieCards;
    }

    public int getTotalCards() { return cards.size(); }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
