package org.katas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TripDetails;
import org.katas.model.TrainDetails;

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
        System.out.println(this.passengers.toString());

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

    /*
     * Dans tous les autres cas, c'est +20% (Hé quoi, il faut bien qu'on fasse du profit !)
     */

    @Test
    void shouldReturn40percentDiscount_ForPassengerUnder18yo_AndOver30daysBeforeDeparture() {
        this.addPassenger(17, List.of());

        double estimatedPrice = this.helperTicketEstimator(40);
        assertEquals(40,
                estimatedPrice
        );
    }


    // TODO Demander au PO pourquoi on a pas de d'augmentation du prix du billet on a 82 au lieu de 100
    // Puis on applique 2% d'augmentation par jour pendant 25 jours (donc de -18% à 29 jours jusqu'à +30% à 5 jours de la date de départ)
    @Test
    void shouldReturn20percentDiscount_ForPassengerOver70yo_And20percentIncreaseFor20daysBeforeDeparture() {
        this.addPassenger(70, List.of());

        double estimatedPrice = this.helperTicketEstimator(20);
        assertEquals(82,
                estimatedPrice
        );
    }

    @Test
    void shouldReturn20percentIncrease_ForAllOtherPassenger_AndOver30daysBeforeDeparture() {
        this.addPassenger(40, List.of());

        double estimatedPrice = this.helperTicketEstimator(40);
        assertEquals(100,
                estimatedPrice
        );
    }



    /*
    * * Puis on applique 2% d'augmentation par jour pendant 25 jours (donc de -18% à 29 jours jusqu'à +30% à 5 jours de la date de départ)
    * * À moins de 5 jours du voyage, le tarif du billet double.
    */

    /*
    * ### Cartes de réduction
    *
    * Les usagers peuvent posséder des cartes de réduction :
    * * Carte `TrainStroke staff` : tous les billets sont à 1 euro
    * * Carte `Senior` : valable uniquement si l'utilisateur a plus de 70 ans. 20% de réduction supplémentaire
    * * Carte `Couple`: valable uniquement si le voyage concerne 2 passagers majeurs. 20% de réduction sur le billet de chacun de ces passagers. Valable une seule fois !
    * * Carte `Mi-couple` : valable uniquement si le voyage concerne 1 passager majeur. 10% de réduction sur le voyage.
    *
    * Les cartes de réduction sont cumulables si elles sont compatibles (sauf `TrainStroke Staff`). Ainsi un couple de séniors a 40% de réduction sur ses billets (plus 20% parce qu'ils sont seniors... à ce prix c'est cadeau).
    */

    /*
    * ## Nouvelles fonctionnalités
    *
    * Deux nouvelles fonctionnalités sont nécessaires pour notre outil :
    * * On s'est rendu compte qu'il restait parfois des billets à écouler juste avant le départ du train, et qu'un siège vide est moins rentable qu'un siège vendu pas cher.
    * Par conséquent, 6 heures avant le départ, on applique une réduction de 20% sur le prix du billet (au lieu de doubler le prix du billet comme actuellement)
    * * La carte Famille est un nouveau concept qui nous a été demandé et fonctionne comme suit. Si un passager la possède, tous ceux qui ont le même nom de famille bénéficie de 30% de réduction.
    * Pour cela, il faudra ajouter un champ `lastName` dans le passager. La carte ne s'applique pas si le nom n'est pas renseigné.
    * Cette carte est non cumulable avec les autres réductions. Comme elle est plus avantageuse que les autres, elle est prioritaire sur les autres cartes.
    */
}