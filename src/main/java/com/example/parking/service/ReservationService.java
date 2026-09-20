package com.example.parking.service;

import com.example.parking.dto.request.CreateReservationRequest;
import com.example.parking.dto.request.UpdateReservationRequest;
import com.example.parking.dto.response.ReservationResponse;
import com.example.parking.entity.Reservation;
import com.example.parking.entity.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ReservationService {

    Page<ReservationResponse> search(String term, ReservationStatus status, Long userId,
                                     LocalDateTime from, LocalDateTime to, Pageable pageable);

    ReservationResponse getById(Long id);

    Reservation getEntity(Long id);

    ReservationResponse create(CreateReservationRequest request);

    ReservationResponse update(Long id, UpdateReservationRequest request);

    ReservationResponse cancel(Long id, String reason);

    /** Passe en EXPIREE les reservations non honorees dont la periode est passee. */
    int expireStaleReservations();
}
