package org.katas;

import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TrainDetails;
import org.katas.model.TripDetails;
import org.katas.service.ValidationService;
import org.katas.service.IApiCall;

import java.util.Date;
import java.util.List;

public class TrainTicketEstimator {

    public double estimate(TrainDetails trainDetails, IApiCall iApiCall) {

        List<Passenger> passengers = trainDetails.passengers();
        if (passengers.isEmpty()) {
            return 0;
        }

        TripDetails tripDetails = trainDetails.details();
        double basePrice = iApiCall.getBasePrice(trainDetails);
        double total = 0;

        ValidationService validationService = new ValidationService();
        validationService.validationDetails(tripDetails, passengers);

        for (Passenger passenger : passengers) {

            double calculedPrice = basePrice;

            calculedPrice = applyAgeDiscount(passenger, calculedPrice, basePrice);

            calculedPrice = applyDateDiscount(tripDetails, calculedPrice, basePrice);

            if (passenger.age() > 0 && passenger.age() < 4) {
                calculedPrice = 9;
            }

            if (passenger.discounts().contains(DiscountCard.Senior)) {
                calculedPrice = DiscountCard.Senior.applyDiscount(calculedPrice, basePrice);
            }

            if (passenger.discounts().contains(DiscountCard.TrainStroke)) {
                calculedPrice = DiscountCard.TrainStroke.applyDiscount(calculedPrice, basePrice);
            }

            total += calculedPrice;
        }

        boolean hasMinor = passengers.stream().anyMatch(p -> p.age() < 18);

        if (!hasMinor) {
            if (passengers.size() == 2 && passengers.stream().anyMatch(p -> p.discounts().contains(DiscountCard.Couple))) {

                total -= basePrice * 0.2 * 2;
            } else if (passengers.size() == 1 && passengers.stream().anyMatch(p -> p.discounts().contains(DiscountCard.HalfCouple))) {
                total -= basePrice * 0.1;
            }
        }

        return total;
    }



    private double applyAgeDiscount(Passenger passenger, double calculedPrice, double basePrice) {
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

    private double applyDateDiscount(TripDetails tripDetails, double calculedPrice, double basePrice) {
        Date currentDate = new Date();
        long timeDiff = tripDetails.when().getTime() - currentDate.getTime();
        var diffDays = ((int)((tripDetails.when().getTime() / (24 * 60 * 60 * 1000)) - (int)(currentDate.getTime() / (24 * 60 * 60 * 1000))));

        if(diffDays >= 30 || timeDiff <= (6 * 60 * 60 * 1000)) {
            calculedPrice -= basePrice * 0.2;
        }else if(diffDays >= 5) {
            calculedPrice += (20 - diffDays) * 0.02 * basePrice;
        } else {
            calculedPrice += basePrice;
        }

        return calculedPrice;
    }
}