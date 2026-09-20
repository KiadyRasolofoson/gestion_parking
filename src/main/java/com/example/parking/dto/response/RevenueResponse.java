package com.example.parking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Chiffre d'affaires sur une periode. */
public record RevenueResponse(
        LocalDate from,
        LocalDate to,
        String currency,
        BigDecimal totalRevenue,
        BigDecimal parkingRevenue,
        BigDecimal overstayRevenue,
        BigDecimal discountGranted,
        long invoiceCount,
        BigDecimal averageTicket,
        List<DailyRevenue> daily) {

    public record DailyRevenue(LocalDate date, long invoiceCount, BigDecimal amount) {
    }
}
