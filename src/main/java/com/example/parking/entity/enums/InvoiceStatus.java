package com.example.parking.entity.enums;

/** Statut d'une facture. */
public enum InvoiceStatus {

    EN_ATTENTE("En attente"),
    PAYEE("Payee"),
    ANNULEE("Annulee");

    private final String label;

    InvoiceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
