package com.example.parking.util;

import java.math.BigDecimal;

/** Resultat detaille d'un calcul de tarif. */
public record PricingResult(
        long actualMinutes,
        long billedMinutes,
        long expectedMinutes,
        long overstayMinutes,
        BigDecimal baseAmount,
        BigDecimal overstayAmount,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        String appliedTariff,
        String details) {
}
