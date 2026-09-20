package com.example.parking.controller.rest;

import com.example.parking.dto.request.CreateReservationRequest;
import com.example.parking.dto.request.UpdateReservationRequest;
import com.example.parking.dto.response.PageResponse;
import com.example.parking.dto.response.ReservationResponse;
import com.example.parking.entity.enums.ReservationStatus;
import com.example.parking.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Reservation de places")
public class ReservationRestController {

    private final ReservationService reservationService;

    public ReservationRestController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    @Operation(summary = "Liste paginee des reservations (recherche, filtres, tri)")
    public ResponseEntity<PageResponse<ReservationResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime to,
            @PageableDefault(size = 20, sort = "expectedArrival", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<ReservationResponse> page = reservationService.search(search, status, userId, from, to, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'une reservation")
    public ResponseEntity<ReservationResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Cree une reservation (attribution automatique si aucune place fournie)")
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody CreateReservationRequest request) {
        ReservationResponse created = reservationService.create(request);
        return ResponseEntity.created(URI.create("/api/reservations/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifie une reservation confirmee")
    public ResponseEntity<ReservationResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateReservationRequest request) {
        return ResponseEntity.ok(reservationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Annule une reservation")
    public ResponseEntity<ReservationResponse> cancel(@PathVariable Long id,
                                                      @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(reservationService.cancel(id, reason));
    }

    @PostMapping("/expire-stale")
    @Operation(summary = "Passe en EXPIREE les reservations non honorees")
    public ResponseEntity<Integer> expireStale() {
        return ResponseEntity.ok(reservationService.expireStaleReservations());
    }
}
