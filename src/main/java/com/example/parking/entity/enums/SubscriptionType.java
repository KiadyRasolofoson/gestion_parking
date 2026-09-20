package com.example.parking.entity.enums;

import java.time.LocalDate;

/** Type d'abonnement et duree associee. */
public enum SubscriptionType {

    JOURNALIER("Journalier"),
    HEBDOMADAIRE("Hebdomadaire"),
    MENSUEL("Mensuel"),
    ANNUEL("Annuel");

    private final String label;

    SubscriptionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Calcule la date de fin (incluse) a partir de la date de debut. */
    public LocalDate computeEndDate(LocalDate start) {
        return switch (this) {
            case JOURNALIER -> start;
            case HEBDOMADAIRE -> start.plusWeeks(1).minusDays(1);
            case MENSUEL -> start.plusMonths(1).minusDays(1);
            case ANNUEL -> start.plusYears(1).minusDays(1);
        };
    }
}
