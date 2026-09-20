package com.example.parking.util;

import com.example.parking.config.ParkingProperties;
import com.example.parking.entity.TariffRule;
import com.example.parking.entity.enums.SpaceType;
import com.example.parking.entity.enums.UserCategory;
import com.example.parking.entity.enums.VehicleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.StringJoiner;

/**
 * Calcul du tarif d'une session de stationnement.
 *
 * <p>Le tarif depend de la duree, de la zone, du type de place, du type de
 * vehicule, de la categorie d'utilisateur et de l'abonnement eventuel.
 * Les minutes passees au-dela de la reservation sont facturees avec une
 * majoration.</p>
 */
@Component
public class PricingCalculator {

    private static final int SCALE = 2;
    private static final BigDecimal MINUTES_PER_HOUR = new BigDecimal("60");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final ParkingProperties properties;

    public PricingCalculator(ParkingProperties properties) {
        this.properties = properties;
    }

    /**
     * @param actualMinutes   duree reelle de stationnement
     * @param expectedMinutes duree prevue par la reservation (0 si aucune)
     * @param rule            regle tarifaire retenue (peut etre nulle)
     * @param spaceType       type de la place occupee
     * @param vehicleType     type du vehicule
     * @param category        categorie de l'utilisateur
     * @param subscriberValid vrai si un abonnement valide couvre le vehicule
     */
    public PricingResult compute(long actualMinutes,
                                 long expectedMinutes,
                                 TariffRule rule,
                                 SpaceType spaceType,
                                 VehicleType vehicleType,
                                 UserCategory category,
                                 boolean subscriberValid) {

        ParkingProperties.Pricing defaults = properties.getPricing();
        long safeActual = Math.max(0L, actualMinutes);
        long safeExpected = Math.max(0L, expectedMinutes);

        int freeMinutes = rule != null ? rule.getFreeMinutes() : defaults.getFreeMinutes();
        BigDecimal hourlyRate = rule != null ? rule.getHourlyRate() : defaults.getDefaultHourlyRate();
        BigDecimal minimumFee = rule != null ? rule.getMinimumFee() : defaults.getMinimumFee();
        BigDecimal dailyCap = rule != null ? rule.getDailyCap() : defaults.getDailyCap();
        BigDecimal overstayMultiplier = rule != null ? rule.getOverstayMultiplier() : defaults.getOverstayMultiplier();
        BigDecimal discountPercent = rule != null
                ? rule.getSubscriberDiscountPercent()
                : defaults.getSubscriberDiscountPercent();
        String tariffName = rule != null ? rule.getName() : "Tarif par defaut";

        StringJoiner details = new StringJoiner(" | ");
        details.add("Duree reelle : " + DateTimeUtils.formatDuration(safeActual));

        // 1. Periode de gratuite
        if (safeActual <= freeMinutes) {
            details.add("Duree inferieure a la franchise de " + freeMinutes + " min : gratuit");
            return new PricingResult(safeActual, 0L, safeExpected, 0L,
                    zero(), zero(), zero(), zero(), tariffName, details.toString());
        }

        // 2. Arrondi a l'increment de facturation
        long chargeable = safeActual - freeMinutes;
        int increment = Math.max(1, defaults.getBillingIncrementMinutes());
        long billedMinutes = roundUp(chargeable, increment);
        details.add("Franchise : " + freeMinutes + " min");
        details.add("Duree facturee : " + DateTimeUtils.formatDuration(billedMinutes));

        // 3. Separation duree normale / depassement de reservation
        long normalMinutes = billedMinutes;
        long overstayMinutes = 0L;
        if (safeExpected > 0 && safeActual > safeExpected) {
            long expectedChargeable = Math.max(0L, safeExpected - freeMinutes);
            normalMinutes = Math.min(billedMinutes, roundUp(expectedChargeable, increment));
            overstayMinutes = billedMinutes - normalMinutes;
            details.add("Duree prevue : " + DateTimeUtils.formatDuration(safeExpected));
            details.add("Depassement : " + DateTimeUtils.formatDuration(overstayMinutes));
        }

        // 4. Tarif a la minute, pondere par le type de vehicule
        BigDecimal vehicleCoefficient = vehicleType != null ? vehicleType.getCoefficient() : BigDecimal.ONE;
        BigDecimal ratePerMinute = hourlyRate
                .multiply(vehicleCoefficient)
                .divide(MINUTES_PER_HOUR, 6, RoundingMode.HALF_UP);
        details.add("Tarif horaire : " + hourlyRate + " x coefficient " + vehicleCoefficient);

        BigDecimal baseAmount = ratePerMinute.multiply(BigDecimal.valueOf(normalMinutes));

        // 5. Majoration place VIP
        if (spaceType == SpaceType.VIP && defaults.getVipSurchargePercent().signum() > 0) {
            BigDecimal surcharge = baseAmount.multiply(defaults.getVipSurchargePercent()).divide(HUNDRED, 6,
                    RoundingMode.HALF_UP);
            baseAmount = baseAmount.add(surcharge);
            details.add("Majoration place VIP : " + defaults.getVipSurchargePercent() + " %");
        }

        // 6. Forfait journalier (plafond par tranche de 24 h)
        if (dailyCap != null && dailyCap.signum() > 0) {
            long days = Math.max(1L, roundUp(normalMinutes, 1440) / 1440);
            BigDecimal cap = dailyCap.multiply(BigDecimal.valueOf(days));
            if (baseAmount.compareTo(cap) > 0) {
                baseAmount = cap;
                details.add("Forfait journalier applique : " + cap);
            }
        }

        // 7. Depassement majore
        BigDecimal overstayAmount = ratePerMinute
                .multiply(BigDecimal.valueOf(overstayMinutes))
                .multiply(overstayMultiplier);
        if (overstayMinutes > 0) {
            details.add("Coefficient de depassement : x" + overstayMultiplier);
        }

        BigDecimal subtotal = baseAmount.add(overstayAmount).setScale(SCALE, RoundingMode.HALF_UP);

        // 8. Tarif minimum
        if (minimumFee != null && subtotal.compareTo(minimumFee) < 0) {
            subtotal = minimumFee.setScale(SCALE, RoundingMode.HALF_UP);
            details.add("Tarif minimum applique : " + minimumFee);
        }

        // 9. Reduction abonne
        BigDecimal discountAmount = zero();
        if (subscriberValid && discountPercent != null && discountPercent.signum() > 0) {
            discountAmount = subtotal.multiply(discountPercent).divide(HUNDRED, SCALE, RoundingMode.HALF_UP);
            details.add("Reduction abonne : " + discountPercent + " %");
        } else if (category == UserCategory.ABONNE && discountPercent != null && discountPercent.signum() > 0) {
            discountAmount = subtotal.multiply(discountPercent).divide(HUNDRED, SCALE, RoundingMode.HALF_UP);
            details.add("Reduction categorie abonne : " + discountPercent + " %");
        }

        BigDecimal total = subtotal.subtract(discountAmount).max(zero()).setScale(SCALE, RoundingMode.HALF_UP);

        return new PricingResult(
                safeActual,
                billedMinutes,
                safeExpected,
                overstayMinutes,
                baseAmount.setScale(SCALE, RoundingMode.HALF_UP),
                overstayAmount.setScale(SCALE, RoundingMode.HALF_UP),
                discountAmount,
                total,
                tariffName,
                truncate(details.toString()));
    }

    private static long roundUp(long value, long increment) {
        if (value <= 0) {
            return 0L;
        }
        long blocks = (value + increment - 1) / increment;
        return blocks * increment;
    }

    private static BigDecimal zero() {
        return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private static String truncate(String value) {
        return value.length() <= 1000 ? value : value.substring(0, 1000);
    }
}
