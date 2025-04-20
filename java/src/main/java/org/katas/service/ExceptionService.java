package org.katas.service;

import org.katas.exceptions.InvalidTripInputException;
import org.katas.model.Passenger;
import org.katas.model.TripDetails;

import java.util.Date;
import java.util.List;

public class ExceptionService {
    public static Integer throwException(List<Passenger> passengers, TripDetails tripDetails) {
        if (passengers.isEmpty()) {
            return 0;
        }

        if (tripDetails.from().trim().isEmpty()) {
            throw new InvalidTripInputException("Start city is invalid");
        }

        if (tripDetails.to().trim().isEmpty()) {
            throw new InvalidTripInputException("Destination city is invalid");
        }

        if (tripDetails.when().before(new Date())) {
            throw new InvalidTripInputException("Date is invalid");
        }

        if (passengers.stream().anyMatch(p -> p.age() < 0)) {
            throw new InvalidTripInputException("Age is invalid");
        }
        return null;
    }
}