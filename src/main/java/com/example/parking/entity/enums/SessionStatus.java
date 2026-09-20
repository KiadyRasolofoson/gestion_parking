package com.example.parking.entity.enums;

/** Statut d'une session de stationnement (entree / sortie). */
public enum SessionStatus {

    EN_COURS("En cours"),
    TERMINEE("Terminee");

    private final String label;

    SessionStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
