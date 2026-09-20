package com.example.parking.dto.response;

import com.example.parking.entity.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceResponse(
        Long id,
        String reference,
        Long sessionId,
        String sessionReference,
        Long userId,
        String userName,
        String plateNumber,
        String spaceNumber,
        long billedMinutes,
        long expectedMinutes,
        long overstayMinutes,
        BigDecimal baseAmount,
        BigDecimal overstayAmount,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        String appliedTariff,
        String details,
        InvoiceStatus status,
        String statusLabel,
        LocalDateTime issuedAt,
        LocalDateTime paidAt) {
}
