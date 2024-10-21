package PointSalad.Utilities;

import PointSalad.Models.AbstractPlayer;
import PointSalad.Models.Card;
import PointSalad.Models.VegetableCard;

import java.util.ArrayList;
import java.util.List;

public class ScoreUtil {

    public static int calculateScore(ArrayList<Card> hand, int playerID, ArrayList<AbstractPlayer> players) {
        int totalScore = 0;

        for (Card card : hand) {
            if (card instanceof VegetableCard) {
                VegetableCard criteriaCard = (VegetableCard) card;
                if (criteriaCard.isCriteriaSideUp()) {
                    String criteria = criteriaCard.getCriteria();
                    String[] parts = criteria.split(",");

                    if (criteria.contains("TOTAL") || criteria.contains("TYPE") || criteria.contains("SET")) {
                        if (criteria.contains("TOTAL")) {
                            int countVeg = countTotalVegetables(hand);
                            int thisHandCount = countVeg;
                            for (AbstractPlayer p : players) {
                                if (p.getPlayerID() != playerID) {  // Use passed playerID
                                    int playerVeg = countTotalVegetables(p.getHand());
                                    if (criteria.contains("MOST") && playerVeg > countVeg) {
                                        countVeg = playerVeg;
                                    }
                                    if (criteria.contains("FEWEST") && playerVeg < countVeg) {
                                        countVeg = playerVeg;
                                    }
                                }
                            }
                            if (countVeg == thisHandCount) {
                                totalScore += Integer.parseInt(criteria.substring(criteria.indexOf("=") + 1).trim());
                            }
                        }

                        if (criteria.contains("TYPE")) {
                            String[] expr = criteria.split("/");
                            int addScore = Integer.parseInt(expr[0].trim());
                            if (expr[1].contains("MISSING")) {
                                int missing = 0;
                                for (VegetableCard.Vegetable vegetable : VegetableCard.Vegetable.values()) {
                                    if (countVegetables(hand, vegetable) == 0) {
                                        missing++;
                                    }
                                }
                                totalScore += missing * addScore;
                            } else {
                                int atLeastPerVegType = Integer.parseInt(expr[1].substring(expr[1].indexOf(">=") + 2).trim());
                                int totalType = 0;
                                for (VegetableCard.Vegetable vegetable : VegetableCard.Vegetable.values()) {
                                    int countVeg = countVegetables(hand, vegetable);
                                    if (countVeg >= atLeastPerVegType) {
                                        totalType++;
                                    }
                                }
                                totalScore += totalType * addScore;
                            }
                        }

                        if (criteria.contains("SET")) {
                            int addScore = 12;
                            for (VegetableCard.Vegetable vegetable : VegetableCard.Vegetable.values()) {
                                int countVeg = countVegetables(hand, vegetable);
                                if (countVeg == 0) {
                                    addScore = 0;
                                    break;
                                }
                            }
                            totalScore += addScore;
                        }
                    } else if (criteria.contains("MOST") || criteria.contains("FEWEST")) {
                        int vegIndex = criteria.contains("MOST") ? criteria.indexOf("MOST") + 5 : criteria.indexOf("FEWEST") + 7;
                        String veg = criteria.substring(vegIndex, criteria.indexOf("=")).trim();
                        int countVeg = countVegetables(hand, VegetableCard.Vegetable.valueOf(veg));
                        int nrVeg = countVeg;
                        for (AbstractPlayer p : players) {
                            if (p.getPlayerID() != playerID) {  // Use passed playerID
                                int playerVeg = countVegetables(p.getHand(), VegetableCard.Vegetable.valueOf(veg));
                                if (criteria.contains("MOST") && playerVeg > nrVeg) {
                                    nrVeg = playerVeg;
                                }
                                if (criteria.contains("FEWEST") && playerVeg < nrVeg) {
                                    nrVeg = playerVeg;
                                }
                            }
                        }
                        if (nrVeg == countVeg) {
                            totalScore += Integer.parseInt(criteria.substring(criteria.indexOf("=") + 1).trim());
                        }
                    } else if (parts.length > 1 || criteria.contains("+") || parts[0].contains("/")) {
                        if (criteria.contains("+")) {
                            String expr = criteria.split("=")[0].trim();
                            String[] vegs = expr.split("\\+");
                            int[] nrVeg = new int[vegs.length];
                            int countSameKind = 1;
                            for (int j = 1; j < vegs.length; j++) {
                                if (vegs[0].trim().equals(vegs[j].trim())) {
                                    countSameKind++;
                                }
                            }
                            if (countSameKind > 1) {
                                totalScore += (countVegetables(hand, VegetableCard.Vegetable.valueOf(vegs[0].trim())) / countSameKind) * Integer.parseInt(criteria.split("=")[1].trim());
                            } else {
                                for (int i = 0; i < vegs.length; i++) {
                                    nrVeg[i] = countVegetables(hand, VegetableCard.Vegetable.valueOf(vegs[i].trim()));
                                }
                                int min = nrVeg[0];
                                for (int x = 1; x < nrVeg.length; x++) {
                                    if (nrVeg[x] < min) {
                                        min = nrVeg[x];
                                    }
                                }
                                totalScore += min * Integer.parseInt(criteria.split("=")[1].trim());
                            }
                        } else if (parts[0].contains("=")) {
                            String veg = parts[0].substring(0, parts[0].indexOf(":"));
                            int countVeg = countVegetables(hand, VegetableCard.Vegetable.valueOf(veg));
                            totalScore += (countVeg % 2 == 0) ? 7 : 3;
                        } else {
                            for (int i = 0; i < parts.length; i++) {
                                String[] veg = parts[i].split("/");
                                totalScore += Integer.parseInt(veg[0].trim()) * countVegetables(hand, VegetableCard.Vegetable.valueOf(veg[1].trim()));
                            }
                        }
                    }
                }
            }
        }
        return totalScore;
    }


    // Counts how many of a specific vegetable are in the player's hand
    public static int countVegetables(List<Card> hand, VegetableCard.Vegetable vegetable) {
        int count = 0;
        for (Card card : hand) {
            if (card instanceof VegetableCard) {
                VegetableCard veggieCard = (VegetableCard) card;
                if (!veggieCard.isCriteriaSideUp() && veggieCard.getVegetable() == vegetable) {
                    count++;
                }
            }
        }
        return count;
    }


    // Counts the total number of vegetables in the player's hand
    public static int countTotalVegetables(List<Card> hand) {
        int count = 0;
        for (Card card : hand) {
            if (card instanceof VegetableCard) {
                VegetableCard veggieCard = (VegetableCard) card;
                if (!veggieCard.isCriteriaSideUp()) {
                    count++;
                }
            }
        }
        return count;
    }

}
