package com.example.parking.dto.response;

import com.example.parking.entity.enums.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        String reference,
        Long userId,
        String userName,
        Long vehicleId,
        String plateNumber,
        Long spaceId,
        String spaceNumber,
        String zone,
        LocalDateTime expectedArrival,
        LocalDateTime expectedDeparture,
        long expectedDurationMinutes,
        ReservationStatus status,
        String statusLabel,
        String notes,
        LocalDateTime createdAt) {
}
