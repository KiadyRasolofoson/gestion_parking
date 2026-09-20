package com.example.parking.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationOverlapTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 5, 10, 10, 0);

    @Test
    @DisplayName("Deux periodes qui se croisent sont en conflit")
    void overlappingPeriodsConflict() {
        Reservation reservation = reservation(BASE, BASE.plusHours(3));

        assertThat(reservation.overlaps(BASE.plusHours(1), BASE.plusHours(2))).isTrue();
        assertThat(reservation.overlaps(BASE.minusHours(1), BASE.plusHours(1))).isTrue();
        assertThat(reservation.overlaps(BASE.plusHours(2), BASE.plusHours(5))).isTrue();
    }

    @Test
    @DisplayName("Des periodes adjacentes ne sont pas en conflit")
    void adjacentPeriodsDoNotConflict() {
        Reservation reservation = reservation(BASE, BASE.plusHours(3));

        assertThat(reservation.overlaps(BASE.plusHours(3), BASE.plusHours(5))).isFalse();
        assertThat(reservation.overlaps(BASE.minusHours(2), BASE)).isFalse();
    }

    @Test
    @DisplayName("La duree prevue est exprimee en minutes")
    void expectedDurationIsComputedInMinutes() {
        assertThat(reservation(BASE, BASE.plusMinutes(90)).getExpectedDurationMinutes()).isEqualTo(90);
    }

    private Reservation reservation(LocalDateTime arrival, LocalDateTime departure) {
        Reservation reservation = new Reservation();
        reservation.setExpectedArrival(arrival);
        reservation.setExpectedDeparture(departure);
        return reservation;
    }
}
