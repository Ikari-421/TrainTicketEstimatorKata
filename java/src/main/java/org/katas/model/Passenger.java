package org.katas.model;

import java.util.List;

public record Passenger(int age, List<DiscountCard> discounts, String lastName) {
    public Passenger(int age, List<DiscountCard> discounts) {
        this(age, discounts, "");
    }
}