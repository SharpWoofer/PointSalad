package PointSalad.Utilities;

import PointSalad.Models.Card;
import PointSalad.Models.VegetableCard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JsonUtil {
    public static List<Card> loadDeck() {
        List<Card> deck = new ArrayList<>();

        try (InputStream fInputStream = new FileInputStream("./data/PointSaladManifest.json");
             Scanner scanner = new Scanner(fInputStream, "UTF-8").useDelimiter("\\A")) {

            String jsonString = scanner.hasNext() ? scanner.next() : "";

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray cardsArray = jsonObject.getJSONArray("cards");

            for (int i = 0; i < cardsArray.length(); i++) {
                JSONObject cardJson = cardsArray.getJSONObject(i);

                // Get the criteria object from the card JSON
                JSONObject criteriaObj = cardJson.getJSONObject("criteria");

                // Add each vegetable card to the deck with its corresponding criteria
                if (criteriaObj.has("PEPPER")) {
                    deck.add(new VegetableCard(VegetableCard.Vegetable.PEPPER, criteriaObj.getString("PEPPER")));
                }
                if (criteriaObj.has("LETTUCE")) {
                    deck.add(new VegetableCard(VegetableCard.Vegetable.LETTUCE, criteriaObj.getString("LETTUCE")));
                }
                if (criteriaObj.has("CARROT")) {
                    deck.add(new VegetableCard(VegetableCard.Vegetable.CARROT, criteriaObj.getString("CARROT")));
                }
                if (criteriaObj.has("CABBAGE")) {
                    deck.add(new VegetableCard(VegetableCard.Vegetable.CABBAGE, criteriaObj.getString("CABBAGE")));
                }
                if (criteriaObj.has("ONION")) {
                    deck.add(new VegetableCard(VegetableCard.Vegetable.ONION, criteriaObj.getString("ONION")));
                }
                if (criteriaObj.has("TOMATO")) {
                    deck.add(new VegetableCard(VegetableCard.Vegetable.TOMATO, criteriaObj.getString("TOMATO")));
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        return deck;
    }
}
