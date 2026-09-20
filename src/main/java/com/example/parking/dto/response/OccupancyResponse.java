package com.example.parking.dto.response;

import java.util.List;
import java.util.Map;

/** Statistiques d'occupation du parking. */
public record OccupancyResponse(
        long totalSpaces,
        long freeSpaces,
        long reservedSpaces,
        long occupiedSpaces,
        long outOfServiceSpaces,
        double occupancyRate,
        Map<String, Long> countByType,
        Map<String, Long> countByStatus,
        List<ZoneOccupancy> zones) {

    /** Occupation detaillee par zone. */
    public record ZoneOccupancy(String zone, long total, long free, long occupied, double occupancyRate) {
    }
}
