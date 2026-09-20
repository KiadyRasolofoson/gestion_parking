package com.example.parking.config;

import com.example.parking.entity.enums.SpaceType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parametres metier configurables (prefixe {@code parking} dans application.yml).
 * Tarification, regles d'attribution et bornes de reservation.
 */
@ConfigurationProperties(prefix = "parking")
public class ParkingProperties {

    private String name = "Parking";
    private boolean seedDemoData = true;
    private Pricing pricing = new Pricing();
    private Reservation reservation = new Reservation();
    private Allocation allocation = new Allocation();
    private SubscriptionPricing subscription = new SubscriptionPricing();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSeedDemoData() {
        return seedDemoData;
    }

    public void setSeedDemoData(boolean seedDemoData) {
        this.seedDemoData = seedDemoData;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Allocation getAllocation() {
        return allocation;
    }

    public void setAllocation(Allocation allocation) {
        this.allocation = allocation;
    }

    public SubscriptionPricing getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionPricing subscription) {
        this.subscription = subscription;
    }

    /** Parametres de tarification par defaut (utilises si aucune regle ne correspond). */
    public static class Pricing {

        private String currency = "MGA";
        private BigDecimal defaultHourlyRate = new BigDecimal("2000.00");
        private BigDecimal minimumFee = new BigDecimal("1000.00");
        private BigDecimal dailyCap = new BigDecimal("20000.00");
        private int freeMinutes = 15;
        private int billingIncrementMinutes = 15;
        private BigDecimal overstayMultiplier = new BigDecimal("1.50");
        private BigDecimal subscriberDiscountPercent = new BigDecimal("20.00");
        private BigDecimal vipSurchargePercent = new BigDecimal("25.00");

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public BigDecimal getDefaultHourlyRate() {
            return defaultHourlyRate;
        }

        public void setDefaultHourlyRate(BigDecimal defaultHourlyRate) {
            this.defaultHourlyRate = defaultHourlyRate;
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

        public int getFreeMinutes() {
            return freeMinutes;
        }

        public void setFreeMinutes(int freeMinutes) {
            this.freeMinutes = freeMinutes;
        }

        public int getBillingIncrementMinutes() {
            return billingIncrementMinutes;
        }

        public void setBillingIncrementMinutes(int billingIncrementMinutes) {
            this.billingIncrementMinutes = billingIncrementMinutes;
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

        public BigDecimal getVipSurchargePercent() {
            return vipSurchargePercent;
        }

        public void setVipSurchargePercent(BigDecimal vipSurchargePercent) {
            this.vipSurchargePercent = vipSurchargePercent;
        }
    }

    /** Bornes appliquees aux reservations. */
    public static class Reservation {

        private int maxDurationHours = 48;
        private int minDurationMinutes = 30;
        private int gracePeriodMinutes = 30;

        public int getMaxDurationHours() {
            return maxDurationHours;
        }

        public void setMaxDurationHours(int maxDurationHours) {
            this.maxDurationHours = maxDurationHours;
        }

        public int getMinDurationMinutes() {
            return minDurationMinutes;
        }

        public void setMinDurationMinutes(int minDurationMinutes) {
            this.minDurationMinutes = minDurationMinutes;
        }

        public int getGracePeriodMinutes() {
            return gracePeriodMinutes;
        }

        public void setGracePeriodMinutes(int gracePeriodMinutes) {
            this.gracePeriodMinutes = gracePeriodMinutes;
        }
    }

    /** Regles de substitution pour l'attribution automatique des places. */
    public static class Allocation {

        private Map<SpaceType, List<SpaceType>> substitution = new LinkedHashMap<>();

        public Map<SpaceType, List<SpaceType>> getSubstitution() {
            return substitution;
        }

        public void setSubstitution(Map<SpaceType, List<SpaceType>> substitution) {
            this.substitution = substitution;
        }

        /**
         * Ordre de preference pour un type demande. Retourne une valeur par
         * defaut sensee si aucune regle n'est configuree.
         */
        public List<SpaceType> preferenceFor(SpaceType requested) {
            List<SpaceType> configured = substitution.get(requested);
            if (configured != null && !configured.isEmpty()) {
                return configured;
            }
            return defaults().get(requested);
        }

        private Map<SpaceType, List<SpaceType>> defaults() {
            Map<SpaceType, List<SpaceType>> defaults = new EnumMap<>(SpaceType.class);
            defaults.put(SpaceType.VIP, List.of(SpaceType.VIP, SpaceType.ABONNE, SpaceType.STANDARD));
            defaults.put(SpaceType.PMR, List.of(SpaceType.PMR, SpaceType.STANDARD));
            defaults.put(SpaceType.ABONNE, List.of(SpaceType.ABONNE, SpaceType.STANDARD));
            defaults.put(SpaceType.STANDARD, List.of(SpaceType.STANDARD));
            return defaults;
        }
    }

    /** Catalogue de prix des abonnements. */
    public static class SubscriptionPricing {

        private BigDecimal dailyPrice = new BigDecimal("15000.00");
        private BigDecimal weeklyPrice = new BigDecimal("80000.00");
        private BigDecimal monthlyPrice = new BigDecimal("250000.00");
        private BigDecimal yearlyPrice = new BigDecimal("2400000.00");

        public BigDecimal getDailyPrice() {
            return dailyPrice;
        }

        public void setDailyPrice(BigDecimal dailyPrice) {
            this.dailyPrice = dailyPrice;
        }

        public BigDecimal getWeeklyPrice() {
            return weeklyPrice;
        }

        public void setWeeklyPrice(BigDecimal weeklyPrice) {
            this.weeklyPrice = weeklyPrice;
        }

        public BigDecimal getMonthlyPrice() {
            return monthlyPrice;
        }

        public void setMonthlyPrice(BigDecimal monthlyPrice) {
            this.monthlyPrice = monthlyPrice;
        }

        public BigDecimal getYearlyPrice() {
            return yearlyPrice;
        }

        public void setYearlyPrice(BigDecimal yearlyPrice) {
            this.yearlyPrice = yearlyPrice;
        }
    }
}
