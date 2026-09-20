package com.example.parking.dto.response;

import java.math.BigDecimal;

/** Indicateurs affiches sur le tableau de bord. */
public record DashboardResponse(
        long totalSpaces,
        long freeSpaces,
        long occupiedSpaces,
        long reservedSpaces,
        long outOfServiceSpaces,
        double occupancyRate,
        long ongoingSessions,
        long entriesToday,
        long exitsToday,
        long activeReservations,
        long activeSubscriptions,
        BigDecimal revenueToday,
        BigDecimal revenueThisMonth,
        String currency) {
}
