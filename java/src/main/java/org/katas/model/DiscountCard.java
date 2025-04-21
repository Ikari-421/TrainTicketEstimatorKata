package org.katas.model;

import java.util.Optional;

public enum DiscountCard
{
    Senior("Senior"){
        @Override
        public double applyDiscount(double calculedPrice, double basePrice) {
            return calculedPrice - basePrice * 0.2;
        }
    },
    TrainStroke("TrainStroke") {
        @Override
        public double applyDiscount(double calculedPrice, double basePrice) {
            return 1;
        }
    },
    Couple("Couple") {
        @Override
        public double applyDiscount(double calculedPrice, double basePrice) {
            return  calculedPrice - basePrice * 0.2 * 2;
        }
    },
    HalfCouple("HalfCouple") {
        @Override
        public double applyDiscount(double calculedPrice, double basePrice) {
            return  calculedPrice - basePrice * 0.1;
        }
    },
    Family("Family") {
        @Override
        public double applyDiscount(double calculedPrice, double basePrice) {
            return 0;
        }
    };

    private final String card;

    DiscountCard(String card) {
        this.card = card;
    }

    String getCard() {
        return card;
    }

    public abstract double applyDiscount(double calculedPrice, double basePrice);

}
