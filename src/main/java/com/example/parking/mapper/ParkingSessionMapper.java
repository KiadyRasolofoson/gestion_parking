package com.example.parking.mapper;

import com.example.parking.dto.response.InvoiceResponse;
import com.example.parking.dto.response.ParkingSessionResponse;
import com.example.parking.entity.Invoice;
import com.example.parking.entity.ParkingSession;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ParkingSessionMapper {

    private final InvoiceMapper invoiceMapper;

    public ParkingSessionMapper(InvoiceMapper invoiceMapper) {
        this.invoiceMapper = invoiceMapper;
    }

    public ParkingSessionResponse toResponse(ParkingSession session) {
        return toResponse(session, null);
    }

    public ParkingSessionResponse toResponse(ParkingSession session, Invoice invoice) {
        if (session == null) {
            return null;
        }
        InvoiceResponse invoiceResponse = invoice != null ? invoiceMapper.toResponse(invoice) : null;
        return new ParkingSessionResponse(
                session.getId(),
                session.getReference(),
                session.getUser().getId(),
                session.getUser().getFullName(),
                session.getVehicle().getId(),
                session.getVehicle().getPlateNumber(),
                session.getSpace().getId(),
                session.getSpace().getNumber(),
                session.getSpace().getZone(),
                session.getReservation() != null ? session.getReservation().getId() : null,
                session.getReservation() != null ? session.getReservation().getReference() : null,
                session.getSubscription() != null ? session.getSubscription().getId() : null,
                session.getEntryTime(),
                session.getExitTime(),
                session.getElapsedMinutes(LocalDateTime.now()),
                session.getStatus(),
                session.getStatus() != null ? session.getStatus().getLabel() : null,
                session.getEntryOperator(),
                session.getExitOperator(),
                invoiceResponse);
    }
}
