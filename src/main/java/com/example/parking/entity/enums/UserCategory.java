package com.example.parking.entity.enums;

/**
 * Categorie d'un utilisateur. Elle determine le type de place recherche en
 * priorite lors de l'attribution automatique.
 */
public enum UserCategory {

    STANDARD("Standard", SpaceType.STANDARD),
    PMR("PMR", SpaceType.PMR),
    VIP("VIP", SpaceType.VIP),
    ABONNE("Abonne", SpaceType.ABONNE);

    private final String label;
    private final SpaceType preferredSpaceType;

    UserCategory(String label, SpaceType preferredSpaceType) {
        this.label = label;
        this.preferredSpaceType = preferredSpaceType;
    }

    public String getLabel() {
        return label;
    }

    public SpaceType getPreferredSpaceType() {
        return preferredSpaceType;
    }
}
