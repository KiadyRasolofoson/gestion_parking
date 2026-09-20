package com.example.parking.dto.response;

import com.example.parking.entity.enums.SessionStatus;

import java.time.LocalDateTime;

public record ParkingSessionResponse(
        Long id,
        String reference,
        Long userId,
        String userName,
        Long vehicleId,
        String plateNumber,
        Long spaceId,
        String spaceNumber,
        String zone,
        Long reservationId,
        String reservationReference,
        Long subscriptionId,
        LocalDateTime entryTime,
        LocalDateTime exitTime,
        long durationMinutes,
        SessionStatus status,
        String statusLabel,
        String entryOperator,
        String exitOperator,
        InvoiceResponse invoice) {
}
