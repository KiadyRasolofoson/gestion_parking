package com.example.parking.entity;

import com.example.parking.entity.enums.SpaceType;
import com.example.parking.entity.enums.UserCategory;
import com.example.parking.entity.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Regle tarifaire configurable par l'administrateur.
 * Un champ null signifie "s'applique a tout". La regle active de plus forte
 * priorite qui correspond au contexte est retenue.
 */
@Entity
@Table(name = "tariff_rules", indexes = {
        @Index(name = "idx_tariff_active", columnList = "active"),
        @Index(name = "idx_tariff_priority", columnList = "priority")
})
public class TariffRule extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 30)
    private String zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "space_type", length = 20)
    private SpaceType spaceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", length = 20)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_category", length = 20)
    private UserCategory userCategory;

    @Column(name = "hourly_rate", nullable = false, precision = 12, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "minimum_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal minimumFee = BigDecimal.ZERO;

    /** Forfait journalier : plafond applique par tranche de 24h. */
    @Column(name = "daily_cap", precision = 12, scale = 2)
    private BigDecimal dailyCap;

    /** Coefficient applique aux minutes de depassement. */
    @Column(name = "overstay_multiplier", nullable = false, precision = 6, scale = 2)
    private BigDecimal overstayMultiplier = new BigDecimal("1.50");

    @Column(name = "subscriber_discount_percent", nullable = false, precision = 6, scale = 2)
    private BigDecimal subscriberDiscountPercent = BigDecimal.ZERO;

    @Column(name = "free_minutes", nullable = false)
    private int freeMinutes = 0;

    @Column(nullable = false)
    private int priority = 0;

    @Column(nullable = false)
    private boolean active = true;

    public TariffRule() {
    }

    /** Verifie si la regle correspond au contexte de facturation. */
    public boolean matches(String zoneValue, SpaceType spaceTypeValue, VehicleType vehicleTypeValue,
                          UserCategory userCategoryValue) {
        if (!active) {
            return false;
        }
        if (zone != null && !zone.equalsIgnoreCase(zoneValue)) {
            return false;
        }
        if (spaceType != null && spaceType != spaceTypeValue) {
            return false;
        }
        if (vehicleType != null && vehicleType != vehicleTypeValue) {
            return false;
        }
        return userCategory == null || userCategory == userCategoryValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TariffRule other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
