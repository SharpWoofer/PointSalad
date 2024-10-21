package PointSalad;

import PointSalad.Controllers.GameController;

public class PointSalad {
    public static void main(String[] args) {
        GameController controller = new GameController();
        controller.runGame(args);
    }
}