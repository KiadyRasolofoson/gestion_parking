package com.example.parking.config;

import com.example.parking.entity.enums.InvoiceStatus;
import com.example.parking.entity.enums.ReservationStatus;
import com.example.parking.entity.enums.Role;
import com.example.parking.entity.enums.SessionStatus;
import com.example.parking.entity.enums.SpaceStatus;
import com.example.parking.entity.enums.SpaceType;
import com.example.parking.entity.enums.SubscriptionStatus;
import com.example.parking.entity.enums.SubscriptionType;
import com.example.parking.entity.enums.UserCategory;
import com.example.parking.entity.enums.VehicleType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Donnees communes injectees dans tous les modeles Thymeleaf. */
@ControllerAdvice(basePackages = "com.example.parking.controller.web")
public class GlobalModelAttributes {

    private final ParkingProperties properties;

    public GlobalModelAttributes(ParkingProperties properties) {
        this.properties = properties;
    }

    @ModelAttribute("parkingName")
    public String parkingName() {
        return properties.getName();
    }

    @ModelAttribute("currency")
    public String currency() {
        return properties.getPricing().getCurrency();
    }

    @ModelAttribute("spaceTypes")
    public SpaceType[] spaceTypes() {
        return SpaceType.values();
    }

    @ModelAttribute("spaceStatuses")
    public SpaceStatus[] spaceStatuses() {
        return SpaceStatus.values();
    }

    @ModelAttribute("vehicleTypes")
    public VehicleType[] vehicleTypes() {
        return VehicleType.values();
    }

    @ModelAttribute("reservationStatuses")
    public ReservationStatus[] reservationStatuses() {
        return ReservationStatus.values();
    }

    @ModelAttribute("subscriptionTypes")
    public SubscriptionType[] subscriptionTypes() {
        return SubscriptionType.values();
    }

    @ModelAttribute("subscriptionStatuses")
    public SubscriptionStatus[] subscriptionStatuses() {
        return SubscriptionStatus.values();
    }

    @ModelAttribute("sessionStatuses")
    public SessionStatus[] sessionStatuses() {
        return SessionStatus.values();
    }

    @ModelAttribute("invoiceStatuses")
    public InvoiceStatus[] invoiceStatuses() {
        return InvoiceStatus.values();
    }

    @ModelAttribute("roles")
    public Role[] roles() {
        return Role.values();
    }

    @ModelAttribute("userCategories")
    public UserCategory[] userCategories() {
        return UserCategory.values();
    }
}
