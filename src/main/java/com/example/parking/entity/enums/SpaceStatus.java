package com.example.parking.entity.enums;

/** Statut d'une place de parking. */
public enum SpaceStatus {

    LIBRE("Libre"),
    RESERVEE("Reservee"),
    OCCUPEE("Occupee"),
    HORS_SERVICE("Hors service");

    private final String label;

    SpaceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
