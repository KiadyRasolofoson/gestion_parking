package com.example.parking.dto.response;

import com.example.parking.entity.enums.SpaceStatus;
import com.example.parking.entity.enums.SpaceType;

public record ParkingSpaceResponse(
        Long id,
        String number,
        String zone,
        Integer floor,
        SpaceType type,
        String typeLabel,
        SpaceStatus status,
        String statusLabel,
        String outOfServiceReason) {
}
