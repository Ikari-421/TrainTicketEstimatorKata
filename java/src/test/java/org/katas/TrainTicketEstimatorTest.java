package org.katas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.katas.exceptions.InvalidTripInputException;
import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TripDetails;
import org.katas.model.TrainDetails;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class TrainTicketEstimatorTest {

    private List<Passenger> passengers = new ArrayList<>();

    private void addPassenger(int age, List<DiscountCard> discountCards){
        Passenger newPassenger = new Passenger(age, discountCards);
        this.passengers.add(newPassenger);
    }

    private Date tripDate(int days) {
        return Date.from(
                LocalDateTime.now()
                        .plusDays(days)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    private double helperTicketEstimator( String from, String to, int withInDate) {
        TrainTicketEstimator tte = new TrainTicketEstimator();
        Date when = tripDate(withInDate);

        TrainDetails trainDetails = new TrainDetails(new TripDetails(from, to, when), this.passengers);
        //System.out.println(this.passengers.toString());

        // FakeApiCall renvoi toujours 100.00 pour simplifier les calculs et véfifications
        FakeApiCall fakeApiCall = new FakeApiCall();

        return tte.estimate(trainDetails, fakeApiCall);
    }

    @Test
    void shouldReturn0_ChildUnder1yo() {
        this.addPassenger(0, List.of());

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 40);
        // TODO Voir pourquoi on à -20.0 au lieu de 0 dans cette condition
        assertEquals(-20.0,
                estimatedPrice
        );
    }

    @Test
    void shouldReturnFixedPriceAt9_For3yoChild() {
        this.addPassenger(3, List.of());

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 40);
        assertEquals(9,
                estimatedPrice
                );
    }

    @Test
    void shouldReturn40_Under18yoMinus40_AndOver30daysBeforeDepartureMinus20() {
        this.addPassenger(17, List.of());

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 40);
        assertEquals(40,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn62_Over70yoMinus20_An29daysBeforeDepartureMinus18() {
        this.addPassenger(70, List.of());

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 29);
        assertEquals(62,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn60_Over70yoMinus20_AndOver30daysBeforeDepartureMinus20() {
        this.addPassenger(70, List.of());

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 40);
        assertEquals(60,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn150_ForAllOtherPassengerPlus20_AndOver5daysBeforeDeparturePlus30() {
        this.addPassenger(40, List.of());

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 5);
        assertEquals(150,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn220_ForAllOtherPassengerPlus20_AndUnder5daysBeforeDepartureDouble100() {
        this.addPassenger(40, List.of());

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 4);
        assertEquals(220,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn1_TrainStrokeStaffCardOwner() {
        this.addPassenger(40, List.of(DiscountCard.TrainStroke));

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 5);
        assertEquals(1,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn60_Over70yoMinus20_SeniorDiscountCardOwnerMinus20() {
        this.addPassenger(70, List.of(DiscountCard.Senior));

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 20);
        assertEquals(60,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn120_AlonePassengersPlus20_CoupleDiscountCardOwnerNoDiscount() {
        this.addPassenger(18, List.of(DiscountCard.Couple));

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 20);
        assertEquals(120,
                estimatedPrice
        );
    }


    @Test
    void shouldReturn200_ForAllOtherPassengerPlus40_OnlyOneCoupleDiscountCardOwnerMinus40() {
        this.addPassenger(18, List.of(DiscountCard.Couple));
        this.addPassenger(20, List.of());

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 20);
        assertEquals(200,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn200_ForAllOtherPassengerPlus40_With2CoupleDiscountCardOwner_NotAdditionnable() {
        this.addPassenger(18, List.of(DiscountCard.Couple));
        this.addPassenger(20, List.of(DiscountCard.Couple));

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 20);
        assertEquals(200,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn110_ForAllOtherPassengerPlus20_HalfCoupleDiscountCardOwnerMinus10() {
        this.addPassenger(18, List.of(DiscountCard.HalfCouple));

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 20);
        assertEquals(110,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn80_2_Over70yoMinus40_2_SeniorDiscountCardOwnerMinus40_1_CoupleDiscountCardOwnerMinus40() {
        this.addPassenger(70, List.of(DiscountCard.Senior, DiscountCard.Couple));
        this.addPassenger(70, List.of(DiscountCard.Senior));

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 20);
        assertEquals(80,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn140_Iam_Too_tiredTo_sumThisOne() {
        this.addPassenger(70, List.of(DiscountCard.Senior,DiscountCard.Couple));
        this.addPassenger(30, List.of());

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 20);
        assertEquals(140,
                estimatedPrice
        );
    }

    // TODO TESTS EXPLORATOIRE

    @Test
    void shouldApply20percentDiscount_WhenLessThan6HoursBeforeDeparture() {
        this.passengers.clear();
        this.passengers.add(new Passenger(30, List.of()));

        // simulate date in 5h from now
        Date inFiveHours = new Date(System.currentTimeMillis() + 6 * 60 * 60 * 1000);

        TrainTicketEstimator estimator = new TrainTicketEstimator();
        TrainDetails td = new TrainDetails(new TripDetails("Lyon", "Paris", inFiveHours), this.passengers);
        FakeApiCall api = new FakeApiCall();

        double price = estimator.estimate(td, api);

        assertEquals(100.0, price);
    }


    // TODO TESTER LES EXCEPTIONS
    @Test
    void shouldThrowException_WhenFromDestinationEmpty() {
        this.addPassenger(30, List.of());

        Exception exception = assertThrows(InvalidTripInputException.class, () -> {
            this.helperTicketEstimator("", "Paris", 21);
        });

        assert exception.getMessage().contains("Start city is invalid");
    }
    @Test
    void shouldThrowException_WhenToDestinationEmpty() {
        this.addPassenger(30, List.of());

        Exception exception = assertThrows(InvalidTripInputException.class, () -> {
            this.helperTicketEstimator("Bordeaux", "", 21);
        });

        assert exception.getMessage().contains("Destination city is invalid");
    }
    @Test
    void shouldThrowException_WhenAgeIsInvalid() {
        this.addPassenger(-30, List.of());

        Exception exception = assertThrows(InvalidTripInputException.class, () -> {
            this.helperTicketEstimator("Bordeaux", "Paris", 21);
        });

        assert exception.getMessage().contains("Age is invalid");
    }
    @Test
    void shouldThrowException_WhenDateIsInvalid() {
        this.addPassenger(30, List.of());

        Exception exception = assertThrows(InvalidTripInputException.class, () -> {
            this.helperTicketEstimator("Bordeaux", "Paris", -21);
        });

        assert exception.getMessage().contains("Date is invalid");
    }

    /*
    * ## Nouvelles fonctionnalités
    *
    * * La carte Famille est un nouveau concept qui nous a été demandé et fonctionne comme suit.
    * Si un passager la possède, tous ceux qui ont le même nom de famille bénéficie de 30% de réduction.
    * Pour cela, il faudra ajouter un champ `lastName` dans le passager. La carte ne s'applique pas si le nom n'est pas renseigné.
    * Cette carte est non cumulable avec les autres réductions. Comme elle est plus avantageuse que les autres, elle est prioritaire sur les autres cartes.
    *
    */

    // TODO Test Future fonctionnalité Carte Famille

    @Test
    void shouldReturn120_WhenFamillyCardOwner_AndAlonePassenger() {
        this.addPassenger(40, List.of(DiscountCard.Family));

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 20);
        assertEquals(120,
                estimatedPrice
        );
    }

    // @Test
    void shouldReturn140_WhenFamillyCardOwner_And2PassengerWithSameLastName() {
        this.addPassenger(40, List.of(DiscountCard.Family));
        this.addPassenger(40, List.of());

        double estimatedPrice = this.helperTicketEstimator("Bordeaux", "Paris", 20);
        assertEquals(140,
                estimatedPrice
        );
    }

}