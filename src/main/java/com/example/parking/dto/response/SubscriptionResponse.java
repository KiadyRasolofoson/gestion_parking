package com.example.parking.dto.response;

import com.example.parking.entity.enums.SubscriptionStatus;
import com.example.parking.entity.enums.SubscriptionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SubscriptionResponse(
        Long id,
        String reference,
        Long userId,
        String userName,
        Long vehicleId,
        String plateNumber,
        SubscriptionType type,
        String typeLabel,
        LocalDate startDate,
        LocalDate endDate,
        SubscriptionStatus status,
        String statusLabel,
        BigDecimal price,
        boolean valid,
        long remainingDays) {
}
