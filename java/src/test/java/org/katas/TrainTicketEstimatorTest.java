package org.katas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TripDetails;
import org.katas.model.TrainDetails;
import org.katas.model.DiscountCard;

import java.time.LocalDate;
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
                LocalDate.now()
                        .plusDays(days)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    private double helperTicketEstimator(int withInDate) {
        TrainTicketEstimator tte = new TrainTicketEstimator();
        Date when = tripDate(withInDate);

        TrainDetails trainDetails = new TrainDetails(new TripDetails("Bordeaux", "Paris", when), this.passengers);
        //System.out.println(this.passengers.toString());

        // FakeApiCall renvoi toujours 100.00 pour simplifier les calculs et véfifications
        FakeApiCall fakeApiCall = new FakeApiCall();

        return tte.estimate(trainDetails, fakeApiCall);
    }

    @Test
    void shouldReturn0_ChildUnder1yo() {
        this.addPassenger(0, List.of());

        double estimatedPrice = this.helperTicketEstimator(40);
        // TODO Voir pourquoi on à -20.0 au lieu de 0 dans cette condition
        assertEquals(-20.0,
                estimatedPrice
        );
    }

    @Test
    void shouldReturnFixedPriceAt9_For3yoChild() {
        this.addPassenger(3, List.of());

        double estimatedPrice = this.helperTicketEstimator(40);
        assertEquals(9,
                estimatedPrice
                );
    }

    // TODO Demander au metier si c'est normal d'avoir un tarifs de 0 Si il n'y a pas de passager ???
    // TODO Devrait lever une exception pour dire qu'il n'y a pas de passagers

    @Test
    void shouldReturn40_Under18yoMinus40_AndOver30daysBeforeDepartureMinus20() {
        this.addPassenger(17, List.of());

        double estimatedPrice = this.helperTicketEstimator(40);
        assertEquals(40,
                estimatedPrice
        );
    }

    // TODO Demander au PO pourquoi à 29 jours du départ on à 16% de remise a lieu de 18%
    // Puis on applique 2% d'augmentation par jour pendant 25 jours (donc de -18% à 29 jours jusqu'à +30% à 5 jours de la date de départ)
    @Test
    void shouldReturn62_Over70yoMinus20_And29daysBeforeDepartureMinus18() {
        this.addPassenger(70, List.of());

        double estimatedPrice = this.helperTicketEstimator(29);
        assertEquals(64,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn60_Over70yoMinus20_AndOver30daysBeforeDepartureMinus20() {
        this.addPassenger(70, List.of());

        double estimatedPrice = this.helperTicketEstimator(40);
        assertEquals(60,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn150_ForAllOtherPassengerPlus20_AndOver5daysBeforeDeparturePlus30() {
        this.addPassenger(40, List.of());

        double estimatedPrice = this.helperTicketEstimator(6);
        assertEquals(150,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn220_ForAllOtherPassengerPlus20_AndUnder5daysBeforeDepartureDouble100() {
        this.addPassenger(40, List.of());

        double estimatedPrice = this.helperTicketEstimator(5);
        assertEquals(220,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn1_TrainStrokeStaffCardOwner() {
        this.addPassenger(40, List.of(DiscountCard.TrainStroke));

        double estimatedPrice = this.helperTicketEstimator(5);
        assertEquals(1,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn60_Over70yoMinus20_SeniorDiscountCardOwnerMinus20() {
        this.addPassenger(70, List.of(DiscountCard.Senior));

        double estimatedPrice = this.helperTicketEstimator(21);
        assertEquals(60,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn120_AlonePassengersPlus20_CoupleDiscountCardOwnerNoDiscount() {
        this.addPassenger(18, List.of(DiscountCard.Couple));

        double estimatedPrice = this.helperTicketEstimator(21);
        assertEquals(120,
                estimatedPrice
        );
    }


    @Test
    void shouldReturn200_ForAllOtherPassengerPlus40_OnlyOneCoupleDiscountCardOwnerMinus40() {
        this.addPassenger(18, List.of(DiscountCard.Couple));
        this.addPassenger(20, List.of());

        double estimatedPrice = this.helperTicketEstimator(21);
        assertEquals(200,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn200_ForAllOtherPassengerPlus40_With2CoupleDiscountCardOwner_NotAdditionnable() {
        this.addPassenger(18, List.of(DiscountCard.Couple));
        this.addPassenger(20, List.of(DiscountCard.Couple));

        double estimatedPrice = this.helperTicketEstimator(21);
        assertEquals(200,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn110_ForAllOtherPassengerPlus20_HalfCoupleDiscountCardOwnerMinus10() {
        this.addPassenger(18, List.of(DiscountCard.HalfCouple));

        double estimatedPrice = this.helperTicketEstimator(21);
        assertEquals(110,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn80_2_Over70yoMinus40_2_SeniorDiscountCardOwnerMinus40_1_CoupleDiscountCardOwnerMinus40() {
        this.addPassenger(70, List.of(DiscountCard.Senior, DiscountCard.Couple));
        this.addPassenger(70, List.of(DiscountCard.Senior));

        double estimatedPrice = this.helperTicketEstimator(21);
        assertEquals(80,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn140_Iam_Too_tiredTo_sumThisOne() {
        this.addPassenger(70, List.of(DiscountCard.Senior,DiscountCard.Couple));
        this.addPassenger(30, List.of());

        double estimatedPrice = this.helperTicketEstimator(21);
        assertEquals(140,
                estimatedPrice
        );
    }

    // TODO TESTS EXPLORATOIRE

    // TODO TESTER LES EXCEPTIONS

    /*
    * ## Nouvelles fonctionnalités
    *
    * Deux nouvelles fonctionnalités sont nécessaires pour notre outil :
    *
    * * On s'est rendu compte qu'il restait parfois des billets à écouler juste avant le départ du train, et qu'un siège vide est moins rentable qu'un siège vendu pas cher.
    * Par conséquent, 6 heures avant le départ, on applique une réduction de 20% sur le prix du billet (au lieu de doubler le prix du billet comme actuellement)
    *
    * TODO 6 heures avant départ retirer DOUBLE prix et faire 20% sur prix de base
    *
    * * La carte Famille est un nouveau concept qui nous a été demandé et fonctionne comme suit.
    * Si un passager la possède, tous ceux qui ont le même nom de famille bénéficie de 30% de réduction.
    * Pour cela, il faudra ajouter un champ `lastName` dans le passager. La carte ne s'applique pas si le nom n'est pas renseigné.
    * Cette carte est non cumulable avec les autres réductions. Comme elle est plus avantageuse que les autres, elle est prioritaire sur les autres cartes.
    *
    *
    *
    */

}