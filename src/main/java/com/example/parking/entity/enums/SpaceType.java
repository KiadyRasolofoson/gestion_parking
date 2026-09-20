package com.example.parking.entity.enums;

/** Type de place de parking. */
public enum SpaceType {

    STANDARD("Standard"),
    PMR("PMR"),
    VIP("VIP"),
    ABONNE("Abonne");

    private final String label;

    SpaceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
