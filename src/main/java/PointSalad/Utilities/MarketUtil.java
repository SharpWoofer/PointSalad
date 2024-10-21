package PointSalad.Utilities;

import PointSalad.Models.Pile;
import PointSalad.Models.VegetableCard;

import java.util.List;

public class MarketUtil {

    // Static method to print the current state of the market (piles)
    public static String printMarket(List<Pile> piles) {
        String pileString = "Point Cards:\t";

        // Loop through each pile and display the point cards (or empty if none)
        for (int p = 0; p < piles.size(); p++) {
            if (piles.get(p).getPointCard(piles) == null) {
                pileString += "[" + p + "]" + String.format("%-43s", "Empty") + "\t";
            } else {
                pileString += "[" + p + "]" + String.format("%-43s", piles.get(p).getPointCard(piles).getDescription()) + "\t";
            }
        }

        pileString += "\nVeggie Cards:\t";
        char veggieCardIndex = 'A';

        // Display the first vegetable card in each pile
        for (Pile pile : piles) {
            VegetableCard veggieCard = (VegetableCard) pile.getVeggieCard(0);
            pileString += "[" + veggieCardIndex + "]" + String.format("%-43s", veggieCard != null ? veggieCard.getDescription() : "Empty") + "\t";
            veggieCardIndex++;
        }

        pileString += "\n\t\t";

        // Display the second vegetable card in each pile
        for (Pile pile : piles) {
            VegetableCard veggieCard = (VegetableCard) pile.getVeggieCard(1);
            pileString += "[" + veggieCardIndex + "]" + String.format("%-43s", veggieCard != null ? veggieCard.getDescription() : "Empty") + "\t";
            veggieCardIndex++;
        }

        return pileString;
    }
}
