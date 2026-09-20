package com.example.parking.dto.request;

import com.example.parking.entity.enums.SpaceType;
import com.example.parking.entity.enums.UserCategory;
import com.example.parking.entity.enums.VehicleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class TariffRuleRequest {

    @NotBlank(message = "Le nom de la regle est obligatoire")
    @Size(max = 120)
    private String name;

    @Size(max = 30)
    private String zone;

    private SpaceType spaceType;

    private VehicleType vehicleType;

    private UserCategory userCategory;

    @NotNull(message = "Le tarif horaire est obligatoire")
    @Positive(message = "Le tarif horaire doit etre strictement positif")
    private BigDecimal hourlyRate;

    @NotNull(message = "Le tarif minimum est obligatoire")
    @PositiveOrZero(message = "Le tarif minimum doit etre positif ou nul")
    private BigDecimal minimumFee = BigDecimal.ZERO;

    @PositiveOrZero(message = "Le forfait journalier doit etre positif ou nul")
    private BigDecimal dailyCap;

    @NotNull(message = "Le coefficient de depassement est obligatoire")
    @DecimalMin(value = "1.0", message = "Le coefficient de depassement doit etre >= 1")
    private BigDecimal overstayMultiplier = new BigDecimal("1.50");

    @NotNull(message = "La reduction abonne est obligatoire")
    @PositiveOrZero(message = "La reduction doit etre positive ou nulle")
    @DecimalMin(value = "0.0")
    private BigDecimal subscriberDiscountPercent = BigDecimal.ZERO;

    @PositiveOrZero(message = "Les minutes gratuites doivent etre positives ou nulles")
    private int freeMinutes = 0;

    @PositiveOrZero(message = "La priorite doit etre positive ou nulle")
    private int priority = 0;

    private boolean active = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public SpaceType getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(SpaceType spaceType) {
        this.spaceType = spaceType;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public UserCategory getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(UserCategory userCategory) {
        this.userCategory = userCategory;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getMinimumFee() {
        return minimumFee;
    }

    public void setMinimumFee(BigDecimal minimumFee) {
        this.minimumFee = minimumFee;
    }

    public BigDecimal getDailyCap() {
        return dailyCap;
    }

    public void setDailyCap(BigDecimal dailyCap) {
        this.dailyCap = dailyCap;
    }

    public BigDecimal getOverstayMultiplier() {
        return overstayMultiplier;
    }

    public void setOverstayMultiplier(BigDecimal overstayMultiplier) {
        this.overstayMultiplier = overstayMultiplier;
    }

    public BigDecimal getSubscriberDiscountPercent() {
        return subscriberDiscountPercent;
    }

    public void setSubscriberDiscountPercent(BigDecimal subscriberDiscountPercent) {
        this.subscriberDiscountPercent = subscriberDiscountPercent;
    }

    public int getFreeMinutes() {
        return freeMinutes;
    }

    public void setFreeMinutes(int freeMinutes) {
        this.freeMinutes = freeMinutes;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
