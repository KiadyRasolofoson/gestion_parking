package com.example.parking.mapper;

import com.example.parking.dto.request.UpdateReservationRequest;
import com.example.parking.dto.response.ReservationResponse;
import com.example.parking.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        return new ReservationResponse(
                reservation.getId(),
                reservation.getReference(),
                reservation.getUser().getId(),
                reservation.getUser().getFullName(),
                reservation.getVehicle().getId(),
                reservation.getVehicle().getPlateNumber(),
                reservation.getSpace().getId(),
                reservation.getSpace().getNumber(),
                reservation.getSpace().getZone(),
                reservation.getExpectedArrival(),
                reservation.getExpectedDeparture(),
                reservation.getExpectedDurationMinutes(),
                reservation.getStatus(),
                reservation.getStatus() != null ? reservation.getStatus().getLabel() : null,
                reservation.getNotes(),
                reservation.getCreatedAt());
    }

    public UpdateReservationRequest toUpdateRequest(Reservation reservation) {
        UpdateReservationRequest request = new UpdateReservationRequest();
        request.setSpaceId(reservation.getSpace().getId());
        request.setExpectedArrival(reservation.getExpectedArrival());
        request.setExpectedDeparture(reservation.getExpectedDeparture());
        request.setNotes(reservation.getNotes());
        return request;
    }
}
