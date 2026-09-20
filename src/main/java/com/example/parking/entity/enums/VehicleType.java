package com.example.parking.entity.enums;

import java.math.BigDecimal;

/**
 * Type de vehicule. Le coefficient influence directement le tarif applique
 * lors du calcul de la facture.
 */
public enum VehicleType {

    MOTO("Moto", new BigDecimal("0.60")),
    VOITURE("Voiture", new BigDecimal("1.00")),
    UTILITAIRE("Utilitaire", new BigDecimal("1.40")),
    CAMION("Camion", new BigDecimal("2.00")),
    ELECTRIQUE("Electrique", new BigDecimal("0.90"));

    private final String label;
    private final BigDecimal coefficient;

    VehicleType(String label, BigDecimal coefficient) {
        this.label = label;
        this.coefficient = coefficient;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }
}
