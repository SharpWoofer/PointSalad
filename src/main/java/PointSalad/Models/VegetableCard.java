package PointSalad.Models;

public class VegetableCard implements Card {
    public enum Vegetable {
        PEPPER, LETTUCE, CARROT, CABBAGE, ONION, TOMATO
    }

    private Vegetable vegetable;
    private String criteria;
    private boolean criteriaSideUp = true;

    public VegetableCard(Vegetable vegetable, String criteria) {
        this.vegetable = vegetable;
        this.criteria = criteria;
    }

    @Override
    public String getDescription() {
        if (criteriaSideUp) {
            return criteria + " (" + vegetable + ")";
        } else {
            return vegetable.toString();
        }
    }

    public Vegetable getVegetable() {
        return vegetable;
    }

    public String getCriteria() {
        return criteria;
    }

    public boolean isCriteriaSideUp() {
        return criteriaSideUp;
    }

    public void setCriteriaSideUp(boolean criteriaSideUp) {
        this.criteriaSideUp = criteriaSideUp;
    }

}
