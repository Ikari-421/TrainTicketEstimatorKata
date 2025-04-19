package org.katas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.katas.TrainTicketEstimator;
import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TripDetails;
import org.katas.model.TripRequest;

import java.time.LocalDate;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class TrainTicketEstimatorTest {

    public Date tripDate(int days) {
        return Date.from(
                LocalDate.now()
                        .plusDays(days)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    @Test
    void should_NotWork() {
        assertEquals(4, 2+2);
    }

    /*
    * ### Typologie de passager
    * Si le passager est un enfant, alors :
    * S'il a moins d'un an à la date du voyage, c'est gratuit (en même temps, il n'aura pas de siège attribué)
    * S'il a 3 ans ou moins, c'est un tarif fixe de 9 euros
    * Jusqu'à 18 ans, il a 40% de réduction par rapport au tarif de base.
    * Si le passager est un senior (>= 70ans), alors il bénéficie de 20% de réduction
    * Dans tous les autres cas, c'est +20% (Hé quoi, il faut bien qu'on fasse du profit !)
    */


    @Test
    void should_returnFloat() {

        TrainTicketEstimator tte = new TrainTicketEstimator();
        Passenger passenger1 = new Passenger(10, List.of(DiscountCard.HalfCouple));
        List<Passenger> passengers = new ArrayList<>();
        Date when = tripDate(20);
        passengers.add(passenger1);
        TripRequest tripRequest = new TripRequest(new TripDetails("Tokyo", "Paris", when), passengers);

        double estimatedPrice = tte.estimate(tripRequest);

        assertEquals(4,
                estimatedPrice
                );
    }

    /*
    * ### Date du voyage
    * On calcule la durée entre aujourd'hui et le départ et applique les modificateurs suivants :
    *
    * * 30 jours avant le voyage, on applique -20% de réduction.
    * * Puis on applique 2% d'augmentation par jour pendant 25 jours (donc de -18% à 29 jours jusqu'à +30% à 5 jours de la date de départ)
    * * À moins de 5 jours du voyage, le tarif du billet double.
    *
    * Ces règles ne s'appliquent pas sur les billets à prix fixe.
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