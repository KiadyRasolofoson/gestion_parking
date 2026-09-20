package com.example.parking.entity;

import com.example.parking.entity.enums.SubscriptionStatus;
import com.example.parking.entity.enums.SubscriptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionTest {

    @Test
    @DisplayName("Les dates de fin sont calculees selon le type d'abonnement")
    void endDatesAreComputedFromType() {
        LocalDate start = LocalDate.of(2026, 1, 10);

        assertThat(SubscriptionType.JOURNALIER.computeEndDate(start)).isEqualTo(LocalDate.of(2026, 1, 10));
        assertThat(SubscriptionType.HEBDOMADAIRE.computeEndDate(start)).isEqualTo(LocalDate.of(2026, 1, 16));
        assertThat(SubscriptionType.MENSUEL.computeEndDate(start)).isEqualTo(LocalDate.of(2026, 2, 9));
        assertThat(SubscriptionType.ANNUEL.computeEndDate(start)).isEqualTo(LocalDate.of(2027, 1, 9));
    }

    @Test
    @DisplayName("Un abonnement actif est valide a l'interieur de sa periode")
    void activeSubscriptionIsValidInsidePeriod() {
        Subscription subscription = subscription(SubscriptionStatus.ACTIF,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertThat(subscription.isValidOn(LocalDate.of(2026, 1, 1))).isTrue();
        assertThat(subscription.isValidOn(LocalDate.of(2026, 1, 31))).isTrue();
        assertThat(subscription.isValidOn(LocalDate.of(2025, 12, 31))).isFalse();
        assertThat(subscription.isValidOn(LocalDate.of(2026, 2, 1))).isFalse();
    }

    @Test
    @DisplayName("Un abonnement suspendu n'est jamais valide")
    void suspendedSubscriptionIsNeverValid() {
        Subscription subscription = subscription(SubscriptionStatus.SUSPENDU,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertThat(subscription.isValidOn(LocalDate.of(2026, 1, 15))).isFalse();
    }

    private Subscription subscription(SubscriptionStatus status, LocalDate start, LocalDate end) {
        Subscription subscription = new Subscription();
        subscription.setStatus(status);
        subscription.setStartDate(start);
        subscription.setEndDate(end);
        return subscription;
    }
}
