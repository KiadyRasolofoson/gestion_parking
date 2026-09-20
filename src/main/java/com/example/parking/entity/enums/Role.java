package com.example.parking.entity.enums;

/**
 * Roles applicatifs.
 * ADMIN     : configuration (tarifs, places, utilisateurs).
 * MANAGER   : gestion metier (reservations, abonnements, facturation).
 * OPERATOR  : operations quotidiennes (entrees / sorties).
 * USER      : consultation et actions limitees (ses reservations).
 */
public enum Role {

    ADMIN("Administrateur"),
    MANAGER("Gestionnaire"),
    OPERATOR("Operateur"),
    USER("Utilisateur");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String authority() {
        return "ROLE_" + name();
    }
}
