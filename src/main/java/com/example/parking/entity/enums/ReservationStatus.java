package com.example.parking.entity.enums;

import java.util.EnumSet;
import java.util.Set;

/** Statut d'une reservation. */
public enum ReservationStatus {

    CONFIRMEE("Confirmee"),
    EN_COURS("En cours"),
    TERMINEE("Terminee"),
    ANNULEE("Annulee"),
    EXPIREE("Expiree");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Statuts qui bloquent encore la place (utilises pour la detection de conflit). */
    public static Set<ReservationStatus> activeStatuses() {
        return EnumSet.of(CONFIRMEE, EN_COURS);
    }

    public boolean isFinal() {
        return this == TERMINEE || this == ANNULEE || this == EXPIREE;
    }
}
