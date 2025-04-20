package org.katas;

import org.katas.model.*;
import org.katas.service.ExceptionService;
import org.katas.service.IApiCall;

import java.util.Date;
import java.util.List;

public class TrainTicketEstimator {
    private double basePrice;
    private double calculedPrice;

    public double estimate(TrainDetails trainDetails, IApiCall iApiCall) {

        // Récupération des détails du train
        TripDetails tripDetails = trainDetails.details();
        List<Passenger> passengers = trainDetails.passengers();

        // Gestion des exceptions
        ExceptionService.throwException(passengers, tripDetails);

        // Extraction de l'appel d'API
        basePrice = iApiCall.getBasePrice(trainDetails);

        double total = 0;
        calculedPrice = basePrice;

        for (Passenger passenger : passengers) {

            if (passenger.age() < 1) {
                calculedPrice = 0;
            } else if (passenger.age() <= 17) {
                calculedPrice -= basePrice * 0.4;
            } else if (passenger.age() >= 70) {
                calculedPrice -= basePrice * 0.2;
            } else {
                calculedPrice = basePrice * 1.2;
            }

            Date currentDate = new Date();
            long tripDatetime = tripDetails.when().getTime();

            currentDate.setDate(currentDate.getDate() +30);
            if (tripDatetime >= currentDate.getTime() ) {
                calculedPrice -= basePrice * 0.2;
            } else {
                currentDate.setDate(currentDate.getDate() -25);
                if (tripDatetime > currentDate.getTime()) {
                    currentDate.setDate(currentDate.getDate() - 5);
                    var diffDays = ((int)((tripDatetime /(24*60*60*1000)) - (int)(currentDate.getTime()/(24*60*60*1000))));
                    calculedPrice += (20 - diffDays) * 0.02 * basePrice;
                } else {
                    calculedPrice += basePrice;
                }
            }

            if (passenger.age() > 0 && passenger.age() < 4) {
                calculedPrice = 9;
            }

            if (passenger.discounts().contains(DiscountCard.Senior)) {
                calculedPrice -= basePrice * 0.2;
            }

            if (passenger.discounts().contains(DiscountCard.TrainStroke)) {
                calculedPrice = 1;
            }

            total += calculedPrice;
            calculedPrice = basePrice;
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