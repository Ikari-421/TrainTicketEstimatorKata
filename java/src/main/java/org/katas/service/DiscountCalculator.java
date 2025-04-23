package org.katas.service;

import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TripDetails;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class DiscountCalculator {

    public double applyAgeDiscount(Passenger passenger, double calculedPrice, double basePrice) {
        if (passenger.age() < 1) {
            calculedPrice = 0;
        } else if (passenger.age() <= 17) {
            calculedPrice -= basePrice * 0.4;
        } else if (passenger.age() >= 70) {
            calculedPrice -= basePrice * 0.2;
        } else {
            calculedPrice = basePrice * 1.2;
        }
        return calculedPrice;
    }

    public double applyDateDiscount(TripDetails tripDetails, double calculedPrice, double basePrice) {
        Date currentDate = new Date();
        long timeDiff = tripDetails.when().getTime() - currentDate.getTime();
        int diffDays = (int) ((tripDetails.when().getTime() / (24 * 60 * 60 * 1000)) -
                (currentDate.getTime() / (24 * 60 * 60 * 1000)));

        if (diffDays >= 30 || timeDiff <= (6 * 60 * 60 * 1000)) {
            calculedPrice -= basePrice * 0.2;
        } else if (diffDays >= 5) {
            calculedPrice += (20 - diffDays) * 0.02 * basePrice;
        } else {
            calculedPrice += basePrice;
        }

        return calculedPrice;
    }

    public double applyCardDiscount(Passenger passenger, double calculedPrice, Set<String> lastNamesFamilyDiscountCard, double basePrice, List<Passenger> passengers) {
        if (passenger.age() > 0 && passenger.age() < 4) {
            calculedPrice = 9;
        }

        if (lastNamesFamilyDiscountCard.contains(passenger.lastName())) {
            calculedPrice = DiscountCard.Family.applyDiscount(calculedPrice, basePrice);
        }

        if (passenger.discounts().contains(DiscountCard.Senior) && !lastNamesFamilyDiscountCard.contains(passenger.lastName())) {
            calculedPrice = DiscountCard.Senior.applyDiscount(calculedPrice, basePrice);
        }

        if (passenger.discounts().contains(DiscountCard.TrainStroke)) {
            calculedPrice = DiscountCard.TrainStroke.applyDiscount(calculedPrice, basePrice);
        }

        if (passenger.age() >= 18 && !lastNamesFamilyDiscountCard.contains(passenger.lastName())) {
            if (passengers.size() == 2 && passengers.stream().anyMatch(p -> p.discounts().contains(DiscountCard.Couple))) {
                calculedPrice = DiscountCard.Couple.applyDiscount(calculedPrice, basePrice);
            }

            if (passengers.size() == 1 && passenger.discounts().contains(DiscountCard.HalfCouple)) {
                calculedPrice = DiscountCard.HalfCouple.applyDiscount(calculedPrice, basePrice);
            }
        }

        return calculedPrice;
    }
}
