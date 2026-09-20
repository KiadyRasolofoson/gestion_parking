package com.example.parking.mapper;

import com.example.parking.dto.response.InvoiceResponse;
import com.example.parking.entity.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    public InvoiceResponse toResponse(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getReference(),
                invoice.getSession() != null ? invoice.getSession().getId() : null,
                invoice.getSession() != null ? invoice.getSession().getReference() : null,
                invoice.getUser().getId(),
                invoice.getUser().getFullName(),
                invoice.getSession() != null ? invoice.getSession().getVehicle().getPlateNumber() : null,
                invoice.getSession() != null ? invoice.getSession().getSpace().getNumber() : null,
                invoice.getBilledMinutes(),
                invoice.getExpectedMinutes(),
                invoice.getOverstayMinutes(),
                invoice.getBaseAmount(),
                invoice.getOverstayAmount(),
                invoice.getDiscountAmount(),
                invoice.getTotalAmount(),
                invoice.getAppliedTariff(),
                invoice.getDetails(),
                invoice.getStatus(),
                invoice.getStatus() != null ? invoice.getStatus().getLabel() : null,
                invoice.getIssuedAt(),
                invoice.getPaidAt());
    }
}
