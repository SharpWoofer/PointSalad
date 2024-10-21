package PointSalad.Utilities;

import PointSalad.Models.Card;
import PointSalad.Models.VegetableCard;

import java.util.ArrayList;

public class HandUtil {

    public static String displayHand(ArrayList<Card> hand) {
        String handString = "Criteria:\t";
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) instanceof VegetableCard) {
                VegetableCard vegetableCard = (VegetableCard) hand.get(i);
                if (vegetableCard.isCriteriaSideUp()) {
                    handString += "[" + i + "] " + vegetableCard.getCriteria() + " (" + vegetableCard.getVegetable().toString() + ")" + "\t";
                }
            }
        }
        handString += "\nVegetables:\t";
        // Sum up the number of each vegetable and show the total number of each vegetable
        for (VegetableCard.Vegetable vegetable : VegetableCard.Vegetable.values()) {
            int count = ScoreUtil.countVegetables(hand, vegetable); // Updated call to ScoreUtil
            if (count > 0) {
                handString += vegetable + ": " + count + "\t";
            }
        }
        return handString;
    }
}
