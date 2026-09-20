package com.example.parking.dto.response;

import com.example.parking.entity.enums.SpaceType;
import com.example.parking.entity.enums.UserCategory;
import com.example.parking.entity.enums.VehicleType;

import java.math.BigDecimal;

public record TariffRuleResponse(
        Long id,
        String name,
        String zone,
        SpaceType spaceType,
        VehicleType vehicleType,
        UserCategory userCategory,
        BigDecimal hourlyRate,
        BigDecimal minimumFee,
        BigDecimal dailyCap,
        BigDecimal overstayMultiplier,
        BigDecimal subscriberDiscountPercent,
        int freeMinutes,
        int priority,
        boolean active) {
}
