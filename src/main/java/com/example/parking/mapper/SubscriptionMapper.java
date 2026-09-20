package com.example.parking.mapper;

import com.example.parking.dto.response.SubscriptionResponse;
import com.example.parking.entity.Subscription;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class SubscriptionMapper {

    public SubscriptionResponse toResponse(Subscription subscription) {
        if (subscription == null) {
            return null;
        }
        LocalDate today = LocalDate.now();
        long remainingDays = subscription.getEndDate() != null
                ? Math.max(0L, ChronoUnit.DAYS.between(today, subscription.getEndDate()))
                : 0L;
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getReference(),
                subscription.getUser().getId(),
                subscription.getUser().getFullName(),
                subscription.getVehicle().getId(),
                subscription.getVehicle().getPlateNumber(),
                subscription.getType(),
                subscription.getType() != null ? subscription.getType().getLabel() : null,
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getStatus(),
                subscription.getStatus() != null ? subscription.getStatus().getLabel() : null,
                subscription.getPrice(),
                subscription.isValidOn(today),
                remainingDays);
    }
}
