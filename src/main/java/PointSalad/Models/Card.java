package PointSalad.Models;

public interface Card {
    String getDescription();
    boolean isCriteriaSideUp();
    VegetableCard.Vegetable getVegetable();
}
