package org.katas.model;

import java.util.List;

public record TrainDetails(TripDetails details, List<Passenger> passengers) {
}
