package com.example.parking.dto.response;

import com.example.parking.entity.enums.VehicleType;

public record VehicleResponse(
        Long id,
        String plateNumber,
        String brand,
        String model,
        String color,
        VehicleType type,
        String typeLabel,
        Long ownerId,
        String ownerName) {
}
