package com.example.parking.mapper;

import com.example.parking.dto.request.TariffRuleRequest;
import com.example.parking.dto.response.TariffRuleResponse;
import com.example.parking.entity.TariffRule;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TariffRuleMapper {

    public TariffRuleResponse toResponse(TariffRule rule) {
        if (rule == null) {
            return null;
        }
        return new TariffRuleResponse(
                rule.getId(),
                rule.getName(),
                rule.getZone(),
                rule.getSpaceType(),
                rule.getVehicleType(),
                rule.getUserCategory(),
                rule.getHourlyRate(),
                rule.getMinimumFee(),
                rule.getDailyCap(),
                rule.getOverstayMultiplier(),
                rule.getSubscriberDiscountPercent(),
                rule.getFreeMinutes(),
                rule.getPriority(),
                rule.isActive());
    }

    public TariffRule toEntity(TariffRuleRequest request) {
        TariffRule rule = new TariffRule();
        applyTo(rule, request);
        return rule;
    }

    public void applyTo(TariffRule rule, TariffRuleRequest request) {
        rule.setName(request.getName().trim());
        rule.setZone(StringUtils.hasText(request.getZone()) ? request.getZone().trim().toUpperCase() : null);
        rule.setSpaceType(request.getSpaceType());
        rule.setVehicleType(request.getVehicleType());
        rule.setUserCategory(request.getUserCategory());
        rule.setHourlyRate(request.getHourlyRate());
        rule.setMinimumFee(request.getMinimumFee());
        rule.setDailyCap(request.getDailyCap());
        rule.setOverstayMultiplier(request.getOverstayMultiplier());
        rule.setSubscriberDiscountPercent(request.getSubscriberDiscountPercent());
        rule.setFreeMinutes(request.getFreeMinutes());
        rule.setPriority(request.getPriority());
        rule.setActive(request.isActive());
    }

    public TariffRuleRequest toRequest(TariffRule rule) {
        TariffRuleRequest request = new TariffRuleRequest();
        request.setName(rule.getName());
        request.setZone(rule.getZone());
        request.setSpaceType(rule.getSpaceType());
        request.setVehicleType(rule.getVehicleType());
        request.setUserCategory(rule.getUserCategory());
        request.setHourlyRate(rule.getHourlyRate());
        request.setMinimumFee(rule.getMinimumFee());
        request.setDailyCap(rule.getDailyCap());
        request.setOverstayMultiplier(rule.getOverstayMultiplier());
        request.setSubscriberDiscountPercent(rule.getSubscriberDiscountPercent());
        request.setFreeMinutes(rule.getFreeMinutes());
        request.setPriority(rule.getPriority());
        request.setActive(rule.isActive());
        return request;
    }
}
