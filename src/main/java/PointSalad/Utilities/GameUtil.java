package PointSalad.Utilities;

import PointSalad.Models.AbstractPlayer;
import PointSalad.Models.Card;
import PointSalad.Models.VegetableCard;
import PointSalad.Models.Pile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameUtil {

    // Send a message to all players in the game
    public static void sendToAllPlayers(String message, ArrayList<AbstractPlayer> players) {
        for (AbstractPlayer player : players) {
            player.sendMessage(message);
        }
    }

}
