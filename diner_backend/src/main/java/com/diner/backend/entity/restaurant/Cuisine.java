package com.diner.backend.entity.restaurant;

public enum Cuisine {
    NORTH_INDIAN("North Indian"),
    SOUTH_INDIAN("South Indian"),
    CHINESE("Chinese"),
    ITALIAN("Italian"),
    MEXICAN("Mexican"),
    THAI("Thai"),
    JAPANESE("Japanese"),
    KOREAN("Korean"),
    CONTINENTAL("Continental"),
    AMERICAN("American"),
    GREEK("Greek"),
    SPANISH("Spanish"),
    FRENCH("French"),
    LEBANESE("Lebanese"),
    VIETNAMESE("Vietnamese"),
    TURKISH("Turkish"),
    BENGALI("Bengali"),
    GUJARATI("Gujarati"),
    RAJASTHANI("Rajasthani"),
    MUGHLAI("Mughlai"),
    TIBETAN("Tibetan"),
    SEAFOOD("Seafood"),
    STREET_FOOD("Street Food"),
    DESSERTS("Desserts"),
    BIRYANI("Biryani"),
    PIZZA("Pizza"),
    BURGERS("Burgers"),
    PASTA("Pasta"),
    HEALTHY_FOOD("Healthy Food"),
    VEGAN("Vegan"),
    BBQ("BBQ"),
    CAFE("Cafe"),
    BAKERY("Bakery");

    private final String displayName;

    Cuisine(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}