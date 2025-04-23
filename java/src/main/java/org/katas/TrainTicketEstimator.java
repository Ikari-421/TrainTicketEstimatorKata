package org.katas;

import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TrainDetails;
import org.katas.model.TripDetails;
import org.katas.service.ValidationService;
import org.katas.service.IApiCall;
import org.katas.service.DiscountCalculator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        DiscountCalculator calculator = new DiscountCalculator();

        Set<String> lastNamesFamilyDiscountCard = passengers.stream()
                .filter(p -> p.discounts().contains(DiscountCard.Family))
                .map(Passenger::lastName)
                .collect(Collectors.toSet());

        for (Passenger passenger : passengers) {

            double calculedPrice = basePrice;

            calculedPrice = calculator.applyAgeDiscount(passenger, calculedPrice, basePrice);
            calculedPrice = calculator.applyDateDiscount(tripDetails, calculedPrice, basePrice);
            calculedPrice = calculator.applyCardDiscount(passenger, calculedPrice, lastNamesFamilyDiscountCard, basePrice, passengers);

            total += calculedPrice;
        }

        return total;
    }
}
