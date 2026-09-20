package com.example.parking.util;

import com.example.parking.config.ParkingProperties;
import com.example.parking.entity.TariffRule;
import com.example.parking.entity.enums.SpaceType;
import com.example.parking.entity.enums.UserCategory;
import com.example.parking.entity.enums.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PricingCalculatorTest {

    private PricingCalculator calculator;
    private ParkingProperties properties;

    @BeforeEach
    void setUp() {
        properties = new ParkingProperties();
        ParkingProperties.Pricing pricing = properties.getPricing();
        pricing.setDefaultHourlyRate(new BigDecimal("2000.00"));
        pricing.setMinimumFee(new BigDecimal("1000.00"));
        pricing.setDailyCap(new BigDecimal("20000.00"));
        pricing.setFreeMinutes(15);
        pricing.setBillingIncrementMinutes(15);
        pricing.setOverstayMultiplier(new BigDecimal("1.50"));
        pricing.setSubscriberDiscountPercent(new BigDecimal("20.00"));
        pricing.setVipSurchargePercent(new BigDecimal("25.00"));
        calculator = new PricingCalculator(properties);
    }

    @Test
    @DisplayName("Une duree inferieure a la franchise n'est pas facturee")
    void shortStayIsFree() {
        PricingResult result = calculator.compute(10, 0, null, SpaceType.STANDARD, VehicleType.VOITURE,
                UserCategory.STANDARD, false);

        assertThat(result.totalAmount()).isEqualByComparingTo("0.00");
        assertThat(result.billedMinutes()).isZero();
    }

    @Test
    @DisplayName("Trois heures sans reservation : 165 minutes facturees apres franchise")
    void standardStayIsBilledAfterFreeMinutes() {
        PricingResult result = calculator.compute(180, 0, null, SpaceType.STANDARD, VehicleType.VOITURE,
                UserCategory.STANDARD, false);

        // 180 - 15 = 165 minutes, deja multiple de l'increment de 15 minutes
        assertThat(result.billedMinutes()).isEqualTo(165);
        // 165 x (2000 / 60) = 5500
        assertThat(result.totalAmount()).isEqualByComparingTo("5500.00");
        assertThat(result.overstayMinutes()).isZero();
    }

    @Test
    @DisplayName("Depassement de reservation : 2 heures supplementaires majorees")
    void overstayIsChargedWithMultiplier() {
        // Reservation de 3 h, depart reel apres 5 h : 2 h de depassement
        PricingResult result = calculator.compute(300, 180, null, SpaceType.STANDARD, VehicleType.VOITURE,
                UserCategory.STANDARD, false);

        assertThat(result.billedMinutes()).isEqualTo(285);
        assertThat(result.overstayMinutes()).isEqualTo(120);
        // normal : 165 x 33.333333 = 5500 ; depassement : 120 x 33.333333 x 1.5 = 6000
        assertThat(result.baseAmount()).isEqualByComparingTo("5500.00");
        assertThat(result.overstayAmount()).isEqualByComparingTo("6000.00");
        assertThat(result.totalAmount()).isEqualByComparingTo("11500.00");
    }

    @Test
    @DisplayName("La duree est arrondie a l'increment de facturation superieur")
    void billedMinutesAreRoundedUp() {
        PricingResult result = calculator.compute(35, 0, null, SpaceType.STANDARD, VehicleType.VOITURE,
                UserCategory.STANDARD, false);

        // 35 - 15 = 20 minutes -> arrondi a 30 minutes
        assertThat(result.billedMinutes()).isEqualTo(30);
    }

    @Test
    @DisplayName("Le tarif minimum s'applique aux courtes durees facturables")
    void minimumFeeIsApplied() {
        PricingResult result = calculator.compute(20, 0, null, SpaceType.STANDARD, VehicleType.MOTO,
                UserCategory.STANDARD, false);

        // 15 minutes x (2000 x 0.60 / 60) = 300 -> releve au tarif minimum 1000
        assertThat(result.totalAmount()).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("Le coefficient du type de vehicule est applique")
    void vehicleCoefficientIsApplied() {
        PricingResult utility = calculator.compute(180, 0, null, SpaceType.STANDARD, VehicleType.UTILITAIRE,
                UserCategory.STANDARD, false);

        // 165 x (2000 x 1.40 / 60) = 7700
        assertThat(utility.totalAmount()).isEqualByComparingTo("7700.00");
    }

    @Test
    @DisplayName("Une place VIP entraine une majoration")
    void vipSurchargeIsApplied() {
        PricingResult result = calculator.compute(180, 0, null, SpaceType.VIP, VehicleType.VOITURE,
                UserCategory.VIP, false);

        // 5500 + 25 % = 6875
        assertThat(result.totalAmount()).isEqualByComparingTo("6875.00");
    }

    @Test
    @DisplayName("Un abonnement valide donne droit a la reduction abonne")
    void subscriberDiscountIsApplied() {
        PricingResult result = calculator.compute(180, 0, null, SpaceType.STANDARD, VehicleType.VOITURE,
                UserCategory.STANDARD, true);

        assertThat(result.discountAmount()).isEqualByComparingTo("1100.00");
        assertThat(result.totalAmount()).isEqualByComparingTo("4400.00");
    }

    @Test
    @DisplayName("Le forfait journalier plafonne une longue duree")
    void dailyCapLimitsLongStay() {
        // 24 h de stationnement : 1425 minutes facturees x 33.33 = 47500 -> plafonne a 20000
        PricingResult result = calculator.compute(1440, 0, null, SpaceType.STANDARD, VehicleType.VOITURE,
                UserCategory.STANDARD, false);

        assertThat(result.totalAmount()).isEqualByComparingTo("20000.00");
    }

    @Test
    @DisplayName("Une regle tarifaire remplace les valeurs par defaut")
    void tariffRuleOverridesDefaults() {
        TariffRule rule = new TariffRule();
        rule.setName("Deux-roues");
        rule.setVehicleType(VehicleType.MOTO);
        rule.setHourlyRate(new BigDecimal("1200.00"));
        rule.setMinimumFee(new BigDecimal("600.00"));
        rule.setDailyCap(new BigDecimal("8000.00"));
        rule.setOverstayMultiplier(new BigDecimal("1.30"));
        rule.setSubscriberDiscountPercent(new BigDecimal("20.00"));
        rule.setFreeMinutes(20);

        PricingResult result = calculator.compute(300, 0, rule, SpaceType.STANDARD, VehicleType.MOTO,
                UserCategory.STANDARD, false);

        // 300 - 20 = 280 -> arrondi a 285 ; 285 x (1200 x 0.60 / 60) = 3420
        assertThat(result.appliedTariff()).isEqualTo("Deux-roues");
        assertThat(result.billedMinutes()).isEqualTo(285);
        assertThat(result.totalAmount()).isEqualByComparingTo("3420.00");
    }
}
