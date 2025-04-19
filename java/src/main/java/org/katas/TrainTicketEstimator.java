package org.katas;

import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TrainDetails;
import org.katas.model.exceptions.ApiException;
import org.katas.model.exceptions.InvalidTripInputException;
import org.katas.service.ApiCallService;

import java.util.Date;
import java.util.List;

public class TrainTicketEstimator {

    public double estimate(TrainDetails trainDetails, IApiCall iApiCall) {
        if (trainDetails.passengers().isEmpty()) {
            return 0;
        }

        if (trainDetails.details().from().trim().isEmpty()) {
            throw new InvalidTripInputException("Start city is invalid");
        }

        if (trainDetails.details().to().trim().isEmpty()) {
            throw new InvalidTripInputException("Destination city is invalid");
        }

        if (trainDetails.details().when().before(new Date())) {
            throw new InvalidTripInputException("Date is invalid");
        }

        // Extraction de l'appel d'API
        double basePrice = iApiCall.getBasePrice(trainDetails);


        if (basePrice == -1) {
            throw new ApiException();
        }

        List<Passenger> passengers = trainDetails.passengers();
        double total = 0;
        double temp = basePrice;

        for (Passenger passenger : passengers) {
            if (passenger.age() < 0) {
                throw new InvalidTripInputException("Age is invalid");
            }

            if (passenger.age() < 1) {
                temp = 0;
            } else if (passenger.age() <= 17) {
                temp = basePrice * 0.6;
            } else if (passenger.age() >= 70) {
                temp = basePrice * 0.8;
                if (passenger.discounts().contains(DiscountCard.Senior)) {
                    temp -= basePrice * 0.2;
                }
            } else {
                temp = basePrice * 1.2;
            }

            Date currentDate = new Date();
            currentDate.setDate(currentDate.getDate() +30);
            if (trainDetails.details().when().getTime() >= currentDate.getTime() ) {
                temp -= basePrice * 0.2;
            } else {
                currentDate.setDate(currentDate.getDate() -30 + 5);
                if (trainDetails.details().when().getTime() > currentDate.getTime()) {
                    currentDate.setDate(currentDate.getDate() - 5);
                    var diffDays = ((int)((trainDetails.details().when().getTime()/(24*60*60*1000)) - (int)(currentDate.getTime()/(24*60*60*1000))));
                    temp += (20 - diffDays) * 0.02 * basePrice;
                } else {
                    temp += basePrice;
                }
            }

            if (passenger.age() > 0 && passenger.age() < 4) {
                temp = 9;
            }

            if (passenger.discounts().contains(DiscountCard.TrainStroke)) {
                temp = 1;
            }

            total += temp;
            temp = basePrice;
        }

        if (passengers.size() == 2) {
            boolean couple = false;
            boolean minor = false;
            for (Passenger passenger : passengers) {
                if (passenger.discounts().contains(DiscountCard.Couple)) {
                    couple = true;
                }
                if (passenger.age() < 18) {
                    minor = true;
                }
            }
            if (couple && !minor) {
                total -= basePrice * 0.2 * 2;
            }
        }

        if (passengers.size() == 1) {
            boolean halfCouple = false;
            boolean minor = false;
            for (Passenger passenger : passengers) {
                if (passenger.discounts().contains(DiscountCard.HalfCouple)) {
                    halfCouple = true;
                }
                if (passenger.age() < 18) {
                    minor = true;
                }
            }
            if (halfCouple && !minor) {
                total -= basePrice * 0.1;
            }
        }

        return total;
    }
}