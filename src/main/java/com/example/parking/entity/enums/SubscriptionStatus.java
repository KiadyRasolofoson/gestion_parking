package com.example.parking.entity.enums;

/** Statut d'un abonnement. */
public enum SubscriptionStatus {

    ACTIF("Actif"),
    EXPIRE("Expire"),
    SUSPENDU("Suspendu"),
    ANNULE("Annule");

    private final String label;

    SubscriptionStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
